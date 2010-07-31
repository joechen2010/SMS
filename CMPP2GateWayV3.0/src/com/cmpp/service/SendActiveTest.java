package com.cmpp.service;

import java.io.*;

import com.cmpp.common.*;
import com.cmpp.protocol.*;
import com.cmpp.stdout.ConsoleMonitor;


public class SendActiveTest extends Thread{

	private CMPPConnection iod_conn =null;
	private Cmpp2_ActiveTest ActiveTest=new Cmpp2_ActiveTest();
	private Seq_ID seq_id=null;
	private int Delay =0; //发送Active间隔单位毫秒
	private CMPPSocket cmppSocket=null;
	//private ConsoleMonitor cm=null;
	
	public SendActiveTest(CMPPSocket cmppSocket,CMPPConnection iod_conn,int Delay,Seq_ID seq_id,ConsoleMonitor cm){
		this.cmppSocket=cmppSocket;
		this.iod_conn=iod_conn;
		this.Delay=Delay;
		this.seq_id=seq_id;
		//this.cm=cm;
	}
	
	public  void run(){
		//cm.sout("["+iod_conn.getConnectionId()+"] SendActiveTest Start");
		while(Controller.isBRun() ){
			try {
				SendActive();
				sleep(Delay);
			} catch (Exception e){
				try{sleep(3000);}catch(InterruptedException e0){}
			}
		}
		//cm.sout("["+iod_conn.getConnectionId()+"] SendActiveTest End");
	}
	
	private void SendActive() throws IOException {
		if (iod_conn.isConnected()) {
			ActiveTest.Seq_ID = seq_id.GetSeqID();
			ActiveTest.send(cmppSocket);
		}
	}
}
