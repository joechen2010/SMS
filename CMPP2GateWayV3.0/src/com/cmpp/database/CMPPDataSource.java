/**
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 文件：CMPPDataSource.java
 * 功能：CMPP数据库操作类
 */

package com.cmpp.database;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.cmpp.common.Controller;
import com.cmpp.protocol.Cmpp2_Submit;
import com.cmpp.stdout.ConsoleMonitor;


public class CMPPDataSource {
	protected String DBConnStr = ""; 	// 数据库连接字符串
	protected Connection Conn = null;
	protected Statement StmSelect = null;
	protected Statement StmExec = null;
	protected ResultSet Res = null;
	protected boolean bConnected=false;
	protected DS_Params dp=null;
	protected ConsoleMonitor cm= null;
	//取数据标志
	boolean muti_flag = false;
	Map<String,Cmpp2_Submit> submitMap = new HashMap<String, Cmpp2_Submit>();
	
	public CMPPDataSource(DS_Params dp,ConsoleMonitor cm) throws ClassNotFoundException {
		this.dp=dp;
		this.cm=cm;
		if (dp.getNDbType()==1){
			DBConnStr="jdbc:jtds:sqlserver://"+dp.getHost()+";DatabaseName="+dp.getDbName()+";tds=8.0;lastupdatecount=true";
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		}
	}
	public boolean isConnected(){
		return bConnected;
	}
	public void activeDB(){
		while (Controller.isBRun()){
			if (!connectToDB()){
				try{
				Thread.sleep(3000);
				}catch (Exception e){}
				continue;
			}
			break;
		}
		//cm.sout("成功连接到数据库");
		if (!Controller.isBRun())
			closeDB();
	}

	public void closeDB() {
		bConnected = false;
		for (int i = 0; i < 4; i++) {
			try {
				if (Res != null) {
					Res.close();
					Res = null;
				}
				if (StmSelect != null) {
					StmSelect.close();
					StmSelect = null;
				}
				if (StmExec != null) {
					StmExec.close();
					StmExec = null;
				}
				if (Conn != null)
					Conn.close();
				break;
			} catch (SQLException e) {
				continue;
			}
		}
	}
	/*
	 * 连接数据库
	 */
	private boolean connectToDB(){
		closeDB();
		try {
			Conn = DriverManager.getConnection(DBConnStr, dp.getUserName(), dp.getPassword());
			Conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			StmSelect = Conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			StmExec = Conn.createStatement();
			Conn.setAutoCommit(true);
			//Conn.setAutoCommit(false);
			bConnected=true;
		} catch (SQLException ex) {
			cm.sout("ERROR @[connectToDB()]: " + ex.getMessage());
			bConnected=false;
		} 
		return bConnected;
	}
}
