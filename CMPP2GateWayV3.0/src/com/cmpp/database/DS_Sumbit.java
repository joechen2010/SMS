/**
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 文件：DS_Sumbit.java
 * 功能：CMPP_Submit数据库操作类
 */

package com.cmpp.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cmpp.common.ConfigBundle;
import com.cmpp.common.Controller;
import com.cmpp.protocol.Cmpp2_Submit;
import com.cmpp.service.CMPPException;
import com.cmpp.stdout.ConsoleMonitor;
import com.cmpp.stdout.NetworkMonitor;
import com.cmpp.tools.Tools;

public class DS_Sumbit extends CMPPDataSource {
	private NetworkMonitor ncm=null;
	private int nSendCountPerTime=0;	//每秒发送条数
	
	public DS_Sumbit(DS_Params dp,ConsoleMonitor cm,NetworkMonitor ncm) throws ClassNotFoundException {
		super(dp,cm);
		this.ncm=ncm;
		nSendCountPerTime=dp.getNBatchSendCount();
	}
	
	//如果为false，则是系统要求退出
	public boolean resetSubmitTable(){
		boolean bRet=false;
		String strSql = "update tb_SMS_CMPP_MTs set SendStatus=0 where SendStatus=2";
		while (Controller.isBRun()) {
			bRet=false;
			try {
				StmExec.executeUpdate(strSql);
				bRet=true;
				break;
			} catch (SQLException e) {
				if (Controller.isBRun()) {
					// cm.sout("数据库处于断开，正在重新连接：" + e.getMessage());
					ncm.sendStdLog("数据库处于断开，正在重新连接@(DS_Submit_resetSubmitTable)："
							+ e.getMessage(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					activeDB();
					// cm.sout("已连接发送数据库.");
					ncm.sendStdLog("已连接发送数据库@(DS_Submit_resetSubmitTable)：", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					try {Thread.sleep(2000);} catch (Exception ee) {}
					continue;
				} else {
					break;
				}
			}
		}//end while
		//cm.sout("Reset send table completed.");
		return bRet;
	}
	
	/*
	 * 一直阻塞，真到返回数据或系统要求退出，如是系统要求退出，则返回false
	 */
	public synchronized boolean SetSubmitData(Cmpp2_Submit Submit){
		String strMsgContent=null;
		String strWapHttp=null;
		String strWapContent=null;
		boolean bHaveNextRecord=false;
		boolean bRet=false;
		
		while (Controller.isBRun()) {
			bRet=false;
			if ((Res == null && !muti_flag) || (submitMap.size()==0 && muti_flag)) {
				if (!getSubmitDataFromDB()) {
					if (!Controller.isBRun()) {
						//系统要求退出,
						return false;
					}else{
						//非系统要求退出，则肯定为数据库操作失败，重新连接数据库
						try{if (Res != null) Res.close();Res=null;}catch (SQLException e0){}
						activeDB();
						if (!Controller.isBRun()) return false;
						//重连数据库后再重新刚才失败的操作
						continue;
					}
				}
				
			}
			try{
				if(muti_flag){
					getSubmitMutiDataMap();
					bHaveNextRecord = submitMap.size()>0 ? true : false ;
				}else{
					bHaveNextRecord=Res.next();
				}
			}catch (SQLException e0){
				try{if (Res != null) Res.close();Res=null;}catch (SQLException e1){}
				continue;
			}
			if (!bHaveNextRecord) { 
				try{if (Res != null) Res.close();Res=null;}catch (SQLException e1){}
				// 更改抓取群发阀门
				muti_flag = !muti_flag;
				continue;
			}
			
			cm.sout("找到数据，开始发送...");
			
			if(muti_flag){
				Cmpp2_Submit multiSubmit = null ;
				Iterator it = submitMap.entrySet().iterator();
				Map.Entry entry = null ;
				entry = (Map.Entry)it.next();
				multiSubmit = (Cmpp2_Submit)entry.getValue();
				String multiSubmitKey = entry.getKey().toString();
				try{
					Submit.setDBID(multiSubmit.getDBID());
					Submit.setSendSevel(multiSubmit.getSendSevel());
					Submit.setStatusReport(multiSubmit.getStatusReport());
					Submit.setService_ID(multiSubmit.getService_ID());
					Submit.setFee_UserType(multiSubmit.getFee_UserType());
					Submit.setFeeAddr(multiSubmit.getFeeAddr());
					Submit.setNDestAddrRealLen(multiSubmit.getNDestAddrRealLen());
					//Submit.setDestAddr(multiSubmit.getDestAddrStr().getBytes("ISO-8859-1"));
					Submit.setDestAddrStr(multiSubmit.getDestAddrStr());
					Submit.setNMsgType(multiSubmit.getNMsgType());
					Submit.setSP_ID(multiSubmit.getSP_ID());
					Submit.setFeeCode(multiSubmit.getFeeCode());
					Submit.setFeeType(multiSubmit.getFeeType());
					Submit.setOrgAddr(multiSubmit.getOrgAddr());
					// 标志本次submit为群发
					Submit.setMultiFlag(true);
					if(multiSubmit.getNMsgType()==1){
						if(multiSubmit.getStrMsgContent().length()>70){
							Submit.setMsg_Content(multiSubmit.getStrMsgContent().getBytes("iso-10646-ucs-2"),multiSubmit.getStrMsgContent());
						}else{
							Submit.setMsg_Content(multiSubmit.getStrMsgContent().getBytes("GB2312"),multiSubmit.getStrMsgContent());
						}
					}else{
						Submit.setWapPushHttp(multiSubmit.getStrWapPushHttp().getBytes("utf-8"),multiSubmit.getStrWapPushHttp());
						Submit.setWapPushContent(multiSubmit.getStrWapPushContent().getBytes("utf-8"),multiSubmit.getStrWapPushContent());
					}
					bRet=true;
					submitMap.remove(multiSubmitKey);
					break ;
				}catch(Exception e){
					ncm.sendStdLog("提取短信时发生其它错误@(DS_Submit_main)：" + e, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
					try{Thread.sleep(5000);}catch (InterruptedException er){}
				}
				return bRet;
			}
			
			
			// 本批为普通单发	
				
			try {
				Integer dbid = Res.getInt("ID");
				Submit.setDBID(dbid.toString());
				Submit.setSendSevel(Res.getInt("nSendLevel"));
				Submit.setStatusReport(Res.getByte("StatusReportFlag"));
				String strTemp = Res.getString("ServiceID");
				if (strTemp == null || strTemp.length() > 10
						|| strTemp.length() < 1) {
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <ServiceID> value,<ServiceID>="
									+ strTemp);
				}
				strTemp = strTemp.trim();
				Submit.setService_ID(strTemp.getBytes("ISO-8859-1"));
				Submit.setFee_UserType(Res.getByte("FeeUserType"));

				strTemp = Res.getString("FeeAddr");
				if (strTemp == null) {
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <FeeAddr> value,<FeeAddr>="
									+ strTemp);
				}
				strTemp = strTemp.trim();
				Submit.setFeeAddr(strTemp.getBytes("ISO-8859-1"));
				// System.out.println("FEEADDRLEN="+strTemp.length());

				strTemp = Res.getString("DestAddr");
				Submit.setNDestAddrRealLen(1);
				if (strTemp == null) {
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <DestAddr> value,<DestAddr>="
									+ strTemp);
				}
				strTemp = strTemp.trim();
				if(!Tools.checkMobile(strTemp.trim())){
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <DestAddr> value,<DestAddr>="
									+ strTemp);
				}
				Submit.setDestAddr(strTemp.getBytes("ISO-8859-1"));
				Submit.setDestAddrStr(strTemp);

				int nMsgType = Res.getByte("MsgType");
				Submit.setNMsgType(nMsgType);
				strTemp = Res.getString("SPID");
				strTemp = strTemp.trim();
				if (strTemp == null || strTemp.length() > 6) {
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <SPID> value,<SPID>="
									+ strTemp);
				}
				Submit.setSP_ID(strTemp.getBytes("ISO-8859-1"));

				strTemp = Res.getString("FeeCode");
				strTemp = strTemp.trim();
				if (strTemp == null) {
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <FeeCode> value,<FeeCode>="
									+ strTemp);
				}else if (strTemp.length() <6){
					for (int i=0;i<(6-strTemp.length());i++){
						strTemp="0"+strTemp;
					}
				}else if (strTemp.length()>6){
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <FeeCode> value,<FeeCode>="
									+ strTemp);
				}				
				Submit.setFeeCode(strTemp.getBytes("ISO-8859-1"));

				strTemp = Res.getString("FeeType");
				strTemp = strTemp.trim();
				if (strTemp == null) {
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp3_Submit Submit)]: Invalid <FeeType> value,<FeeType>="
									+ strTemp);
				}else if (strTemp.length()<2){
					strTemp="0"+strTemp;
				}else if (strTemp.length()>2){
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <FeeType> value,<FeeType>="
									+ strTemp);		
				}
				Submit.setFeeType(strTemp.getBytes("ISO-8859-1"));
				
				// 修改端口号
				strTemp = cm.getStrRegionId()+Res.getString("OrgAddr");
				if (strTemp == null || strTemp.length() < 1
						|| strTemp.length() > 21) {
					throw new CMPPException(
							"ERROR @[SetSubmitData(Cmpp2_Submit Submit)]: Invalid <OrgAddr> value,<OrgAddr>="
									+ strTemp);
				}
				strTemp = strTemp.trim();
				Submit.setOrgAddr(strTemp.getBytes("ISO-8859-1"));
				if (nMsgType == 1) {	//文本短信
					strMsgContent = Res.getString("UserData");
					if (strMsgContent == null) {
						strMsgContent = " ";
					} else {
						strMsgContent = strMsgContent.trim();
					}
					//Submit.setMsg_Content(strMsgContent.getBytes("GB2312"),strMsgContent);
					if(strMsgContent.length()>70){
						Submit.setMsg_Content(strMsgContent.getBytes("iso-10646-ucs-2"),strMsgContent);
					}else{
						Submit.setMsg_Content(strMsgContent.getBytes("GB2312"),strMsgContent);
					}
					int msg_fmt=Res.getInt("msg_fmt");
					String msg_port=Res.getString("msg_port");
					Submit.setMsg_fmt(0);
					if(msg_fmt==1){
						Submit.setMsg_fmt(msg_fmt);
						if(msg_port == null || "".equals(msg_port.trim())){
							msg_port = "1231" ;
						}
						Submit.setMsg_port((new Integer(msg_port).intValue()));
						Submit.setSend_port((new Integer(ConfigBundle.getString("SMC_PORT")).intValue()));
						//strMsgContent=ConfigBundle.getString("SMC_PORT")+"|"+msg_port+"|"+strMsgContent;
						Submit.setMsg_Content(strMsgContent.getBytes("iso-10646-ucs-2"),strMsgContent);
						/*if(strMsgContent.length()>70){
							Submit.setMsg_Content(strMsgContent.getBytes("iso-10646-ucs-2"),strMsgContent);
						}else{
							Submit.setMsg_Content(strMsgContent.getBytes("GB2312"),strMsgContent);
						}*/
					}
				} else {
					strWapHttp = Res.getString("WapPushHttp");
					strWapContent = Res.getString("WapPushContent");
					if (strWapHttp == null)
						strWapHttp = " ";
					else
						strWapHttp = strWapHttp.trim();
					if (strWapContent == null)
						strWapContent = " ";
					else
						strWapContent = strWapContent.trim();
					Submit.setWapPushHttp(strWapHttp.getBytes("utf-8"),
							strWapHttp);
					Submit.setWapPushContent(strWapContent.getBytes("utf-8"),
							strWapContent);
				}
				bRet=true;
				break;
			} catch (SQLException ex) {
				try{Thread.sleep(3000);}catch (InterruptedException er){}
				try{if (Res != null) Res.close();Res=null;}catch (SQLException e1){}
				ncm.sendStdLog("提取短信时发生SQL错误@(DS_Submit_main)：" + ex, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			} catch (Exception exo) {
				ncm.sendStdLog("提取短信时发生其它错误@(DS_Submit_main)：" + exo, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				try{Thread.sleep(5000);}catch (InterruptedException er){}
			}
		}//end while
		//cm.sout("-----------");
		return bRet;
	}

	/*
	 * 取得提交数据(如果失败或系统要求退出返回false,成功返回true
	 */
	private boolean getSubmitDataFromDB() {
		String strSql=null;
		int nFoundRecordCount=0;
		boolean bRet=false;
		while (Controller.isBRun()) {
			cm.sout("向数据库请求数据....");
			bRet=false;
			try{if (Res != null) Res.close();}catch (SQLException e0){}
			//取得新的数据，如果失败则直接返回false
			if(!muti_flag){
				strSql="select top "+Integer.toString(nSendCountPerTime)+" * from tb_SMS_CMPP_MTs with(nolock) where sms_multi=1 and (SendStatus=0 and SendCount<MaxSendCount) OR (SendResult=8 and sms_multi=1) order by nSendLevel DESC,ID ASC";
			}else {
				strSql="select top 99 * from tb_SMS_CMPP_MTs with(nolock) where SendStatus=0 and sms_multi=0 order by nSendLevel DESC,ID ASC,UserData ASC";
			}
			
			try{Res = StmSelect.executeQuery(strSql);}catch(SQLException e1){return false;}
			nFoundRecordCount=0;
			//更新数据状态为：已检取（２）
			//System.out.println("SQL="+strSql);
			try{Conn.setAutoCommit(false);}catch (SQLException e2){return false;}
			try {
				while (Res.next()) {	//
					nFoundRecordCount++;
					strSql = "update tb_SMS_CMPP_MTs set SendStatus=2 where Id="+ Res.getInt("ID");
					StmExec.executeUpdate(strSql);
				}
				Conn.commit();
				try{Conn.setAutoCommit(true);}catch (SQLException e3){return false;}
			} catch (SQLException exSQL) {
				ncm.sendStdLog("更新提交状态时发生SQL错误@(DS_Submit_main)：" + exSQL, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
				try{Conn.rollback();}catch (SQLException e4){}
				try{Conn.setAutoCommit(true);}catch (SQLException e5){}
				return false;
			}
			if (nFoundRecordCount>0){
				//发现有数据被找到
				try{Res.first();}catch (SQLException e5){return false;}
				try{Res.previous();}catch (SQLException e6){return false;}
				bRet=true;
				//记录集已准备好，退出函数
				cm.sout("已找到新的待发数据,开始提交发送....");
				break;
			}else{
				cm.sout("..................................");
				muti_flag = !muti_flag;
				//没有发现数据，等待５钟后再检查
				try{Thread.sleep(3000);}catch (InterruptedException er){}
			}
		}//end while
		return bRet;
	}
	
	
	
	
	/*
	 * 
	 * 群发组装 
	 * 
	 * */
	public void getSubmitMutiDataMap(){
		String strMsgContent=null;
		String strWapHttp=null;
		String strWapContent=null;
		if(Res == null) return  ;
		try{
			while(Res.next()){
				Cmpp2_Submit multiSubmit = null;
				String strTemp ;
				int nMsgType = Res.getByte("MsgType");
				if (nMsgType == 1) {
					//文本短信
					String userData = Res.getString("UserData");
					strTemp = Res.getString("DestAddr");
					Integer dbid = Res.getInt("ID");
					if (submitMap.size()>0) {
						try {
							multiSubmit =  submitMap.get(userData);
						} catch (Exception e) {
							multiSubmit = null;
						}
					}
					// 已保存到到map
					if(multiSubmit != null){
						if(strTemp != null && !strTemp.equals("")){
							if(!Tools.checkMobile(strTemp.trim())){
								cm.sout("ERROR @[SetSubmitMultiData]: 手机号码错误"+strTemp);
								continue ;
							}
							multiSubmit.setDestAddrStr(multiSubmit.getDestAddrStr()+","+strTemp);
							int destAddrRealLen = multiSubmit.getNDestAddrRealLen();
							destAddrRealLen = destAddrRealLen +1 ;
							multiSubmit.setNDestAddrRealLen(destAddrRealLen);
						}
						if(dbid != null){
							multiSubmit.setDBID(multiSubmit.getDBID()+","+dbid.toString());
						}
						submitMap.put(userData, multiSubmit);
					}else{
						multiSubmit = new Cmpp2_Submit();
						dbid = Res.getInt("ID");
						multiSubmit.setDBID(dbid.toString());
						multiSubmit.setSendSevel(Res.getInt("nSendLevel"));
						multiSubmit.setStatusReport(Byte.parseByte("0"));
						strTemp = Res.getString("ServiceID");
						if (strTemp == null || strTemp.length() > 10
								|| strTemp.length() < 1) {
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp2_Submit Submit)]: Invalid <ServiceID> value,<ServiceID>="
											+ strTemp);
						}
						strTemp = strTemp.trim();
						multiSubmit.setService_ID(strTemp.getBytes("ISO-8859-1"));
						multiSubmit.setFee_UserType(Res.getByte("FeeUserType"));
				
						strTemp = Res.getString("FeeAddr");
						if (strTemp == null) {
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp2_Submit Submit)]: Invalid <FeeAddr> value,<FeeAddr>="
											+ strTemp);
						}
						strTemp = strTemp.trim();
						multiSubmit.setFeeAddr(strTemp.getBytes("ISO-8859-1"));
						
						
						//处理手机号码
						multiSubmit.setNDestAddrRealLen(1);
						if (strTemp == null) {
							cm.sout("SetSubmitMultiData 群发出现空出现空手机号");
						}else{
							strTemp = strTemp.trim();
							multiSubmit.setDestAddrStr(strTemp);
						}
						multiSubmit.setNMsgType(nMsgType);
						strTemp = Res.getString("SPID");
						strTemp = strTemp.trim();
						if (strTemp == null || strTemp.length() > 6) {
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp2_Submit Submit)]: Invalid <SPID> value,<SPID>="
											+ strTemp);
						}
						multiSubmit.setSP_ID(strTemp.getBytes("ISO-8859-1"));
						
						strTemp = Res.getString("FeeCode");
						strTemp = strTemp.trim();
						if (strTemp == null) {
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp2_Submit Submit)]: Invalid <FeeCode> value,<FeeCode>="
											+ strTemp);
						}else if (strTemp.length() <6){
							for (int i=0;i<(6-strTemp.length());i++){
								strTemp="0"+strTemp;
							}
						}else if (strTemp.length()>6){
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp2_Submit Submit)]: Invalid <FeeCode> value,<FeeCode>="
											+ strTemp);
						}				
						multiSubmit.setFeeCode(strTemp.getBytes("ISO-8859-1"));
						
						strTemp = Res.getString("FeeType");
						strTemp = strTemp.trim();
						if (strTemp == null) {
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp3_Submit Submit)]: Invalid <FeeType> value,<FeeType>="
											+ strTemp);
						}else if (strTemp.length()<2){
							strTemp="0"+strTemp;
						}else if (strTemp.length()>2){
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp2_Submit Submit)]: Invalid <FeeType> value,<FeeType>="
											+ strTemp);		
						}
						multiSubmit.setFeeType(strTemp.getBytes("ISO-8859-1"));
						
						// 修改端口号
						strTemp = cm.getStrRegionId()+Res.getString("OrgAddr");
						if (strTemp == null || strTemp.length() < 1
								|| strTemp.length() > 21) {
							throw new CMPPException(
									"ERROR @[SetSubmitMultiData(Cmpp2_Submit Submit)]: Invalid <OrgAddr> value,<OrgAddr>="
											+ strTemp);
						}
						strTemp = strTemp.trim();
						multiSubmit.setOrgAddr(strTemp.getBytes("ISO-8859-1"));
						
						if (nMsgType == 1) {	//文本短信
							strMsgContent = Res.getString("UserData");
							if (strMsgContent == null) {
								strMsgContent = " ";
							} else {
								strMsgContent = strMsgContent.trim();
							}
							multiSubmit.setStrMsgContent(strMsgContent);
						} else {
							strWapHttp = Res.getString("WapPushHttp");
							strWapContent = Res.getString("WapPushContent");
							if (strWapHttp == null)
								strWapHttp = " ";
							else
								strWapHttp = strWapHttp.trim();
							if (strWapContent == null)
								strWapContent = " ";
							else
								strWapContent = strWapContent.trim();
							multiSubmit.setStrWapPushHttp(strWapHttp);
							multiSubmit.setStrWapPushContent(strWapContent);
					    }
						
						submitMap.put(userData, multiSubmit);
					}
				}
			}
		}catch(Exception e){
			try{Thread.sleep(3000);}catch (InterruptedException er){}
			try{if (Res != null) Res.close();Res=null;}catch (SQLException e1){}
			ncm.sendStdLog("组装群发数据时发生错误@(SetSubmitMultiData )：" + e, NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		}
	}	
		
	
    /**
     * 得到号码的个数
     * @param strMobileList
     * @return
     */
    public int GGetMobileCount(String strMobileList)
    {
    	if(strMobileList==null)return 0;
    	strMobileList=strMobileList.trim();
	    if(strMobileList.equals(""))return 0;	
        String arr[] = strMobileList.split(",");
        return arr.length;
    }
    
    public boolean isNumeric(String str)
    {
        if(str == null || str.trim().equals(""))
        {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

}
