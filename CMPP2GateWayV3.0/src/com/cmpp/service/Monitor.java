package com.cmpp.service;

import com.cmpp.common.Controller;
import com.cmpp.stdout.NetworkMonitor;

public class Monitor extends Thread{
	private Cmpp_SocketRev cmpp=null;
	private NetworkMonitor ncm=null;
	public Monitor(Cmpp_SocketRev cmpp,NetworkMonitor ncm) {
		super();
		this.cmpp = cmpp;
		this.ncm=ncm;
	}
	
	public void run(){
		int t1=0,j=0;
		while (Controller.isBRun()) {
			if (j++>20){
				j=0;
				if (cmpp.getMonitorTimeCounter()==t1){
					// 重新连接
					if (cmpp.getConnectionStatus()) {
						ncm.sendStdLog("网关超时无反应,重启动...",NetworkMonitor.NM_MSGTYPE_TSP_LOG);
						cmpp.resetCMPP();
					}
				}
				t1=cmpp.getMonitorTimeCounter();
			}
			try{sleep(1000);}catch(InterruptedException e){}
		}
	}
	
}
