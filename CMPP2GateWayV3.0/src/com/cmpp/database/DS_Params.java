/**
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 文件：DS_Params.java
 * 功能：数据源(DataSource)参数类
 */
package com.cmpp.database;

public class DS_Params {
	private String host;
	private String userName;
	private String password;
	private String dbName;
	private int nDbType=0;
	private int nBatchSendCount=0;	//批量发送数量(每次从发送表中获取的MT记录数)

	public int getNBatchSendCount() {
		return nBatchSendCount;
	}

	public void setNBatchSendCount(int batchSendCount) {
		nBatchSendCount = batchSendCount;
	}

	public DS_Params(String host, String userName, String password, String dbName, int dbType,int nBatchSendCount) {
		this.host = host;
		this.userName = userName;
		this.password = password;
		this.dbName = dbName;
		nDbType = dbType;
		this.nBatchSendCount=nBatchSendCount;
	}
	
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getNDbType() {
		return nDbType;
	}
	public void setNDbType(int dbType) {
		nDbType = dbType;
	}
}
