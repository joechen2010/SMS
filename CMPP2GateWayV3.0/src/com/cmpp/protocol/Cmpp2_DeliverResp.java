package com.cmpp.protocol;
import java.io.*;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;

import com.cmpp.common.Const;
import com.cmpp.service.CMPPSocket;
import com.cmpp.tools.Tools;





public class Cmpp2_DeliverResp {
	private int Seq_ID;	
	private long MsgID=0;
	private byte Result=0;


	
	public long getMsgID() {
		return MsgID;
	}



	public void setMsgID(long msgID) {
		MsgID = msgID;
	}



	public byte getResult() {
		return Result;
	}



	public void setResult(byte result) {
		Result = result;
	}



	public int getSeq_ID() {
		return Seq_ID;
	}



	public void setSeq_ID(int seq_ID) {
		Seq_ID = seq_ID;
	}



	public void send(CMPPSocket cmpp) throws IOException{
		byte[] buff=new byte[21];
		System.arraycopy(Tools.int2byte(21), 0, buff, 0, 4);
		System.arraycopy(Tools.int2byte(Const.COMMAND_CMPP_DELIVER_RESP), 0, buff, 4, 4);
		System.arraycopy(Tools.int2byte(Seq_ID), 0, buff, 8, 4);
		Tools.long2byte(MsgID, buff,12);
		buff[20]=Result;
		cmpp.send(buff,0,21);
	}
}
