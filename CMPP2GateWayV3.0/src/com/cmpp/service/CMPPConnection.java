package com.cmpp.service;

import java.io.IOException;
import java.net.*;
import com.cmpp.common.*;
import com.cmpp.common.Const;
import com.cmpp.database.*;
import com.cmpp.protocol.*;
import com.cmpp.stdout.*;
import com.cmpp.tools.Tools;


public class CMPPConnection extends Thread {
//	private int ActiveTestTime = 10000; 
	private int nId = 0;
	
	public SubmitBox rspScan = null;
//	public SendActiveTest SendActive = null;
	public Cmpp_SocketRev SocketRev = null;
	private Seq_ID seq_id = null;
	private Seq_ID nSubmit_Seq=null;
	private TCPWindows tcpWnd = null;
	private DS_Sumbit ds_submit=null;
	private CMPPSocket cmppSocket=null;
	private CMPP2_Param cpp=null;
	private ConsoleMonitor cm=null;
	private NetworkMonitor ncm=null;
//	private StringBuilder sbLog=null;
//	private NetworkMonitor cmpp_ncm=null;
//	private int dynamicWinWidth=0;
	private boolean bCMPPConnected=false;
//	private TCPWindowMgr tcpWndMgr=null;
	private int nSubmitDelay=0;
	private boolean bAutoSpeeedCtrl=false;
	public CMPPConnection(CMPP2_Param cpp, DS_Sumbit ds_submit,
			DS_SubmitRsp ds_submitrsp, DS_MOSMS ds_MOSMS,ConsoleMonitor cm,NetworkMonitor ncm/*,NetworkMonitor cmpp_ncm*/) {
		this.cm=cm;
		this.ncm=ncm;
//		this.cmpp_ncm=cmpp_ncm;
		this.cpp = cpp;
//		sbLog=new StringBuilder();
		this.ds_submit = ds_submit;
		//建立与ISMG的SOCKET连接
		try {
			cmppSocket = new CMPPSocket(cpp.getSmcIP(), cpp.getNSMCPort());
		} catch (UnknownHostException ex) {
			cm.sout("找不到主机名，当前连接已禁止");
			ncm.sendStdLog("找不到主机名，当前连接已禁止", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			return;
		}
		
		tcpWnd = new TCPWindows(cpp.getTcpWindowsSize());
		seq_id = new Seq_ID();
		nSubmit_Seq=new Seq_ID();
		rspScan = new SubmitBox(this,tcpWnd, ds_submitrsp,cm,ncm);
//		SendActive = new SendActiveTest(cmppSocket, this, ActiveTestTime,seq_id,cm);
		SocketRev = new Cmpp_SocketRev(tcpWnd,cmppSocket, this, rspScan,ds_MOSMS,cm,ncm);
		SocketRev.setName("RCV_T");
//		SendActive.setName("ACTIVE_T");
//		tcpWndMgr=new TCPWindowMgr(this,ncm);
		this.setName("SUBMIT_THREAD");
		nSubmitDelay=0;
		
	}
	public void setConnectionId(int nId) {
		this.nId = nId;
	}
	public int getConnectionId(){
		return nId;
	}
	public TCPWindows getTCPWindows(){
		return tcpWnd;
	}
	public boolean isConnected(){
		return bCMPPConnected;
	}
	public boolean isBAutoSpeeedCtrl() {
		return bAutoSpeeedCtrl;
	}
	public void setBAutoSpeeedCtrl(boolean autoSpeeedCtrl) {
		bAutoSpeeedCtrl = autoSpeeedCtrl;
		if (bAutoSpeeedCtrl){
			setSubmitDelay(100);
		}
	}
	public void close() {
		try {
			Logout();
			cmppSocket.close();
		} catch (IOException e) {
			cm.sout(e.getMessage());
		};
	}
	public void run() {
		try {
			startCMPP();
		} catch (Exception e) {
		}
	}
	public void setSubmitDelay(int n){
		if (n == 0) {
			nSubmitDelay = 0;
		} else {
			nSubmitDelay += n;
			if (nSubmitDelay > 1000) {
				nSubmitDelay = 1000;
			} else if (nSubmitDelay < 0) {
				nSubmitDelay = 0;
			}
		}
		ncm.sendStdLog(Tools.int2byte(1000 - nSubmitDelay), 4,
				NetworkMonitor.NM_MSGTYPE_CMPPSPEEDCTRL_LEVEL);
	}
	
	public void startCMPP(){
		tcpWnd.closeWindow();
		ds_submit.activeDB();
		ncm.sendStdLog("已连接发送数据库.", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		ds_submit.resetSubmitTable();
		ncm.sendStdLog("已复位数据库.", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		rspScan.start();
//		SendActive.start();
		SocketRev.start();
		try{Thread.sleep(3000);}catch (InterruptedException e0){}
		Cmpp2_Submit Submit = new Cmpp2_Submit(cmppSocket);
		int nSendSeq = 0,nTimeoutCheckCount=0;
		StringBuilder sb=new StringBuilder();
		int ntcpWndStatus=0;
		while (Controller.isBRun()) {
			/*if (bAutoSpeeedCtrl) {
				if (nSubmitDelay > 0) {
					try {
						Thread.sleep(nSubmitDelay);
					} catch (InterruptedException e0) {
					}
				}
			}*/
			//返回false，则表明是系经要求退出
			if (!ds_submit.SetSubmitData(Submit)) break;
			nSendSeq = nSubmit_Seq.GetSeqID();
			Submit.setSeq_ID(nSendSeq);
			//sleep(2000);
			nTimeoutCheckCount=0;
			// 申请滑动窗口
			while (Controller.isBRun()) {
				//cm.sout("申请窗口........");
				ntcpWndStatus = tcpWnd.changeWindow(1);
				if (ntcpWndStatus < 0) {
					//cm.sout("窗口不足........");
					if (ntcpWndStatus == -1) {
						nTimeoutCheckCount++;
						if (nTimeoutCheckCount > (Const.CMPP_SUBMIT_TIMEOUT * 101)) {
							nTimeoutCheckCount = 0;
							try{cmppSocket.close();}catch (IOException e0){}
						}
					}else{
						nTimeoutCheckCount=0;
					}
					try{Thread.sleep(10);}catch (InterruptedException e0){}
					continue;
				}
				//成功申请到窗口，退出
				break;
			}
			rspScan.PutIntoSubmitBox(Submit.getDBID(), nSendSeq);
			if (Submit.getNMsgType()==1){
				sb.delete(0, sb.length());
				String str = "" ;
				if(Submit.getMsg_fmt()==1){
					str = "端口";
				}
				sb.append(str+"短消息:");
				sb.append(nSendSeq);
				sb.append(",");
				sb.append("["+Submit.getDBID()+"]");
				sb.append(",");
				try{sb.append((new String(Submit.getOrgAddr(),"ISO-8859-1")).trim());}catch(Exception e0){tcpWnd.changeWindow(-1);continue;}
				sb.append(",");
				try{sb.append("["+Submit.getDestAddrStr().trim()+"]");}catch(Exception e0){tcpWnd.changeWindow(-1);continue;}
				sb.append(",");
				try{sb.append((new String(Submit.getService_ID(),"ISO-8859-1")).trim());}catch(Exception e0){tcpWnd.changeWindow(-1);continue;}
				sb.append(",");
				sb.append(Submit.getMsg_Content());
			}else {
				sb.delete(0, sb.length());
				sb.append("WAP PUSH:");
				sb.append(nSendSeq);
				sb.append(",");
				sb.append(Submit.getDBID());
				sb.append(",");
				try{sb.append((new String(Submit.getOrgAddr(),"ISO-8859-1")).trim());}catch(Exception e0){tcpWnd.changeWindow(-1);continue;}
				sb.append(",");
				try{sb.append(new String(Submit.getDestAddr(), "ISO-8859-1").trim());}catch(Exception e0){tcpWnd.changeWindow(-1);continue;}
				sb.append(",");
				try{sb.append((new String(Submit.getService_ID(),"ISO-8859-1")).trim());}catch(Exception e0){tcpWnd.changeWindow(-1);continue;}
				sb.append(",");
				sb.append(Submit.getWapPushHttp()+"|"+Submit.getWapPushContent());	
			}
//			System.out.println("Send msg");
			if(Submit.getSendSevel()==0){
//				cmpp_ncm.sendStdLog(sb.toString(),0);
				ncm.sendStdLog(sb.toString(),NetworkMonitor.NM_MSGTYPE_CMPPMULTISUBMIT_DETAIL);
			}else{
//				cmpp_ncm.sendStdLog(sb.toString(),11);
				ncm.sendStdLog(sb.toString(),NetworkMonitor.NM_MSGTYPE_CMPPSUBMIT_DETAIL);
			}
			//cm.sout(strStdOut);
			try {Submit.Submit();} catch (IOException e){
				rspScan.CleanIndexBox(nSendSeq);
				ncm.sendStdLog("提交短信失败:"+e, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				rspScan.setSubmitTimeOut(Submit.getDBID(),false);
				tcpWnd.changeWindow(-1);
			}
		}//end while
		try{Logout();}catch (IOException e0){}
		ncm.sendStdLog("CMPP连接已经结束", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
	}
	//连接SMC，直到成功或系统要求退出
	public synchronized boolean connectToSMC() {
		//cm.sout("["+nId+"]正在连接网关("+cpp.getSmcIP()+").....");
		Cmpp2_Connect Connect = null;
		boolean bRet=false;
		try{if (bCMPPConnected) Logout();}catch(Exception e){};
		seq_id.resetSeq();
		while (Controller.isBRun()) {
			ncm.sendStdLog("["+nId+"]正在连接网关("+cpp.getSmcIP()+").....", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			bRet=false;
			if (Connect!=null) Connect=null;
			try{cmppSocket.close();}catch (IOException e0){}
			try{cmppSocket.open();}catch (IOException e1){
				try {Thread.sleep(3000);} catch (InterruptedException ec) {}
				continue;
			}
			Connect = new Cmpp2_Connect(cmppSocket, cpp, seq_id.GetSeqID());
			try {
				Connect.send();
				Connect.getLoginResult();
				bRet=true;
				bCMPPConnected = true;
				ncm.sendStdLog("[" + nId + "]成功登录到SMC", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				break;
			} catch (IOException e2) {
				ncm.sendStdLog("[" + nId + "]登录到SMC失败:"+e2.toString(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				try {Thread.sleep(60000);} catch (InterruptedException ec) {}
				continue;
			}
		}//end while
		return bRet;
	}

	public void Logout() throws IOException {
		Logout logout = new Logout();
		logout.Seq_ID = seq_id.GetSeqID();
		logout.send(cmppSocket);
		bCMPPConnected=false;
	}
	public CMPP2_Param getCpp() {
		return cpp;
	}

}
