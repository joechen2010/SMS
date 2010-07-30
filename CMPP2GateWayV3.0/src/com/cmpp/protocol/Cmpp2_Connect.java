package com.cmpp.protocol;
import java.io.*;
import java.security.MessageDigest;

import com.cmpp.common.CTime;
import com.cmpp.common.Const;
import com.cmpp.service.CMPPSocket;
import com.cmpp.tools.Tools;





public class Cmpp2_Connect {
	private int Total_Length;
	private int Command_ID;
	private int Seq_ID;
	private byte[] bSP_ID=new byte[6];
	private byte[] MD5Pwd=new byte[16];
	private int TimeStamp;
	private CMPPSocket cmpp=null;
	private CMPP2_Param cpp=null;
	public Cmpp2_Connect(CMPPSocket cmpp,CMPP2_Param cpp,int Seq_ID){
		this.cpp=cpp;
		this.cmpp=cmpp;
		this.Seq_ID=Seq_ID;
		String md5Str = "";
		String TimeStr = "";
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {}
		TimeStr = CTime.getTime(CTime.YYMMDDhhmmss);
		TimeStr = TimeStr.substring(2);
		md5Str = cpp.getSpid() + (char) 0x0 + (char) 0x0 + (char) 0x0 + (char) 0x0
				+ (char) 0x0 + (char) 0x0 + (char) 0x0 + (
						char) 0x0
				+ (char) 0x0 + cpp.getPassword() + TimeStr;
		md5.update(md5Str.getBytes());
		MD5Pwd=md5.digest();
		TimeStamp=Integer.parseInt(TimeStr);
	}
	
	public void send() throws IOException{
		int Len=0;
		byte[] Buf=new byte[1000];
				
		Command_ID=Const.COMMAND_CMPP_CONNECT;
		Total_Length=4*4+16+1+6; 
		
		System.arraycopy(cpp.getSpid().getBytes(),0,bSP_ID,0,cpp.getSpid().getBytes().length);

		System.arraycopy(Tools.int2byte(Total_Length),0,Buf,Len,4);
		Len=Len+4;
		System.arraycopy(Tools.int2byte(Command_ID),0,Buf,Len,4);
		Len=Len+4;
		System.arraycopy(Tools.int2byte(Seq_ID),0,Buf,Len,4);
		Len=Len+4;
		System.arraycopy(bSP_ID,0,Buf,Len,bSP_ID.length );
		Len=Len+6;
		System.arraycopy(MD5Pwd,0,Buf,Len,MD5Pwd.length);
		Len=Len+16;
		Buf[Len]=cpp.getCmppVersion();
		Len=Len+1;
		System.arraycopy(Tools.int2byte(TimeStamp),0,Buf,Len,4);
		Len=Len+4;
		cmpp.send(Buf,0,Len);
	}
	public void getLoginResult() throws IOException{
		Cmpp2_Head Head = new Cmpp2_Head();
		Cmpp2_ConnectResp ConnectResp = new Cmpp2_ConnectResp();
		DataInputStream dis=new DataInputStream(cmpp.getInputStream());
		
		Head.setTotal_Length(dis.readInt());
		
		Head.setCommand_ID(dis.readInt());
		Head.setSeq_ID (dis.readInt());
		if (Head.getTotal_Length() <= 12) throw new IOException("登录失败!");
		if (Head.getCommand_ID() != Const.COMMAND_CMPP_CONNECT_RESP) new IOException("登录失败!");
		ConnectResp.status=dis.readByte();
		dis.read(ConnectResp.MD5Pwd, 0, ConnectResp.MD5Pwd.length);
		ConnectResp.Version = dis.readByte();
		System.out.println("ConnectResp.Version="+ConnectResp.Version);
		System.out.println("ConnectResp.status="+ConnectResp.status);
		if (ConnectResp.status != 0) throw new IOException("登录失败!");
	}
}
