/*
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 */
package com.cmpp.database;

import java.sql.*;
import com.cmpp.stdout.ConsoleMonitor;
import com.cmpp.stdout.NetworkMonitor;
//import com.cmpp.tools.Tools;

public class DS_SubmitRsp extends CMPPDataSource {
	private NetworkMonitor ncm=null;
	public DS_SubmitRsp(DS_Params dp,ConsoleMonitor cm,NetworkMonitor ncm) throws ClassNotFoundException{
		super(dp,cm);
		this.ncm=ncm;
	}
	public synchronized void UpdateSubmitResp(long nMsgId,String nDbId,int nSendResult,boolean bIgoreMsgId){
		
		if (!isConnected()){
			activeDB();
			//cm.sout("已连接到更新数据库.");
			ncm.sendStdLog("已连接到更新数据库.", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		}
		String sql = null;
		if (!bIgoreMsgId){
			if (nMsgId > 0) {
				//屏蔽  保持SubmitResp的MsgID与状态报告一致
				//String MsgIDStr = Tools.DecodeCMPPMsgID(nMsgId);
				String MsgIDStr = String.valueOf(nMsgId);
				sql = "Update tb_SMS_CMPP_MTs Set msgid='" + MsgIDStr
						+ "',SendStatus=3,SendResult=" + nSendResult
						+ ",SendCount=SendCount+1,SendTime=datediff(second,'2004-1-1 0:0:0',getdate()) Where ID in ("
						+ nDbId+")";
			} else {
				sql = "Update tb_SMS_CMPP_MTs Set SendStatus=0,SendResult="
						+ nSendResult
						+ ",SendCount=SendCount+1,SendTime=datediff(second,'2004-1-1 0:0:0',getdate()) Where ID in ("
						+ nDbId+")";
			}
		}
		else{
			sql="update tb_SMS_CMPP_MTs Set SendStatus=0,SendResult=8,SendCount=0 Where ID="+ nDbId;
			/*sql = "Update tb_SMS_CMPP_MTs Set SendStatus=3,SendResult="
				+ nSendResult
				+ ",SendCount=SendCount+1,SendTime=datediff(second,'2004-1-1 0:0:0',getdate()) Where ID in ("
						+ nDbId+")";*/
		}
		//System.out.println("sql>>>>>>>>>>>:"+sql);
		for (int i = 0; i < 3; i++) {
			try {
				StmExec.executeUpdate(sql);
				break;
			} catch (SQLException eSQL) {
				//cm.sout("更新数据库时发生错误，重新连接数据库：" + eSQL.getMessage());
				ncm.sendStdLog("更新数据库时发生错误，重新连接数据库：" + eSQL.getMessage(),5);
				try {
					Thread.sleep(2000);
					activeDB();
					//cm.sout("已连接到更新数据库.");
					ncm.sendStdLog("已连接到更新数据库(重试).", 5);
				} catch (Exception e) {
					//cm.sout("TTTTTTTTTTTTTTTTTTTTTTTTTTT");
				}
				continue;
			}
		}
	}
}
