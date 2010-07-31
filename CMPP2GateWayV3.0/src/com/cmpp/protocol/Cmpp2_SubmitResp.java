package com.cmpp.protocol;

import java.io.DataInputStream;
import java.io.IOException;

public class Cmpp2_SubmitResp {
	private long MsgID;
	private byte bResult;
	private int Result=0;

	public Cmpp2_SubmitResp(){
		
	}
	public void setMsgId(long nMsgId){
		MsgID=nMsgId;
	}
	public long getMsgId(){
		return MsgID;
	}
	public void setResult(int b){
		Result=b;
	}
	public int getResult(){
		return Result;
	}
	public void ReadIn(DataInputStream input) throws IOException,ArrayIndexOutOfBoundsException{
		MsgID=input.readLong();
        byte b[] = new byte[7];
        b[0] = (byte)(int)(MsgID >> 56);
        b[1] = (byte)(int)(MsgID >> 48);
        b[2] = (byte)(int)(MsgID >> 40);
        b[3] = (byte)(int)(MsgID >> 32);
        b[4] = (byte)(int)(MsgID >> 16);
        b[5] = (byte)(int)(MsgID >> 8);
        b[6] = (byte)(int)MsgID;
        MsgID=(long)b[6] & (long)255 | ((long)b[5] & (long)255) << 8 | 
        ((long)b[4] & (long)255) << 16 | ((long)b[3] & (long)255) << 24 | 
        ((long)b[2] & (long)255) << 32 | ((long)b[1] & (long)255) << 40 | 
        ((long)b[0] & (long)255) << 48;		
        //System.out.print("[@CMPP2_SubmitResp]MsgID:" );
        //System.out.println(MsgID);
		bResult=input.readByte();
		Result=bResult;
		if(Result<0){Result+=256;}
	}
	/**
	 * 为MSGID超过BIGINT类型新编写的函数
	 * @param input
	 * @throws IOException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void ReadIn2(DataInputStream input) throws IOException,ArrayIndexOutOfBoundsException{
		MsgID=input.readLong();
        byte b[] = new byte[7];
        b[0] = (byte)(int)(MsgID >> 56);
        b[1] = (byte)(int)(MsgID >> 48);
        b[2] = (byte)(int)(MsgID >> 40);
        b[3] = (byte)(int)(MsgID >> 32);
        b[4] = (byte)(int)(MsgID >> 16);
        b[5] = (byte)(int)(MsgID >> 8);
        b[6] = (byte)(int)MsgID;
        MsgID=(long)b[6] & (long)255 | ((long)b[5] & (long)255) << 8 | 
        ((long)b[4] & (long)255) << 16 | ((long)b[3] & (long)255) << 24 | 
        ((long)b[2] & (long)255) << 32 | ((long)b[1] & (long)255) << 40 | 
        ((long)b[0] & (long)255) << 48;
		Result=input.readInt();
	}
}
