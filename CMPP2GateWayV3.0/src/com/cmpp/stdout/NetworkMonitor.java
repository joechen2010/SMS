/**
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 */

package com.cmpp.stdout;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkMonitor extends Monitor {
	private String ip=null;
	private int port=0;
	
	private DatagramSocket clientSocket = null;
	private InetAddress addr = null;
	private byte[] cSendBuff=new byte[301];
	private DatagramPacket dp = null;
	private boolean enabled=false;
	
	//正常业务提交
	public static final int NM_MSGTYPE_CMPPSUBMIT_DETAIL=1;
	//群发
	public static final int NM_MSGTYPE_CMPPMULTISUBMIT_DETAIL=2;
	//SMC响应
	public static final int NM_MSGTYPE_CMPPSUBMITRSP=3;
	//用户上行
	public static final int NM_MSGTYPE_CMPPMO_DETAIL=4;
	//网关工作状态（响应检测LOOP）
	public static final int NM_MSGTYPE_STATUSLOOP=5;
	//即时SMS发送数据
	public static final int NM_MSGTYPE_CMPPREALSUBMIT_COUNT=6;
	//当前控速状态（系数）
	public static final int NM_MSGTYPE_CMPPSPEEDCTRL_LEVEL=7;
	//当前系统工作日志
	public static final int NM_MSGTYPE_TSP_LOG=8;
	
	public NetworkMonitor(String ip, int port) {
		this.ip = ip;
		this.port = port;
		//留作扩展
	}
	public void closeMe(){
		//留作扩展
	}
	public synchronized void sendStdLog(byte[] buff,int len,int cType){
		try {
			if (enabled) {
				for (int i=0;i<301;i++){
					cSendBuff[i]=0;
				}
				if (len>300){
					System.arraycopy(buff, 0, cSendBuff, 1, 300);
				}else{
					System.arraycopy(buff, 0, cSendBuff, 1, len);
				}
				cSendBuff[0]=(byte)cType;
				System.out.println(new String(cSendBuff, "gb2312"));
			}
		} catch (Exception ex) {
		}
	}
	public synchronized void sendStdLog(String str,int cType) {
		try {
			System.out.println(str);
		} catch (Exception ex) {
		}
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
