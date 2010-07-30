package com.cmpp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.cmpp.stdout.ConsoleMonitor;


public class KeyInputListen extends Thread{

	private BufferedReader KeyInStream=null;
	private CMPPConnectMgr ccMgr=null;
	private ConsoleMonitor cm=null;
	public KeyInputListen(CMPPConnectMgr ccMgr,ConsoleMonitor cm){
		this.cm=cm;
		KeyInStream=new BufferedReader(new InputStreamReader(System.in ));
		this.ccMgr=ccMgr;
	}
	
	public void run() {
		String KeyStr="";
		cm.sout("已准备好接受键盘命令：９９９退出\n#");
		while (!KeyStr.equals("999")){
			try {
				sleep(100);
				KeyStr=KeyInStream.readLine() ;
				if (KeyStr.equals("999")){
					ccMgr.closeConnections();
				}else if (KeyStr.equals("998")){
					cm.sout("TOTAL="+Runtime.getRuntime().totalMemory()+","+Runtime.getRuntime().freeMemory());
				}else{
					cm.sout("未知命令：９９９退出");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			KeyInStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		cm.sout("已经退出");
	}
}
