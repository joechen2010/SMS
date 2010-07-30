package com.cmpp.protocol;

import java.io.DataInputStream;
import java.io.IOException;

public class Cmpp2_Deliver {
	private Cmpp2_Head ch = null;

	private long nMsgId = 0;

	private long nReportMsgId = 0;

	private byte[] cDestAddr = null;

	private byte[] cServiceId = null;

	private byte[] cSrcAddr = null;

	private int nMsgLen = 0;

	private byte[] cMsgContent = null;

	private byte[] cReserved = null;

	private byte[] cStatus = null;

	private byte[] cSubmitTime = null;

	private int nMsgType = 0;

	private int nStatus = 0;

	private int nMsgFmt = 0;

	private int i=0;
	// -------
	public Cmpp2_Deliver() {
		cDestAddr = new byte[21];
		cServiceId = new byte[10];
		cSrcAddr = new byte[21];
		cMsgContent = new byte[140];
		cReserved = new byte[8];
		cStatus = new byte[7];
		cSubmitTime = new byte[20];
	}

	public void ReadIn(DataInputStream dis) throws IOException,ArrayIndexOutOfBoundsException{
		nMsgId = dis.readLong();
		byte c[] = new byte[7];
        //---------------------变换MISGID---------------------
        c[0] = (byte)(int)(nMsgId >> 56);
        c[1] = (byte)(int)(nMsgId >> 48);
        c[2] = (byte)(int)(nMsgId >> 40);
        c[3] = (byte)(int)(nMsgId >> 32);
        c[4] = (byte)(int)(nMsgId >> 16);
        c[5] = (byte)(int)(nMsgId >> 8);
        c[6] = (byte)(int)nMsgId;
        nMsgId=(long)c[6] & (long)255 | ((long)c[5] & (long)255) << 8 | 
        ((long)c[4] & (long)255) << 16 | ((long)c[3] & (long)255) << 24 | 
        ((long)c[2] & (long)255) << 32 | ((long)c[1] & (long)255) << 40 | 
        ((long)c[0] & (long)255) << 48;
        //------------------------------------------------------
        //System.out.print("[@CMPP2_Deliver]MsgID:" );
        //System.out.println(nMsgId);

		dis.read(cDestAddr, 0, 21);
		dis.read(cServiceId, 0, 10);

		byte b = dis.readByte();
		b = dis.readByte();
		b = dis.readByte();
		nMsgFmt = b;
		dis.read(cSrcAddr, 0, 21);
		for (i=0;i<140;i++){
			cMsgContent[i]=0;
		}
		nMsgType = dis.readByte();
		if (nMsgType == 0) {// 正常的上行短信
			nMsgLen = dis.readByte(); // 短信长度
			if (nMsgLen<0) nMsgLen+=256;
			dis.read(cMsgContent, 0, nMsgLen);
			dis.read(cReserved, 0, 8);

		} else {// 状态报告
			nMsgLen = dis.readByte(); // 短信长度
			nReportMsgId = dis.readLong();
	        //---------------------变换MISGID---------------------
	        c[0] = (byte)(int)(nReportMsgId >> 56);
	        c[1] = (byte)(int)(nReportMsgId >> 48);
	        c[2] = (byte)(int)(nReportMsgId >> 40);
	        c[3] = (byte)(int)(nReportMsgId >> 32);
	        c[4] = (byte)(int)(nReportMsgId >> 16);
	        c[5] = (byte)(int)(nReportMsgId >> 8);
	        c[6] = (byte)(int)nReportMsgId;
	        nReportMsgId=(long)c[6] & (long)255 | ((long)c[5] & (long)255) << 8 | 
	        ((long)c[4] & (long)255) << 16 | ((long)c[3] & (long)255) << 24 | 
	        ((long)c[2] & (long)255) << 32 | ((long)c[1] & (long)255) << 40 | 
	        ((long)c[0] & (long)255) << 48;
	        //------------------------------------------------------
	        //System.out.print("[@CMPP2_Deliver]nReportMsgId:" );
	        //System.out.println(nMsgId);
			dis.read(cStatus, 0, 7);
			if (cStatus[0] == 'D' && cStatus[1] == 'E' && cStatus[2] == 'L'
					&& cStatus[3] == 'I' && cStatus[4] == 'V'
					&& cStatus[5] == 'R' && cStatus[6] == 'D') {
				nStatus = 0;
			} else {
				nStatus = 1;
			} 
			dis.read(cSubmitTime, 0, 20);
			dis.read(cDestAddr, 0, 21);
			dis.read(cSubmitTime, 0, 12);
		}
	}

	public void ReadIn2(DataInputStream dis) throws IOException,ArrayIndexOutOfBoundsException{
		nMsgId = dis.readLong();
        byte c[] = new byte[7];
        //---------------------变换MISGID---------------------
        c[0] = (byte)(int)(nMsgId >> 56);
        c[1] = (byte)(int)(nMsgId >> 48);
        c[2] = (byte)(int)(nMsgId >> 40);
        c[3] = (byte)(int)(nMsgId >> 32);
        c[4] = (byte)(int)(nMsgId >> 16);
        c[5] = (byte)(int)(nMsgId >> 8);
        c[6] = (byte)(int)nMsgId;
        nMsgId=(long)c[6] & (long)255 | ((long)c[5] & (long)255) << 8 | 
        ((long)c[4] & (long)255) << 16 | ((long)c[3] & (long)255) << 24 | 
        ((long)c[2] & (long)255) << 32 | ((long)c[1] & (long)255) << 40 | 
        ((long)c[0] & (long)255) << 48;
        //------------------------------------------------------
		dis.read(cDestAddr, 0, 21);
		dis.read(cServiceId, 0, 10);

		byte b = dis.readByte();
		b = dis.readByte();
		b = dis.readByte();
		nMsgFmt = b;
		dis.read(cSrcAddr, 0, 21);
		for (i=0;i<140;i++){
			cMsgContent[i]=0;
		}
		nMsgType = dis.readByte();
		if (nMsgType == 0) {// 正常的上行短信
			nMsgLen = dis.readByte(); // 短信长度
			if (nMsgLen<0) nMsgLen+=256;
			dis.read(cMsgContent, 0, nMsgLen);
			dis.read(cReserved, 0, 8);

		} else {// 状态报告
			nMsgLen = dis.readByte(); // 短信长度
			nReportMsgId = dis.readLong();
	        //---------------------变换MISGID---------------------
	        c[0] = (byte)(int)(nReportMsgId >> 56);
	        c[1] = (byte)(int)(nReportMsgId >> 48);
	        c[2] = (byte)(int)(nReportMsgId >> 40);
	        c[3] = (byte)(int)(nReportMsgId >> 32);
	        c[4] = (byte)(int)(nReportMsgId >> 16);
	        c[5] = (byte)(int)(nReportMsgId >> 8);
	        c[6] = (byte)(int)nReportMsgId;
	        nReportMsgId=(long)c[6] & (long)255 | ((long)c[5] & (long)255) << 8 | 
	        ((long)c[4] & (long)255) << 16 | ((long)c[3] & (long)255) << 24 | 
	        ((long)c[2] & (long)255) << 32 | ((long)c[1] & (long)255) << 40 | 
	        ((long)c[0] & (long)255) << 48;
	        //------------------------------------------------------
			dis.read(cStatus, 0, 7);
			if (cStatus[0] == 'D' && cStatus[1] == 'E' && cStatus[2] == 'L'
					&& cStatus[3] == 'I' && cStatus[4] == 'V'
					&& cStatus[5] == 'R' && cStatus[6] == 'D') {
				nStatus = 0;
			} else if(cStatus[0] == 'E' && cStatus[1] == 'X' && cStatus[2] == 'P'
					&& cStatus[3] == 'I' && cStatus[4] == 'R'
					&& cStatus[5] == 'E' && cStatus[6] == 'D') {
				nStatus = 1;
			} else if(cStatus[0] == 'D' && cStatus[1] == 'E' && cStatus[2] == 'L'
					&& cStatus[3] == 'E' && cStatus[4] == 'T'
					&& cStatus[5] == 'E' && cStatus[6] == 'D') {
				nStatus = 2;
			} else if(cStatus[0] == 'U' && cStatus[1] == 'N' && cStatus[2] == 'D'
					&& cStatus[3] == 'E' && cStatus[4] == 'L'
					&& cStatus[5] == 'I' && cStatus[6] == 'V') {
				nStatus = 3;
			}else if(cStatus[0] == 'A' && cStatus[1] == 'C' && cStatus[2] == 'C'
					&& cStatus[3] == 'E' && cStatus[4] == 'P'
					&& cStatus[5] == 'T' && cStatus[6] == 'D') {
				nStatus = 4;
			}else if(cStatus[0] == 'U' && cStatus[1] == 'N' && cStatus[2] == 'K'
					&& cStatus[3] == 'N' && cStatus[4] == 'O'
					&& cStatus[5] == 'W' && cStatus[6] == 'N') {
				nStatus = 5;
			}else if(cStatus[0] == 'R' && cStatus[1] == 'E' && cStatus[2] == 'J'
					&& cStatus[3] == 'E' && cStatus[4] == 'C'
					&& cStatus[5] == 'T' && cStatus[6] == 'D') {
				nStatus = 6;
			}
			dis.read(cSubmitTime, 0, 20);
			dis.read(cDestAddr, 0, 21);
			dis.read(cSubmitTime, 0, 12);
		}
	}

	
	public byte[] getCDestAddr() {
		return cDestAddr;
	}

	public void setCDestAddr(byte[] destAddr) {
		cDestAddr = destAddr;
	}

	public Cmpp2_Head getCh() {
		return ch;
	}

	public void setCh(Cmpp2_Head ch) {
		this.ch = ch;
	}

	public byte[] getCServiceId() {
		return cServiceId;
	}

	public void setCServiceId(byte[] serviceId) {
		cServiceId = serviceId;
	}

	public long getNMsgId() {
		return nMsgId;
	}

	public void setNMsgId(long msgId) {
		nMsgId = msgId;
	}

	public int getNMsgLen() {
		return nMsgLen;
	}

	public void setNMsgLen(int msgLen) {
		nMsgLen = msgLen;
	}

	public int getNMsgType() {
		return nMsgType;
	}

	public void setNMsgType(int msgType) {
		nMsgType = msgType;
	}

	public byte[] getCMsgContent() {
		return cMsgContent;
	}

	public void setCMsgContent(byte[] msgContent) {
		cMsgContent = msgContent;
	}

	public byte[] getCSrcAddr() {
		return cSrcAddr;
	}

	public void setCSrcAddr(byte[] srcAddr) {
		cSrcAddr = srcAddr;
	}

	public byte[] getCStatus() {
		return cStatus;
	}

	public void setCStatus(byte[] status) {
		cStatus = status;
	}

	public long getNReportMsgId() {
		return nReportMsgId;
	}

	public void setNReportMsgId(long reportMsgId) {
		nReportMsgId = reportMsgId;
	}

	public int getNStatus() {
		return nStatus;
	}

	public void setNStatus(int status) {
		nStatus = status;
	}

	public int getNMsgFmt() {
		return nMsgFmt;
	}

	public void setNMsgFmt(int msgFmt) {
		nMsgFmt = msgFmt;
	}

}
