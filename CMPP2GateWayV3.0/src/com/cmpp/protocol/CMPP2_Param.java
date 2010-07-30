/**
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 文件：CMPP2_Param.java
 * 功能：CMPP2参数类
 */
package com.cmpp.protocol;

public class CMPP2_Param {
	private String spid;		//企业代码
	private String password;	//登录密码
	private String smcIP;		//ISMG IP
	private int nSMCPort;		//ISMG端口
	private byte cmppVersion;	//
	private int tcpWindowsSize;	//滑动窗口大小
	
	public CMPP2_Param(String spid, String password, String smcIP, int port, byte cmppVersion, int tcpWindowsSize) {
		super();
		this.spid = spid;
		this.password = password;
		this.smcIP = smcIP;
		nSMCPort = port;
		this.cmppVersion = cmppVersion;
		this.tcpWindowsSize = tcpWindowsSize;
	}
	public byte getCmppVersion() {
		return cmppVersion;
	}
	public void setCmppVersion(byte cmppVersion) {
		this.cmppVersion = cmppVersion;
	}
	public int getNSMCPort() {
		return nSMCPort;
	}
	public void setNSMCPort(int port) {
		nSMCPort = port;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSmcIP() {
		return smcIP;
	}
	public void setSmcIP(String smcIP) {
		this.smcIP = smcIP;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public int getTcpWindowsSize() {
		return tcpWindowsSize;
	}
	public void setTcpWindowsSize(int tcpWindowsSize) {
		this.tcpWindowsSize = tcpWindowsSize;
	}
	
}
