/*
 * 创建日期 2007-2-4
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 */
package com.cmpp.database;
import java.sql.*;

import com.cmpp.common.ConfigBundle;
import com.cmpp.stdout.ConsoleMonitor;
import com.cmpp.stdout.NetworkMonitor;
import com.cmpp.tools.RequestUrl;
//import com.cmpp.tools.Tools;

public class DS_MOSMS extends CMPPDataSource {
	private NetworkMonitor ncm=null;
//	private NetworkMonitor cmpp_ncm=null;
	private StringBuilder sbLog=null;
	public DS_MOSMS(DS_Params dp,ConsoleMonitor cm,NetworkMonitor ncm/*,NetworkMonitor cmpp_ncm*/) throws ClassNotFoundException{
		super(dp,cm);
		this.ncm=ncm;
//		this.cmpp_ncm=cmpp_ncm;
		sbLog=new StringBuilder();
	//	ncm=new NetworkMonitor("127.0.0.1",7002);
	}
	
	public synchronized void insertMO_ToDB(long nMsgId, String DestAddr,
			String SrcAddr, String MsgContent,String ServiceId,int nMsgFmt,int nMsgLen,String strLinkId){
		String lSrcAddr=SrcAddr.trim();
		
		if (lSrcAddr.length()>13) {
			lSrcAddr=lSrcAddr.substring(0,13);
			System.out.println("不正常的手机号码:"+SrcAddr);
			return;
		}
		// 修改 JECON 2010-7-26端口号
		String lDestAddr=DestAddr.trim();
		if(lDestAddr.startsWith(cm.getStrRegionId())){
			lDestAddr = lDestAddr.substring(cm.getStrRegionId().length());
		}
		String lServiceId=ServiceId.trim();
		if (lServiceId.length()<1){
			lServiceId="--";
		}
		CallableStatement proc = null;
		sbLog.delete(0, sbLog.length());
		//sbLog.append(nMsgId);
		sbLog.append("服务号:");
		sbLog.append(lDestAddr);
		sbLog.append(",业务代码:");
		sbLog.append(lServiceId);
		sbLog.append(",用户号：");
		sbLog.append(lSrcAddr);
		sbLog.append(",内容：");
		sbLog.append(MsgContent);
		sbLog.append("|其它：");
		sbLog.append(nMsgFmt);
		sbLog.append("(");
		sbLog.append(nMsgLen);
		sbLog.append(")||LINKID=");
		sbLog.append(strLinkId);
		//cm.sout(sbLog.toString());
		//cm.sout(DestAddr);
		
//		cmpp_ncm.sendStdLog(sbLog.toString(), 2);
		ncm.sendStdLog(sbLog.toString(), NetworkMonitor.NM_MSGTYPE_CMPPMO_DETAIL);
		String sMsgIDTemp;
		//add by wangzm 2010-07-27 收到上行时，先根据号码匹配地址及端口再请求
		if (Conn==null){
			activeDB();
		}
		String localServerIp="";
		int localServerPort=80;
		String userMobile="";
		if(lSrcAddr.length()>11){
			userMobile=lSrcAddr.substring(2);
		}else{
			userMobile=lSrcAddr;
		}
		String strSql="select top 1 * from SM_ROUTE where "+userMobile+">=frmnum and "+userMobile+"<=tonum";
		try {
			Res = StmSelect.executeQuery(strSql);
			if (Res.getRow() <=0)
				System.out.println("没有找到匹配的请求地址及端口");
			while (Res.next()) {
				localServerIp=Res.getString("localServerIp");
				localServerPort=Res.getInt("localServerPort");
			}
		} catch (SQLException e1) {
			System.out.println("获取请求地址及端口失败：" + "/n" + e1);
		}	

		if(localServerIp != null && !"".equals(localServerIp)){
			String urlString="http://"+localServerIp+":"+localServerPort+"/sys/aspx/import.aspx?sqlname=imp_sms" +
			"&mobile="+lSrcAddr+","+lDestAddr+"&smstext="+MsgContent.trim();
			System.out.println("上行时请求的地址为："+urlString);
			int request_count=new Integer(ConfigBundle.getString("REQUEST_COUNT")).intValue();
			RequestUrl.toSendUrl(urlString, request_count);
		}
		
		for (int i = 0; i < 3; i++) {
			try {
				if (Conn==null){
					activeDB();
				}
				//对nMsgId进行编码，使得MT的MsgID与MTReport的MsgID一致（wangzm 2009-3-2）
				//sMsgIDTemp = Tools.DecodeCMPPMsgID(nMsgId);
				sMsgIDTemp = String.valueOf(nMsgId);
				nMsgId = Long.parseLong(sMsgIDTemp);
				//System.out.println("[@DS_MOSMS]sMsgIDTemp:" + sMsgIDTemp);
				proc = Conn.prepareCall("{call tsp_SMS_CMPP_MOs(?,?,?,?,?,?)}");
				proc.setLong(1, nMsgId);
				proc.setString(2, lDestAddr.trim());
				proc.setString(3, lSrcAddr);
				proc.setString(4, MsgContent.trim());
				proc.setString(5, lServiceId);
				proc.setString(6, strLinkId.trim());
				proc.executeUpdate();
				break;
			} catch (SQLException eSQL) {
				sbLog.delete(0, sbLog.length());
				sbLog.append("保存MO信息到数据库出错[ERR:");
				sbLog.append(eSQL.toString());
				sbLog.append(",服务号:");
				sbLog.append(lDestAddr);
				sbLog.append(",业务代码:");
				sbLog.append(lServiceId);
				sbLog.append(",用户号：");
				sbLog.append(lSrcAddr);
				sbLog.append(",内容：");
				sbLog.append(MsgContent);
				sbLog.append("|：");
				sbLog.append(nMsgFmt);
				sbLog.append("(");
				sbLog.append(nMsgLen);
				sbLog.append("): ]LINKID=");
				sbLog.append(strLinkId);
				sbLog.append(eSQL.toString());
				ncm.sendStdLog(sbLog.toString(),NetworkMonitor.NM_MSGTYPE_CMPPMO_DETAIL);
				//cm.sout("保存MO信息到数据库出错:"+eSQL.getMessage());
				//try {
				//	Thread.sleep(2000);
				//} catch (Exception e) {
				//}
				activeDB();
				continue;
			}
		}
	}
	public synchronized void insertStatusReport_ToDB(long nMsgId, String strUserNum,String strDestAddr,int nStatus) {
		
		String lstrUserNum=strUserNum.trim();
		if (lstrUserNum.length()>13) {
			lstrUserNum=lstrUserNum.substring(0,13);
			cm.sout("不正常的手机号码(Report):"+lstrUserNum);
			return;
		}
		
		//wangzm 2010-07-29 增加端口号参数传入
		String lstrDestAddr=strDestAddr.trim();
		if(lstrDestAddr.startsWith(cm.getStrRegionId())){
			lstrDestAddr = lstrDestAddr.substring(cm.getStrRegionId().length());
		}
		
		CallableStatement proc = null;
		//sbLog.delete(0, sbLog.length());
		//sbLog.append("收到状态报告:");
		//sbLog.append(sbLog.append(strUserNum));
		//sbLog.append("，状态:");
		//sbLog.append(nStatus);
		//ncm.sendStdLog(sbLog.toString(), NetworkMonitor.NM_MSGTYPE_CMPPMO_DETAIL);
		/*
		 * MsgId DestAddr SrcAddr IsReport MsgContent
		 */
		//cm.sout("收到状态报告：\n\n消息编号:\t"+nMsgId+"\n用户号码:\t"+lstrUserNum+"\n状    态:\t"+nStatus+"\n");
		//add by wangzm 2010-07-27 收到状态报告时，先根据号码匹配地址及端口再请求
		if (Conn==null){
			activeDB();
		}
		String localServerIp="";
		int localServerPort=80;
		String userMobile="";
		if(lstrUserNum.length()>11){
			userMobile=lstrUserNum.substring(2);
		}else{
			userMobile=lstrUserNum;
		}
		String strSql="select top 1 * from SM_ROUTE where "+userMobile+">=frmnum and "+userMobile+"<=tonum";
		try {
			Res = StmSelect.executeQuery(strSql);
			if (Res.getRow()<=0)
				System.out.println("没有找到匹配的请求地址及端口");
			while (Res.next()) {
				localServerIp=Res.getString("localServerIp");
				localServerPort=Res.getInt("localServerPort");
			}
		} catch (SQLException e1) {
			System.out.println("获取请求地址及端口失败：" + "/n" + e1);
		}	

		if(localServerIp != null && !"".equals(localServerIp)){
			String urlString="http://"+localServerIp+":"+localServerPort+"/sys/aspx/import.aspx?sqlname=imp_sms" +
			"&mobile="+lstrUserNum+","+lstrDestAddr+"&smstext="+nStatus;
			System.out.println("返回状态报告时请求的地址为："+urlString);
			int request_count=new Integer(ConfigBundle.getString("REQUEST_COUNT")).intValue();
			RequestUrl.toSendUrl(urlString, request_count);
		}
	
		for (int i = 0; i < 3; i++) {
			try {
				if (Conn==null){
					activeDB();
				}
				sbLog.append("保存收到状态报告信息到数据库出错[MsgID:"+nMsgId);
				proc = Conn.prepareCall("{call tsp_SMS_CMPP_MTReports(?,?,?)}");
				proc.setLong(1, nMsgId);
				proc.setString(2, lstrUserNum);
				proc.setInt(3, nStatus);
				proc.executeUpdate();
				break;
			} catch (SQLException eSQL) {
				sbLog.delete(0, sbLog.length());
				sbLog.append("保存收到状态报告信息到数据库出错[MsgID:");
				sbLog.append(nMsgId);
				sbLog.append(",用户号：");
				sbLog.append(lstrUserNum);
				sbLog.append(",状态：");
				sbLog.append(nStatus);
				sbLog.append("]");
				sbLog.append(eSQL.toString());
				ncm.sendStdLog(sbLog.toString(),NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				
				//cm.sout("保存收到状态报告信息到数据库出错:"+eSQL.getMessage());
				//ncm.sendStdLog("保存收到状态报告信息到数据库出错:"+eSQL.getMessage(),5);
				//try {
				//	Thread.sleep(2000);
				//} catch (Exception e) {
				//}
				activeDB();
				continue;
			}
		}
	}
}
