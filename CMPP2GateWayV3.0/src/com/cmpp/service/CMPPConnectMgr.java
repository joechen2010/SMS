/**
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 文件：CMPPConnectMgr.java
 * 功能：CMPP连接管理类
 */
package com.cmpp.service;
import java.util.*;

import com.cmpp.common.Controller;
import com.cmpp.common.ConfigBundle;
import com.cmpp.database.*;
import com.cmpp.protocol.CMPP2_Param;
import com.cmpp.stdout.ConsoleMonitor;
import com.cmpp.stdout.NetworkMonitor;


public class CMPPConnectMgr {
	private ArrayList<CMPPConnection> connPool=null;
	private CMPP2_Param cpp=null;
	
	private DS_Sumbit ds_submit=null;
	private DS_SubmitRsp ds_submitrsp=null;
	private DS_MOSMS ds_MOSMS=null;
	private ConsoleMonitor cm=null;
	private boolean bDBisOK=false;
	private NetworkMonitor ncm=null;
//	private NetworkMonitor cmpp_ncm=null;
	
	
	public CMPPConnectMgr (CMPP2_Param cpp,DS_Params dp,ConsoleMonitor cm){
		this.cpp=cpp;
		this.cm=cm;
		ncm=new NetworkMonitor(ConfigBundle.getString("MONITOR_IP"),Integer.parseInt(ConfigBundle.getString("MONITOR_PORT")));
		connPool=null;
		connPool=new ArrayList<CMPPConnection>();
		try{
			ds_submit=new DS_Sumbit(dp,cm,ncm);
			ds_submitrsp=new DS_SubmitRsp(dp,cm,ncm);
			ds_MOSMS=new DS_MOSMS(dp,cm,ncm);
			bDBisOK=true;
		}catch (ClassNotFoundException eClass){
			ncm.sendStdLog("没有找到类： "+eClass.getMessage(), NetworkMonitor.NM_MSGTYPE_TSP_LOG);
			dbRelease();
			return;
		}
	}
	private void dbRelease(){
		if (ds_submit!=null) ds_submit.closeDB();
		if (ds_submitrsp!=null) ds_submitrsp.closeDB();
		if (ds_MOSMS!=null) ds_MOSMS.closeDB();
		//cm.sout("已关闭所有数据库");
	}
	
	/**
	 * 创建一个CMPP服务
	 */
	public void createCMPP(){
		if (!bDBisOK) return;
		Thread t=null;
		CMPPConnection Conn = null;
		Conn=new CMPPConnection(cpp,ds_submit,ds_submitrsp,ds_MOSMS,cm,ncm);
		connPool.add(Conn);
		Conn.setConnectionId(connPool.size()-1);
		t=new Thread(Conn);
		t.setName("CONN_CMPP"+getConnectionNums());
		t.start();
	}

	public int getConnectionNums() {
		return connPool.size();
	}
	public void closeConnections(){
		Controller.setBRun(false);
		for (int i=0;i<connPool.size();i++){
			CMPPConnection conn = (CMPPConnection) connPool.get(i);
			conn.close();
			conn = null;
			connPool.remove(i);
		}
	//	cm.sout("已关闭所有连接");
		ncm.sendStdLog("已关闭所有连接", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		connPool.clear();
		//cm.sout("等待数据库关闭");
		ncm.sendStdLog("等待数据库关闭", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
		while (true) {
			if (Controller.getDbCanClose()) {
				dbRelease();
				break;
			}
			try{Thread.sleep(1000);}catch (Exception e){}
		}
		//等待其它工作退出
		try{Thread.sleep(5000);}catch (Exception e){}
		ncm.sendStdLog("网关全部关闭成功，可以关闭控制台", NetworkMonitor.NM_MSGTYPE_TSP_LOG);
//		cmpp_ncm.closeMe();
		ncm.closeMe();
		//cm.sout("等待数据已关闭");
	}
}
