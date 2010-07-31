package com.cmpp.service;

import com.cmpp.tools.*;
import com.cmpp.common.Const;
import com.cmpp.common.Controller;
import com.cmpp.common.TCPWindows;
import com.cmpp.database.*;
import com.cmpp.protocol.*;
import com.cmpp.stdout.ConsoleMonitor;
import com.cmpp.stdout.NetworkMonitor;
import java.text.DecimalFormat;
public class SubmitBox extends Thread{
	private BoxStruct[] submitPool=new BoxStruct[Const.QUE_SUBMITRESP_LENGTH];
	private TCPWindows netwindows=null;
	private DS_SubmitRsp ds=null;
//	private ConsoleMonitor cm=null;
	private NetworkMonitor ncm=null;
//	private NetworkMonitor cmpp_ncm=null;
	private StringBuilder sbLog=null;
	private CMPPConnection iod_conn = null;
	private DecimalFormat rspFmt=null;
	public SubmitBox(CMPPConnection iod_conn,TCPWindows netwindows,DS_SubmitRsp ds,ConsoleMonitor cm,NetworkMonitor ncm
			/*,NetworkMonitor cmpp_ncm*/){
		this.ncm=ncm;
//		this.cmpp_ncm=cmpp_ncm;
		this.netwindows=netwindows;
		this.iod_conn=iod_conn;
		this.ds=ds;
		rspFmt=new DecimalFormat("000");
//		this.cm=cm;
		sbLog=new StringBuilder();
		for (int i=0;i<Const.QUE_SUBMITRESP_LENGTH;i++){
			submitPool[i]=new BoxStruct();
		}
	}
	
	public class BoxStruct{
		public String DBID="0";			//MT的记录编号
		public int nSendSeq=0;		//
		public byte nActionStatus=0;//动作状态
		public long nSMCMsgId=0;	//Msg_ID
		public int nSMCResult=0;	//发送状态
		public long nTime=0;		//
		public BoxStruct(){		
		}
	}
	
	public void CleanIndexBox(int nSeq){
		int nIndex=nSeq % Const.QUE_SUBMITRESP_LENGTH;
		submitPool[nIndex].nSMCMsgId=0;
		submitPool[nIndex].DBID="0";
		submitPool[nIndex].nSendSeq=0;
		submitPool[nIndex].nActionStatus=0;
		submitPool[nIndex].nTime=0;
	}
	public void PutIntoSubmitBox(String DBID,int nSeq){
		//boolean bLogShowed=false;
		int nIndex=nSeq % Const.QUE_SUBMITRESP_LENGTH;
		if (submitPool[nIndex].nActionStatus!=0){
			while (Controller.isBRun()){
				if (submitPool[nIndex].nActionStatus==2){
					//cm.sout("等待原数据被处理....................");
					/*
					if (!bLogShowed) {
						sbLog.delete(0, sbLog.length());
						sbLog.append("等待原数据被处理, SEQ:");
						sbLog.append(nSeq);
						sbLog.append(",ORISEQ:");
						sbLog.append(submitPool[nIndex].nSendSeq);
						sbLog.append(",DBID:");
						sbLog.append(submitPool[nIndex].DBID);
						ncm.sendStdLog(sbLog.toString(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						bLogShowed = true;
					}*/
					try{sleep(100);}catch (Exception e){}
				} else{
					break;
				}
			}//end while
			//bLogShowed=false;
			while (Controller.isBRun()) {
				if (submitPool[nIndex].nActionStatus == 1) {
					if (((System.currentTimeMillis()/1000)-submitPool[nIndex].nTime)>Const.CMPP_SUBMIT_TIMEOUT){
						setSubmitTimeOut(submitPool[nIndex].DBID,false);
						break;
					}else{
						try{sleep(100);}catch (Exception e){}
					}
				}else if (submitPool[nIndex].nActionStatus == 2){
					/*
					if (!bLogShowed) {
						sbLog.delete(0, sbLog.length());
						sbLog.append("等待原数据被处理(2), SEQ:");
						sbLog.append(nSeq);
						sbLog.append(",ORISEQ:");
						sbLog.append(submitPool[nIndex].nSendSeq);
						sbLog.append(",DBID:");
						sbLog.append(submitPool[nIndex].DBID);
						ncm.sendStdLog(sbLog.toString(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						bLogShowed = true;
					}*/
					try{sleep(100);}catch (Exception e){}
				}else{
					break;
				}
			}//end while
		}
		submitPool[nIndex].nSMCMsgId=0;
		submitPool[nIndex].DBID=DBID;
		submitPool[nIndex].nSendSeq=nSeq;
		submitPool[nIndex].nActionStatus=1;
		submitPool[nIndex].nTime=System.currentTimeMillis()/1000;
	}
	
	public void setSubmitTimeOut(String nDbID,boolean bIgoreMsgId){
		ds.UpdateSubmitResp(0,nDbID, 101,bIgoreMsgId);
	}
	
	public void UpdateSubmitBox(Cmpp2_SubmitResp SubmitResp, int Seq_ID) {
		long nMsgId = SubmitResp.getMsgId();
		if (SubmitResp.getResult()==8/* || SubmitResp.getResult()==250*/){
			if (!iod_conn.isBAutoSpeeedCtrl()){
				ncm.sendStdLog("SUBMIT RSP: 发送过快，开始控速...",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				//(wangzm 20100719，恢复速度控制)
				iod_conn.setBAutoSpeeedCtrl(true);
			}
		}
		int nIndex = Seq_ID % Const.QUE_SUBMITRESP_LENGTH;
		if (submitPool[nIndex].nSendSeq!=Seq_ID){
			sbLog.delete(0, sbLog.length());
			sbLog.append("超时被占用,SEQ:");
			sbLog.append(Seq_ID);
			sbLog.append(",ORISEQ:");
			sbLog.append(submitPool[nIndex].nSendSeq);
			ncm.sendStdLog(sbLog.toString(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			//cm.sout("超时被占======================================");
			return;
		}
		
		if (submitPool[nIndex].nActionStatus!=1){
			//cm.sout("错误状态======================================");
			//此条数据返回太迟，档位已被清空，放弃。如此类情况发生较多，则应加大超时的值（CMPP_SUBMIT_TIMEOUT）
			sbLog.delete(0, sbLog.length());
			sbLog.append("超时数据返回：,SEQ:");
			sbLog.append(Seq_ID);
			sbLog.append(",ORISEQ:");
			sbLog.append(submitPool[nIndex].nSendSeq);
			sbLog.append(",ACTIONSTATUS:");
			sbLog.append(submitPool[nIndex].nActionStatus);
			ncm.sendStdLog(sbLog.toString(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			return;
		}
		String nDBID=submitPool[nIndex].DBID;
		int nResult=SubmitResp.getResult();
		submitPool[nIndex].nSMCMsgId=nMsgId;
		submitPool[nIndex].nSMCResult=nResult;
		submitPool[nIndex].nActionStatus=2;
		if (nResult==8 /*|| nResult==250*/){
			setSubmitTimeOut(nDBID,true);
		}
		int nWinSize=netwindows.changeWindow(-1);//滑动窗口减1
		sbLog.delete(0, sbLog.length());
		sbLog.append(Seq_ID);
		sbLog.append(",[");
		sbLog.append(nDBID);
		sbLog.append("],");
		sbLog.append(rspFmt.format(nResult));
		sbLog.append(",");
		sbLog.append(nIndex);
		sbLog.append(",");
		sbLog.append(nWinSize);
//		cmpp_ncm.sendStdLog(sbLog.toString(),1);
		ncm.sendStdLog(sbLog.toString(),NetworkMonitor.NM_MSGTYPE_CMPPSUBMITRSP);
		/*
		if (nWinSize>=12){
			iod_conn.setSubmitDelay(100);
		}else if (nWinSize<11){
			iod_conn.setSubmitDelay(0);
		}*/
	}

	public void run() {
		int nRecordcount=0;
		StringBuilder sb=new StringBuilder();
		byte[] bNCMMonitorBuff=new byte[4];
		int i=0,j=Const.QUE_SUBMITRESP_LENGTH/2;
		while (Controller.isBRun()) {
			nRecordcount=0;
			for (i = 0; i < Const.QUE_SUBMITRESP_LENGTH; i++) {
				//报告检测进度(每秒1次)
				if (i==j){
					System.arraycopy(Tools.int2byte(i), 0, bNCMMonitorBuff, 0, 4);
					ncm.sendStdLog(bNCMMonitorBuff, 4, NetworkMonitor.NM_MSGTYPE_STATUSLOOP);
				}
				if (submitPool[i].nActionStatus == 2) {
					nRecordcount++;
					// 保持与传进来的的msgid一致
					ds.UpdateSubmitResp(submitPool[i].nSMCMsgId,submitPool[i].DBID, submitPool[i].nSMCResult,false);
					submitPool[i].nActionStatus=0;
					submitPool[i].nSMCMsgId=0;
					submitPool[i].nSendSeq=0;
					submitPool[i].nTime=0;
					
				}
			}
			//报告检测结果
			if (nRecordcount > 0) {
				System.arraycopy(Tools.int2byte(nRecordcount), 0,
						bNCMMonitorBuff, 0, 4);
				ncm.sendStdLog(bNCMMonitorBuff, 4,NetworkMonitor.NM_MSGTYPE_CMPPREALSUBMIT_COUNT);
			}
			try {sleep(1000);}catch (Exception e){};
		}
		ncm.sendStdLog("作业线程退出中,正在作处理.....", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		nRecordcount=0;
		//处理超时的项目
		for (i = 0; i < Const.QUE_SUBMITRESP_LENGTH; i++) {
			if (submitPool[i].nActionStatus == 1) {
				nRecordcount++;
				setSubmitTimeOut(submitPool[i].DBID,false);
				submitPool[i].nActionStatus=0;
			}
		}
		Controller.setDbCanClose(true);
		sb.append("作业线程线程已退出,处理项：");
		sb.append(nRecordcount);
		ncm.sendStdLog(sb.toString(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		//cm.sout("更新线程已退出");
	}
	public void checkBoxTimeOut() {
		if (Controller.isBRun()) {
			ncm.sendStdLog("正在检查超时.....", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			long nCuttentTime=System.currentTimeMillis()/1000;
			for (int i = 0; i < Const.QUE_SUBMITRESP_LENGTH; i++) {
				if (submitPool[i].nActionStatus == 1) {
					if (nCuttentTime-submitPool[i].nTime>Const.CMPP_SUBMIT_TIMEOUT){
						//将超进数据放回数据库重发
						setSubmitTimeOut(submitPool[i].DBID,false);
						submitPool[i].nSMCMsgId=0;
						submitPool[i].nActionStatus = 0;
					}
				}
			}
			ncm.sendStdLog("正在检完成.", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		}
	}
}
