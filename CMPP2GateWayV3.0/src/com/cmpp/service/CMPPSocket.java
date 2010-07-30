/*
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 */
package com.cmpp.service;
import java.net.*;
import java.io.*;

public class CMPPSocket {
	private Socket socket=null;
	private String strHost=null;
	private int nPort=0;
	private boolean connected=false;
	public CMPPSocket(String strHost, int nPort) throws UnknownHostException {
		this.strHost=strHost;
		this.nPort=nPort;
	}
	public boolean isConnected(){
		return connected;
	}
	public synchronized void open() throws IOException{
		if (socket!=null){
			if (socket.isConnected()){
				socket.close();
			}
			socket=null;
		}
		socket=new Socket(strHost,nPort);
		socket.setTcpNoDelay(true);
		socket.setSoTimeout(10000);
		connected=true;
	}
	public InputStream getInputStream() throws IOException{
		return socket.getInputStream();
	}
	public OutputStream getOutputStream() throws IOException{
		return socket.getOutputStream();
	}
	public synchronized void close() throws IOException{
		if (socket!=null){
				socket.close();
				socket=null;
			}
		connected=false;
	}
	public synchronized void send(byte[] buff) throws IOException{
		OutputStream op=socket.getOutputStream();
		op.write(buff);
		op.flush();
	}
	public synchronized void send(byte[] buff,int offset,int len) throws IOException{
		OutputStream op=socket.getOutputStream();
		op.write(buff, offset, len);
		op.flush();
	}
}
