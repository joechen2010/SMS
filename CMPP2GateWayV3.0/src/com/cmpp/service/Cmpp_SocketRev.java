package com.cmpp.service;
import java.io.*;
import java.net.SocketTimeoutException;
import com.cmpp.common.Const;
import com.cmpp.common.Controller;
import com.cmpp.common.TCPWindows;
import com.cmpp.database.DS_MOSMS;
import com.cmpp.protocol.*;
import com.cmpp.stdout.ConsoleMonitor;
import com.cmpp.stdout.NetworkMonitor;


public class Cmpp_SocketRev extends Thread {

	private CMPPConnection iod_conn = null;
	private SubmitBox submitBox = null;
	private DS_MOSMS ds_MOSMS=null;
	private CMPPSocket cmpp=null;
	private ConsoleMonitor cm=null;
	private Cmpp2_SubmitResp SubmitResp = null;
	private Cmpp2_Deliver Deliver = null;
	private Cmpp2_DeliverResp DeliverResp=null;
	private StringBuilder sbLog=null;
	private NetworkMonitor ncm=null;
	private TCPWindows tcpWnd = null;
	private DataInputStream cmppDIS=null;
	private Cmpp2_ActiveTestResp ActiveTestResp=null;
	private int monitorTimeCounter=0;
	private boolean bConnected=false;
	private Cmpp2_ActiveTest cmpp_keepalive=null;
	public Cmpp_SocketRev(TCPWindows tcpWnd,CMPPSocket cmpp,
			CMPPConnection iod_conn,SubmitBox submitBox,DS_MOSMS ds_MOSMS,ConsoleMonitor cm,NetworkMonitor ncm) {
		this.cmpp=cmpp;
		this.ncm=ncm;
		this.tcpWnd=tcpWnd;
		this.iod_conn = iod_conn;
		this.submitBox = submitBox;
		this.ds_MOSMS = ds_MOSMS;
		this.cm = cm;
		SubmitResp = new Cmpp2_SubmitResp();
		Deliver=new Cmpp2_Deliver();
		DeliverResp = new Cmpp2_DeliverResp();
		sbLog=new StringBuilder();
		ActiveTestResp=new Cmpp2_ActiveTestResp();
		cmpp_keepalive=new Cmpp2_ActiveTest();
	}
	
	//得取接收流，直到成功或系统要求退出。如果连接未建立，则自动建立CMPP连接
	//当第一次连接或网络出错重登录时调用
	private boolean getCmppInputStream(boolean bReconnectSMC){
		boolean bRet=false;
		//关闭窗口，阻止数据进入
		//ncm.sendStdLog("关闭窗口...", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		tcpWnd.closeWindow();
		if (bReconnectSMC){
			submitBox.checkBoxTimeOut();
			if (!iod_conn.connectToSMC()) return false;
		}
		//得取接收流，直到成功或系统要求退出
		while (Controller.isBRun()) {
			bRet=false;
			//关闭流
			if (cmppDIS!=null){try{cmppDIS.close();cmppDIS=null;}catch (IOException e0){}}
			//取得流
			try {cmppDIS = new DataInputStream(cmpp.getInputStream());bRet=true;break;} catch (IOException e) {
				try{cmppDIS.close();}catch (IOException e0){}
				// cm.sout("取得接收流异常:" + e);
				sbLog.delete(0, sbLog.length());
				sbLog.append("取得接收流异常:");
				sbLog.append(e.toString());
				ncm.sendStdLog(sbLog.toString(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				//重新连接SMC，直到成功或系统要求退出
				if (!iod_conn.connectToSMC()) break;
			}
		}//end while
		return bRet;
	}
	public void run() {
		Cmpp2_Head Head = new Cmpp2_Head();
		byte[] buffOther=new byte[1];
		int nDataLen=0;
		boolean bNeedReConnectSMC=false;
		boolean ISMG_OK=true;
		int ch1=0;
		while (Controller.isBRun()) {
			if (!getCmppInputStream(true)) break;
			bConnected=true;
			ISMG_OK=true;
			tcpWnd.resetTcpWindow();
			
			//开始循环收取
			while (Controller.isBRun() && bConnected) {
				bNeedReConnectSMC=false;
//				monitorTimeCounter=(int)(System.currentTimeMillis()/1000);
				
/*-----------------------------------------------------------------------------------------------*/				
				try{
					ch1=cmppDIS.read();
					//System.out.println(ch1);
					if (ch1<0){
						throw new EOFException();
					}
				}catch (SocketTimeoutException se){
					if (!ISMG_OK){
						ncm.sendStdLog("网关无响应，重启...",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						break;
					}
					ISMG_OK=false;
					try{
						cmpp_keepalive.send(cmpp);
					}catch(IOException ei){
						ncm.sendStdLog("Found error when keep-alive,reset cmpp..: "+ei,NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						break;
					}
					cm.sout("-----------------------------------------");
					continue;
				}catch (EOFException ee){
					ncm.sendStdLog("Disconnected from ISMG.",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					break;
				}catch(IOException eo){
					ncm.sendStdLog("Found error when recv cmpp-head,reset cmpp..: "+eo,NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					break;
				}catch(Exception e1){
					ncm.sendStdLog("收取未知数据出错(0).",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					cm.sout("错误标识:000000000");
					break;
				}
/*-----------------------------------------------------------------------------------------------*/	
				try {
			        int ch2 = cmppDIS.read();
			        //System.out.println(ch2);
			        int ch3 = cmppDIS.read();
			        //System.out.println(ch3);
			        int ch4 = cmppDIS.read();
			        //System.out.println(ch4);
			        if ((ch2 | ch3 | ch4) < 0)
			            throw new EOFException();
					Head.setTotal_Length((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
					Head.setCommand_ID(cmppDIS.readInt());
					Head.setSeq_ID(cmppDIS.readInt());
					if (Head.getTotal_Length()<12){
						ncm.sendStdLog("Bad cmpp packet,reset cmpp...",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						break;
					}
				}catch(SocketTimeoutException se){
					ncm.sendStdLog("Timeout when receive data from ISMG.",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					break;
				}catch (IOException e1){
					//网络出错，退出收循环，重新连接SMC
					ncm.sendStdLog("Disconnected when getting cmpp-header: "+e1,NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					break;
				}catch(Exception e2){
					//网络出错，退出收循环，重新连接SMC
					//System.out.println("字符数据异常，在读取返回头时.");
					ncm.sendStdLog("字符数据异常，在读取返回头时.",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					break;
				}
				if (Head.getTotal_Length() < 12) {
					ncm.sendStdLog("短信中心错误，可以是因为我方速度过快引起，开始自动控速...", 5);
					//iod_conn.setBAutoSpeeedCtrl(true);
					//流错位，退出收循环，重新连接SMC
					break;
				}
				switch (Head.getCommand_ID()){
				case Const.COMMAND_CMPP_SUBMIT_RESP:
					//System.out.println("EEEEEEEEEEEEEEEEE");
					
					if (!doSubmitResp(Head, cmppDIS)) {
						//cm.sout("处理SubmitRsp错.");
						ncm.sendStdLog("处理SubmitRsp错.",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						bNeedReConnectSMC=true;
					}
					break;
				case Const.COMMAND_CMPP_DELIVER:
					if (!doDeliver(Head.getTotal_Length(), Head, cmppDIS)) {
						//cm.sout("处理Deliver错.");
						ncm.sendStdLog("处理Deliver错.",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						bNeedReConnectSMC=true;
					}
					break;
				case Const.COMMAND_CMPP_ACTIVE_TEST:
					ncm.sendStdLog("A cmpp keep-alive test arrived.",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					if (!doKeepAliveResp(Head)){
						bNeedReConnectSMC=true;
					}
					break;
				default:
					if (Head.getCommand_ID()==Const.COMMAND_CMPP_ACTIVE_TEST_REP){
						ISMG_OK=true;
						cm.sout("****************************************");
					}else{
						ncm.sendStdLog("other packet:"+Head.getCommand_ID(),NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					}
					nDataLen = Head.getTotal_Length() - 12;
					if (nDataLen>1000){
						ncm.sendStdLog("rubbishing packet,reset cmpp...",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						bNeedReConnectSMC=true;
						break;
					}
					for (int i=0;i<nDataLen;i++){
						try{
							cmppDIS.read(buffOther, 0, 1);
							}catch (IOException e1){
							ncm.sendStdLog("数据错位，断开连接",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
							bNeedReConnectSMC=true;
							break;
						}catch(Exception e1){
							ncm.sendStdLog("收取未知数据出错.",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
							cm.sout("收取未知数据出错:0000000001");
							bNeedReConnectSMC=true;
							break;
						}
					}//end for
					System.gc();
					break;
				}
				// 网络出错，退出收循环，重新连接SMC
				if (bNeedReConnectSMC) break;
			}
			bConnected=false;
		}// end while
		if (cmppDIS != null) {try {cmppDIS.close();} catch (Exception e) {;}}
		//cm.sout("[" + iod_conn.getConnectionId() + "] 网关接收退出");
		ncm.sendStdLog("网关接收退出", 5);
	}
	
	private boolean doKeepAliveResp(Cmpp2_Head Head){
		ActiveTestResp.setSeq_ID(Head.getSeq_ID());
		try{
		ActiveTestResp.send(cmpp);
		return true;
		}catch (IOException e){
			cm.sout("doKeepAliveResp:"+e);
			return false;
		}catch(Exception e1){
			cm.sout("doKeepAliveResp Exc:"+e1);
			return false;
		}
	}
	
	public void resetCMPP(){
		bConnected=false;
		try{cmppDIS.close();cmpp.close();}catch (IOException e0){};		
	}
	public boolean getConnectionStatus(){
		return bConnected;
	}
	private boolean doSubmitResp(Cmpp2_Head Head, DataInputStream dis){
		try{
			SubmitResp.ReadIn(dis);
		}catch (IOException e){
			//cm.sout("处理SubmitResp时发生异常："+e);
			sbLog.delete(0, sbLog.length());
			sbLog.append("处理SubmitResp时发生异常:");
			sbLog.append(e.toString());
			ncm.sendStdLog(sbLog.toString(), 5);
			return false;
		}catch(ArrayIndexOutOfBoundsException e2){
			//网络出错，退出收循环，重新连接SMC
			cm.sout("异常1（非关键）");
			return false;
		}
		submitBox.UpdateSubmitBox(SubmitResp, Head.getSeq_ID()); // 写入等待SubmitResp的队列
		return true;
	}

	private boolean doDeliver(int PackLen, Cmpp2_Head Head, DataInputStream dis) {
		try {Deliver.ReadIn(dis);
		}catch (IOException e){
			ncm.sendStdLog("处理MO时出错:"+e, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			return false;
		}catch(ArrayIndexOutOfBoundsException e2){
				//网络出错，退出收循环，重新连接SMC
				ncm.sendStdLog("异常2（非关键）"+e2, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				return false;
		}
		String strSMSContent = null;
		try{
			if (Deliver.getNMsgType() == 0) {
				switch (Deliver.getNMsgFmt()) {
				case 8:
					strSMSContent = (new String(Deliver.getCMsgContent(),
							"iso-10646-ucs-2")).trim();
					break;
				case 15:
					strSMSContent = (new String(Deliver.getCMsgContent(), "GBK"))
							.trim();
					break;
				case 0:
					strSMSContent = (new String(Deliver.getCMsgContent(),
							"iso-8859-1")).trim();
					break;
				default:
					strSMSContent = (new String(Deliver.getCMsgContent(),
							"iso-8859-1")).trim();
					break;
				}
			ds_MOSMS.insertMO_ToDB(Deliver.getNMsgId(),
					(new String(Deliver.getCDestAddr())).trim(),
						(new String(Deliver.getCSrcAddr())).trim(), strSMSContent,"--",
						Deliver.getNMsgFmt(),Deliver.getNMsgLen(),"0");
			} else {
				//wangzm 2010-07-29 增加端口号参数传入
				ds_MOSMS.insertStatusReport_ToDB(Deliver.getNReportMsgId(),
						(new String(Deliver.getCDestAddr())).trim(),(new String(Deliver.getCSrcAddr())).trim(), Deliver
								.getNStatus());
			}
		} catch (Exception e) {
			//cm.sout("处理MO短时发生异常:" + e);
			sbLog.delete(0, sbLog.length());
			sbLog.append("处理MO短时发生异常:");
			sbLog.append(e.toString());
			ncm.sendStdLog(sbLog.toString()+e, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		}
		
			DeliverResp.setMsgID(Deliver.getNMsgId());
			DeliverResp.setSeq_ID(Head.getSeq_ID());
			DeliverResp.setResult((byte) 0);
		try {
			DeliverResp.send(cmpp);
			return true;
		} catch (Exception e) {
			//cm.sout("回应MORESP时发生异常:" + e);
			e.printStackTrace();
			sbLog.delete(0, sbLog.length());
			sbLog.append("回应MORESP时发生异常:");
			sbLog.append(e.toString());
			ncm.sendStdLog(sbLog.toString()+e, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			return false;
		}
	}
	public int getMonitorTimeCounter() {
		return monitorTimeCounter;
	}

	public void setMonitorTimeCounter(int monitorTimeCounter) {
		this.monitorTimeCounter = monitorTimeCounter;
	}
}
