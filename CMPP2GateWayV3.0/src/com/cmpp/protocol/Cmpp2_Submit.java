package com.cmpp.protocol;

import java.io.*;
import java.util.StringTokenizer;
//import com.cmpp.common.Const;
import com.cmpp.common.Const;
import com.cmpp.service.CMPPSocket;
import com.cmpp.tools.*;



public class Cmpp2_Submit
{
    private Cmpp2_Head ch;
    private String DBID;
    private int nMsgType;
    private byte StatusReport;
    private byte cServiceId[];
    private byte Fee_UserType;
    private byte FeeAddr[];
    private byte SP_ID[];
    private byte FeeType[];
    private byte FeeCode[];
    private byte OrgAddr[];
    private byte DestAddr[];
    private int nMsg_Len;
    private byte cMsg_Content[];
    private byte cReserve[];
    private int nSMSTextContentLen;
    private int nDestAddrRealLen;
    private String strMsgContent;
    private byte cWapPushHttp[];
    private String strWapPushHttp;
    private int nWapPushHttpLen;
    private byte cWapPushContent[];
    private int nWapPushContentLen;
    private String strWapPushContent;
    private byte PushBuf[];
    private int templen;
    private int sendSevel;
    private CMPPSocket cmpp;
    //add by jecon 2010-7-24
    private String DestAddrStr;
    private boolean multiFlag = false  ;
    //add by wangzm 2010-7-24
   private int msg_fmt=0;//端口短信标志
   private int msg_port;//手机端口号
   private int send_port;//发送端口号
   byte[] pSMSContent=null;
    
    public Cmpp2_Submit(){
    	ch = null;
        DBID = "0";
        nMsgType = 0;
        nDestAddrRealLen = 0;
        cServiceId = new byte[10];
        FeeAddr = new byte[21];
        SP_ID = new byte[6];
        FeeType = new byte[2];
        FeeCode = new byte[6];
        OrgAddr = new byte[21];
        DestAddr = new byte[21];
        nMsg_Len = 0;
        cMsg_Content = null;
        cReserve = new byte[8];
        nSMSTextContentLen = 0;
        strMsgContent = null;
        cWapPushHttp = null;
        strWapPushHttp = null;
        nWapPushHttpLen = 0;
        cWapPushContent = null;
        nWapPushContentLen = 0;
        strWapPushContent = null;
        PushBuf = new byte[160];
        templen = 19;
        sendSevel = 0;
        ch = new Cmpp2_Head();
        cMsg_Content = new byte[1000];
        cWapPushHttp = new byte[140];
        cWapPushContent = new byte[140];
    }
    
    public Cmpp2_Submit(CMPPSocket cmpp)
    {
        ch = null;
        DBID = "0";
        nMsgType = 0;
        cServiceId = new byte[10];
        FeeAddr = new byte[21];
        SP_ID = new byte[6];
        FeeType = new byte[2];
        FeeCode = new byte[6];
        OrgAddr = new byte[21];
        DestAddr = new byte[21];
        nMsg_Len = 0;
        cMsg_Content = null;
        cReserve = new byte[8];
        nSMSTextContentLen = 0;
        nDestAddrRealLen = 0;
        strMsgContent = null;
        cWapPushHttp = null;
        strWapPushHttp = null;
        nWapPushHttpLen = 0;
        cWapPushContent = null;
        nWapPushContentLen = 0;
        strWapPushContent = null;
        PushBuf = new byte[160];
        templen = 19;
        sendSevel = 0;
        this.cmpp = null;
        this.cmpp = cmpp;
        ch = new Cmpp2_Head();
        //cMsg_Content = new byte[140];
        cMsg_Content = new byte[1000];
        cWapPushHttp = new byte[140];
        cWapPushContent = new byte[140];
    }

    private void setMMSPushLen()
    {
        PushBuf[0] = 6;
        PushBuf[1] = 5;
        PushBuf[2] = 4;
        PushBuf[3] = 11;
        PushBuf[4] = -124;
        PushBuf[5] = 35;
        PushBuf[6] = -16;
        PushBuf[7] = 0;
        PushBuf[8] = 6;
        PushBuf[9] = 34;
        PushBuf[10] = 97;
        PushBuf[11] = 112;
        PushBuf[12] = 112;
        PushBuf[13] = 108;
        PushBuf[14] = 105;
        PushBuf[15] = 99;
        PushBuf[16] = 97;
        PushBuf[17] = 116;
        PushBuf[18] = 105;
        PushBuf[19] = 111;
        PushBuf[20] = 110;
        PushBuf[21] = 47;
        PushBuf[22] = 118;
        PushBuf[23] = 110;
        PushBuf[24] = 100;
        PushBuf[25] = 46;
        PushBuf[26] = 119;
        PushBuf[27] = 97;
        PushBuf[28] = 112;
        PushBuf[29] = 46;
        PushBuf[30] = 109;
        PushBuf[31] = 109;
        PushBuf[32] = 115;
        PushBuf[33] = 45;
        PushBuf[34] = 109;
        PushBuf[35] = 101;
        PushBuf[36] = 115;
        PushBuf[37] = 115;
        PushBuf[38] = 97;
        PushBuf[39] = 103;
        PushBuf[40] = 101;
        PushBuf[41] = 0;
        PushBuf[42] = -81;
        PushBuf[43] = -124;
        PushBuf[44] = -116;
        PushBuf[45] = -126;
        PushBuf[46] = -104;
        PushBuf[47] = 97;
        PushBuf[48] = 0;
        PushBuf[49] = -115;
        PushBuf[50] = -112;
        PushBuf[51] = -119;
        byte len = getOrgAddrLen();
        PushBuf[52] = len;
        int pos = 53;
        for(int i = 0; i < len; i++)
            PushBuf[pos++] = OrgAddr[i];

        PushBuf[pos++] = 0;
        PushBuf[pos++] = -106;
        len = getDestAddrLen();
        PushBuf[pos++] = len;
        for(int i = 0; i < len; i++)
            PushBuf[pos++] = DestAddr[i];

        PushBuf[pos++] = 0;
        PushBuf[pos++] = -118;
        PushBuf[pos++] = -128;
        PushBuf[pos++] = -114;
        PushBuf[pos++] = 1;
        PushBuf[pos++] = 58;
        PushBuf[pos++] = -120;
        PushBuf[pos++] = 5;
        PushBuf[pos++] = -127;
        PushBuf[pos++] = 3;
        PushBuf[pos++] = 9;
        PushBuf[pos++] = 58;
        PushBuf[pos++] = -128;
        PushBuf[pos++] = -125;
        PushBuf[pos++] = 104;
        PushBuf[pos++] = 116;
        PushBuf[pos++] = 116;
        PushBuf[pos++] = 112;
        PushBuf[pos++] = 58;
        PushBuf[pos++] = 47;
        PushBuf[pos++] = 47;
        for(int i = 0; i < nWapPushHttpLen; i++)
            PushBuf[pos++] = cWapPushHttp[i];

        PushBuf[pos++] = 0;
        nMsg_Len = pos;
        System.out.println("\n-------------------------------");
        for(int i = 0; i < nMsg_Len; i++)
            System.out.println(Tools.byteToHexString(PushBuf[i]));

        System.out.println("\n-------------------------------");
    }

    private byte getOrgAddrLen()
    {
        for(int i = 0; i < OrgAddr.length; i++)
            if(OrgAddr[i] == 0)
                return (byte)i;

        return 0;
    }

    private byte getDestAddrLen()
    {
        for(int i = 0; i < DestAddr.length; i++)
            if(DestAddr[i] == 0)
                return (byte)i;

        return 0;
    }

    private int setWapPushLen()
    {
        templen = 19;
        PushBuf[0] = 6;
        PushBuf[1] = 5;
        PushBuf[2] = 4;
        PushBuf[3] = 11;
        PushBuf[4] = -124;
        PushBuf[5] = 35;
        PushBuf[6] = -16;
        PushBuf[7] = -78;
        PushBuf[8] = 6;
        PushBuf[9] = 1;
        PushBuf[10] = -82;
        PushBuf[11] = 2;
        PushBuf[12] = 5;
        PushBuf[13] = 106;
        PushBuf[14] = 0;
        PushBuf[15] = 69;
        PushBuf[16] = -58;
        PushBuf[17] = 12;
        PushBuf[18] = 3;
        System.arraycopy(cWapPushHttp, 0, PushBuf, templen, nWapPushHttpLen);
        PushBuf[templen += nWapPushHttpLen] = 0;
        PushBuf[++templen] = 1;
        PushBuf[++templen] = 3;
        System.arraycopy(cWapPushContent, 0, PushBuf, ++templen, nWapPushContentLen);
        PushBuf[templen += nWapPushContentLen] = 0;
        PushBuf[++templen] = 1;
        PushBuf[++templen] = 1;
        templen++;
        nMsg_Len = templen;
        return nMsg_Len;
    }

    public void Submit()
        throws IOException
    {
        int nLen = 0;
        if(!multiFlag){
        	nLen =159;
        }else{
        	nLen = 159-21+this.nDestAddrRealLen*21;
        }
        if(nMsgType == 2)	//WAPPUSH
        {
            if(25 + nWapPushHttpLen + nWapPushContentLen <= 140)
                setWapPushLen();
            else
                System.out.println("WAP PUSH太长=" + nMsg_Len);
        } else
        if(nMsgType == 3)	//MMSPUSH
        {
            setMMSPushLen();
            if(nMsgType > 140)
            {
                System.out.println("MMS PUSH太长=" + nMsg_Len);
                throw new IOException("MMS PUSH太长=" + nMsg_Len);
            }
        } else				//SMS
        {
            nMsg_Len = nSMSTextContentLen;
        }
        if(nMsg_Len < 0)
            nMsg_Len += 256;
        		
		if (nMsg_Len>140){	//超长短信
			int smsTotal=0;		//分割成的短信个数
			int msgMaxLength=132;	//132
			byte[] header=null;
			int lastSMSLen=nMsg_Len % msgMaxLength;
			
			if (lastSMSLen==0){
				smsTotal=nMsg_Len/msgMaxLength;
			}else{
				smsTotal=nMsg_Len/msgMaxLength+1;
			}
			int copy_pos=0;
			byte[] cSMSContent=null;
			for (int i=1;i<=smsTotal;i++){
				header=setMultiHeader((byte)smsTotal,(byte)smsTotal,(byte)i);
				if (i==smsTotal){
					if (lastSMSLen>0){ //最后一条不足134字节
						cSMSContent=new byte[header.length+lastSMSLen];
						System.arraycopy(header, 0, cSMSContent, 0,header.length);
						System.arraycopy(cMsg_Content, copy_pos,cSMSContent, header.length,lastSMSLen);
						//copy_pos+=lastSMSLen;
					}
				}else{
					cSMSContent=new byte[header.length+132];
					System.arraycopy(header, 0, cSMSContent, 0,header.length);
					System.arraycopy(cMsg_Content, copy_pos,cSMSContent, header.length,132);
					copy_pos+=132;
				}
				send_MultiSMS(nLen+cSMSContent.length,cSMSContent,(byte)smsTotal,(byte)i);
			}
		}else{
			nLen += nMsg_Len;
			if(multiFlag)
				multiSend(nLen);
			else {
				if(msg_fmt==1){//端口短信
					byte[] header=null;
					header=setPortHeader(msg_port,send_port);
					pSMSContent=new byte[header.length+nMsg_Len];
					System.arraycopy(header, 0, pSMSContent, 0,header.length);
					System.arraycopy(cMsg_Content, 0,pSMSContent, header.length,nMsg_Len);
					nLen+=header.length;
					sendPort(nLen);
				}else{
					send(nLen);
				}
			}
				
		}
    }

	private byte[] setMultiHeader(byte flag,byte smsTotal,byte smsNo){
		byte[] h=new byte[7];
		h[0]=0x06;
		h[1]=0x08;
		h[2]=0x04;
		h[3]=00;
		h[4]=0x2A;
		h[5]=(byte)smsTotal;
		h[6]=(byte)smsNo;
		return h;
	}    
	/*
	private byte[] setMultiHeader2(byte flag,byte smsTotal,byte smsNo){
		byte[] h=new byte[6];
		h[0]=0x05;
		h[1]=0x00;
		h[2]=0x03;
		h[3]=0x0A;
		h[4]=smsTotal;
		h[5]=smsNo;
		return h;
	} 
	*/
	
	private byte[] setPortHeader(int receivePort,int sendPort){
		byte[] h=new byte[7];
		h[0] = 0x06;
		h[1] = 0x05;
		h[2] = 0x04;
		h[3] = (byte)((receivePort >> 8) & 0xFF);   
		h[4] = (byte)receivePort;
		h[5] = (byte)((sendPort >> 8) & 0xFF);		//
		h[6] = (byte)sendPort;		 
		return h;
	}    
 
	
	/**
	 * 发送多条短信(用于长信息发送)
	 * @param nTotalLen	消息长度
	 * @param smsContent	消息内容
	 * @param smsTotal	消息总个数
	 * @param smsNo	消息序号
	 * @throws IOException
	 */
	private void send_MultiSMS(int nTotalLen,byte[] smsContent,byte smsTotal,byte smsNo) throws IOException {
		ch.setTotal_Length(nTotalLen);
		ch.setCommand_ID(Const.COMMAND_CMPP_SUBMIT);
		//-----------------------------------------------------
		int Len = 0;
		byte[] Buf = new byte[nTotalLen];
		System.arraycopy(Tools.int2byte(ch.getTotal_Length()),0,Buf,Len,4);
		Len+=4;
		System.arraycopy(Tools.int2byte(ch.getCommand_ID()),0,Buf,Len,4);
		Len+=4;
		System.arraycopy(Tools.int2byte(ch.getSeq_ID()),0,Buf,Len,4);
		Len+=4;
		Len+=8;
		Buf[Len++]=smsTotal;
		Buf[Len++]=smsNo;
		Buf[Len++]=StatusReport;
		Buf[Len++]=(byte)3;
		System.arraycopy(cServiceId, 0, Buf,Len,10);
		Len+=10;
		Buf[Len] = Fee_UserType;
		Len ++;
		//System.arraycopy(FeeAddr, 0, Buf, Len, 32);
		//Len = Len + 32;
		System.arraycopy(FeeAddr, 0, Buf, Len, 21);
		Len = Len+21;
		//Buf[Len++] = 0;
		Buf[Len++]=0;	//TP_pId
		Buf[Len] = 1;	//TP_udhi
		Len = Len + 1;
		Buf[Len] = 8; 	//编码
		Len = Len + 1;
		System.arraycopy(SP_ID, 0, Buf, Len, 6);	//Msg_src
		Len = Len + 6;
		System.arraycopy(FeeType, 0, Buf, Len, 2);	//FeeType
		Len = Len + 2;
		System.arraycopy(FeeCode, 0, Buf, Len, 6);	//FeeCode
		Len = Len + 6;
		Len = Len + 17;								//ValId_Time
		Len = Len + 17;								//At_Time
		System.arraycopy(OrgAddr, 0, Buf, Len, 21);	//Src_Id
		Len = Len + 21;
		//修改群发 jecon 20200727
		if(!multiFlag){
			Buf[Len] = 1;								//DestUsr_tl
			Len = Len + 1;
			System.arraycopy(DestAddr, 0, Buf, Len, 21);	
			Len = Len+21;
		}else{
			Buf[Len] = (byte)nDestAddrRealLen;
	        Len++;
	        System.arraycopy(getDestBytes(),0,Buf,Len,nDestAddrRealLen*21);
	        Len += nDestAddrRealLen*21;
		}
		//System.arraycopy(DestAddr, 0, Buf, Len, 32);	//Dest_terminal_Id
		//Len = Len + 32;
		//Buf[Len++]=0;
		Buf[Len++] = (byte)smsContent.length;		//Msg_Length
		System.arraycopy(smsContent,0,Buf,Len,smsContent.length);
		Len+=smsContent.length;
		System.arraycopy(cReserve, 0, Buf, Len, 8);
		Len+=8;
		//System.arraycopy(cLinkId, 0, Buf, Len, 20);
		cmpp.send(Buf, 0, ch.getTotal_Length());
		/*for (int i=0;i<smsContent.length;i++){
			System.out.print(Tools.byteToHexString(smsContent[i])+" ");
		}
		System.out.print("\n---------------------------------------");
		for (int i=0;i<Buf.length;i++){
			System.out.print(Tools.byteToHexString(Buf[i])+" ");
		}
		System.out.print("\n++++++++++++++++++++++++++++++++++++++++++");*/
		
		//System.out.println("发送完成"+nMsg_Len);
	}
	
	
    private void multiSend(int nTotalLen)
        throws IOException
    {
        ch.setTotal_Length(nTotalLen);
        ch.setCommand_ID(4);
        int Len = 0;
        byte Buf[] = new byte[nTotalLen];
        System.arraycopy(Tools.int2byte(ch.getTotal_Length()), 0, Buf, Len, 4);
        Len += 4;
        System.arraycopy(Tools.int2byte(ch.getCommand_ID()), 0, Buf, Len, 4);
        Len += 4;
        System.arraycopy(Tools.int2byte(ch.getSeq_ID()), 0, Buf, Len, 4);
        Len += 4;
        Len += 8;
        Buf[Len++] = 1;
        Buf[Len++] = 1;
        Buf[Len++] = StatusReport;
        Buf[Len++] = 3;
        System.arraycopy(cServiceId, 0, Buf, Len, 10);
        Len += 10;
        Buf[Len] = Fee_UserType;
        Len++;
        System.arraycopy(FeeAddr, 0, Buf, Len, 21);
        Len += 21;
        Buf[Len] = 0;
        Len++;
        if(nMsgType == 2)
            Buf[Len] = 1;
        else
            Buf[Len] = 0;
        Len++;
        if(nMsgType == 2)
            Buf[Len] = 4;
        else
            Buf[Len] = 15;
        Len++;
        System.arraycopy(SP_ID, 0, Buf, Len, 6);
        Len += 6;
        System.arraycopy(FeeType, 0, Buf, Len, 2);
        Len += 2;
        System.arraycopy(FeeCode, 0, Buf, Len, 6);
        Len += 6;
        Len += 17;
        Len += 17;
        System.arraycopy(OrgAddr, 0, Buf, Len, 21);
        Len += 21;
        Buf[Len] = (byte)nDestAddrRealLen;
        Len++;
        //System.arraycopy(DestAddr, 0, Buf, Len, 21);
        System.arraycopy(getDestBytes(),0,Buf,Len,nDestAddrRealLen*21);
        Len += nDestAddrRealLen*21;
        Buf[Len] = (byte)nMsg_Len;
        Len++;
        if(nMsgType == 2)
        {
            System.arraycopy(PushBuf, 0, Buf, Len, nMsg_Len);
            Len += nMsg_Len;
        } else
        {
            System.arraycopy(cMsg_Content, 0, Buf, Len, nMsg_Len);
            Len += nMsg_Len;
        }
        
        //byte[] msgBytes= new byte[Len];
        //System.arraycopy(Buf,0,msgBytes,0,Len);
        //cmpp.send(Buf, 0, Len+8);
        //System.out.println("ddddddddddddddddddd:"+Len);
        cmpp.send(Buf, 0, ch.getTotal_Length());
    }
    

	
    private void send(int nTotalLen)
        throws IOException
    {
        ch.setTotal_Length(nTotalLen);
        ch.setCommand_ID(4);
        int Len = 0;
        byte Buf[] = new byte[nTotalLen];
        System.arraycopy(Tools.int2byte(ch.getTotal_Length()), 0, Buf, Len, 4);
        Len += 4;
        System.arraycopy(Tools.int2byte(ch.getCommand_ID()), 0, Buf, Len, 4);
        Len += 4;
        System.arraycopy(Tools.int2byte(ch.getSeq_ID()), 0, Buf, Len, 4);
        Len += 4;
        Len += 8;
        Buf[Len++] = 1;
        Buf[Len++] = 1;
        Buf[Len++] = StatusReport;
        Buf[Len++] = 3;
        System.arraycopy(cServiceId, 0, Buf, Len, 10);
        Len += 10;
        Buf[Len] = Fee_UserType;
        Len++;
        System.arraycopy(FeeAddr, 0, Buf, Len, 21);
        Len += 21;
        Buf[Len] = 0;
        Len++;
        //TP_udhi
       if(nMsgType == 2){
            Buf[Len] = 1;
        }
        else{
            Buf[Len] = 0;
        }
        Len++;
      if(nMsgType == 2)
            Buf[Len] = 4;
        else
            Buf[Len] = 15;
        Len++;
       
        System.arraycopy(SP_ID, 0, Buf, Len, 6);
        Len += 6;
        System.arraycopy(FeeType, 0, Buf, Len, 2);
        Len += 2;
        System.arraycopy(FeeCode, 0, Buf, Len, 6);
        Len += 6;
        Len += 17;
        Len += 17;
        System.arraycopy(OrgAddr, 0, Buf, Len, 21);
        Len += 21;
        Buf[Len] = 1;
        Len++;
        System.arraycopy(DestAddr, 0, Buf, Len, 21);
        Len += 21;
        Buf[Len] = (byte)nMsg_Len;
        Len++;
        if(nMsgType == 2)
        {
            System.arraycopy(PushBuf, 0, Buf, Len, nMsg_Len);
            //System.out.println("使用的是发送wappush。。。。。。。。。");
        } else
        {
			System.arraycopy(cMsg_Content, 0, Buf, Len, nMsg_Len);
        }
        cmpp.send(Buf, 0, ch.getTotal_Length());
    }

    private void sendPort(int nTotalLen)
    throws IOException
{
    ch.setTotal_Length(nTotalLen);
    ch.setCommand_ID(Const.COMMAND_CMPP_SUBMIT);
    int Len = 0;
    byte Buf[] = new byte[nTotalLen];
    System.arraycopy(Tools.int2byte(ch.getTotal_Length()), 0, Buf, Len, 4);
    Len += 4;
    System.arraycopy(Tools.int2byte(ch.getCommand_ID()), 0, Buf, Len, 4);
    Len += 4;
    System.arraycopy(Tools.int2byte(ch.getSeq_ID()), 0, Buf, Len, 4);
    Len += 4;
    Len += 8;
    Buf[Len++] = 1;
    Buf[Len++] = 1;
    Buf[Len++] = StatusReport;
    Buf[Len++] = 3;
    System.arraycopy(cServiceId, 0, Buf, Len, 10);
    Len += 10;
    Buf[Len] = Fee_UserType;
    Len++;
    System.arraycopy(FeeAddr, 0, Buf, Len, 21);
    Len += 21;
  //add by wangzm 端口短信
    //TP_pId
    Buf[Len] = 1; 
    Len++;
    //TP_udhi
    Buf[Len] = 1;
    Len++;
  //add by wangzm 端口短信
    //Msg_Fmt
    //System.out.println("此条短信为端口短信");
    Buf[Len]=8;
    Len++;
   
    System.arraycopy(SP_ID, 0, Buf, Len, 6);
    Len += 6;
    System.arraycopy(FeeType, 0, Buf, Len, 2);
    Len += 2;
    System.arraycopy(FeeCode, 0, Buf, Len, 6);
    Len += 6;
    Len += 17;
    Len += 17;
    System.arraycopy(OrgAddr, 0, Buf, Len, 21);
    Len += 21;
    Buf[Len] = 1;
    Len++;
    System.arraycopy(DestAddr, 0, Buf, Len, 21);
    Len += 21;
    Buf[Len] = (byte)(pSMSContent.length);
    Len++;   	 
    for (int i = 0; i < pSMSContent.length; i++) {
    	System.out.print(Tools.byteToHexString(pSMSContent[i]) + " ");
	}
	System.arraycopy(pSMSContent, 0, Buf, Len, pSMSContent.length);
	cmpp.send(Buf, 0, ch.getTotal_Length());
}
    
    
    public CMPPSocket getCmpp()
    {
        return cmpp;
    }

    public void setCmpp(CMPPSocket cmpp)
    {
        this.cmpp = cmpp;
    }


    public String getDBID() {
		return DBID;
	}

	public void setDBID(String dbid) {
		DBID = dbid;
	}

	public byte[] getDestAddr()
    {
        return DestAddr;
    }

    public void setDestAddr(byte destAddr[])
    {
        int i = 0;
        for(i = 0; i < 21; i++)
            DestAddr[i] = 0;

        int nLen = destAddr.length;
        if(nLen <= 21)
            for(i = 0; i < nLen; i++)
                DestAddr[i] = destAddr[i];

        else
            for(i = 0; i < 21; i++)
                DestAddr[i] = destAddr[i];

    }

    public byte getFee_UserType()
    {
        return Fee_UserType;
    }

    public void setFee_UserType(byte fee_UserType)
    {
        Fee_UserType = fee_UserType;
    }

    public byte[] getFeeAddr()
    {
        return FeeAddr;
    }

    public void setFeeAddr(byte feeAddr[])
    {
        int i = 0;
        for(i = 0; i < 21; i++)
            FeeAddr[i] = 0;

        int nLen = feeAddr.length;
        if(nLen <= 21)
            for(i = 0; i < nLen; i++)
                FeeAddr[i] = feeAddr[i];

        else
            for(i = 0; i < 21; i++)
                FeeAddr[i] = feeAddr[i];

    }

    public byte[] getFeeCode()
    {
        return FeeCode;
    }

    public void setFeeCode(byte feeCode[])
    {
        int i = 0;
        for(i = 0; i < 6; i++)
            FeeCode[i] = 0;

        int nLen = feeCode.length;
        if(nLen <= 6)
            for(i = 0; i < nLen; i++)
                FeeCode[i] = feeCode[i];

        else
            for(i = 0; i < 6; i++)
                FeeCode[i] = feeCode[i];

    }

    public byte[] getFeeType()
    {
        return FeeType;
    }

    public void setFeeType(byte feeType[])
    {
        int i = 0;
        for(i = 0; i < 2; i++)
            FeeType[i] = 0;

        int nLen = feeType.length;
        if(nLen <= 2)
            for(i = 0; i < nLen; i++)
                FeeType[i] = feeType[i];

        else
            for(i = 0; i < 2; i++)
                FeeType[i] = feeType[i];

    }

    public String getMsg_Content()
    {
        return strMsgContent;
    }

	public void setMsg_Content(byte[] msg_Content,String strMsgContent) {
		int i=0;
		int nLen = msg_Content.length;
		for (i=0;i<cMsg_Content.length;i++) cMsg_Content[i]=0;		
		
		if (nLen <= cMsg_Content.length) {
			nSMSTextContentLen=nLen;
			for (i = 0; i < nLen; i++)
				cMsg_Content[i] = msg_Content[i];
		} else {
			nSMSTextContentLen=cMsg_Content.length;
			for (i = 0; i < cMsg_Content.length; i++)
				cMsg_Content[i] = msg_Content[i];
		}
		//System.out.println(nSMSTextContentLen);
		this.strMsgContent=strMsgContent;
	}    
	
    /*
    public void setMsg_Content(byte msg_Content[], String strMsgContent)
    {
        int i = 0;
        for(i = 0; i < 140; i++)
            cMsg_Content[i] = 0;

        int nLen = msg_Content.length;
        if(nLen <= 140)
        {
            nSMSTextContentLen = nLen;
            for(i = 0; i < nLen; i++)
                cMsg_Content[i] = msg_Content[i];

        } else
        {
            nSMSTextContentLen = 140;
            for(i = 0; i < 140; i++)
                cMsg_Content[i] = msg_Content[i];

        }
        this.strMsgContent = strMsgContent;
    }
	*/
	
    public int getMsg_Len()
    {
        return nMsg_Len;
    }

    public void setMsg_Len(int msg_Len)
    {
        nMsg_Len = msg_Len;
    }

    public int getNMsgType()
    {
        return nMsgType;
    }

    public void setNMsgType(int msgType)
    {
        nMsgType = msgType;
    }

    public byte[] getOrgAddr()
    {
        return OrgAddr;
    }

    public void setOrgAddr(byte orgAddr[])
    {
        int i = 0;
        for(i = 0; i < 21; i++)
            OrgAddr[i] = 0;

        int nLen = orgAddr.length;
        if(nLen <= 21)
            for(i = 0; i < nLen; i++)
                OrgAddr[i] = orgAddr[i];

        else
            for(i = 0; i < 21; i++)
                OrgAddr[i] = orgAddr[i];

    }

    public byte[] getPushBuf()
    {
        return PushBuf;
    }

    public void setPushBuf(byte pushBuf[])
    {
        int i = 0;
        for(i = 0; i < 160; i++)
            PushBuf[i] = 0;

        int nLen = pushBuf.length;
        if(nLen <= 160)
            for(i = 0; i < nLen; i++)
                PushBuf[i] = pushBuf[i];

        else
            for(i = 0; i < 160; i++)
                PushBuf[i] = pushBuf[i];

    }

    public byte[] getService_ID()
    {
        return cServiceId;
    }

    public void setService_ID(byte service_ID[])
    {
        int i = 0;
        for(i = 0; i < 10; i++)
            cServiceId[i] = 0;

        int nLen = service_ID.length;
        if(nLen <= 10)
            for(i = 0; i < nLen; i++)
                cServiceId[i] = service_ID[i];

        else
            for(i = 0; i < 10; i++)
                cServiceId[i] = service_ID[i];

    }

    public byte[] getSP_ID()
    {
        return SP_ID;
    }

    public void setSP_ID(byte sp_id[])
    {
        int i = 0;
        for(i = 0; i < 6; i++)
            SP_ID[i] = 0;

        int nLen = sp_id.length;
        if(nLen <= 6)
            for(i = 0; i < nLen; i++)
                SP_ID[i] = sp_id[i];

        else
            for(i = 0; i < 6; i++)
                SP_ID[i] = sp_id[i];

    }

    public byte getStatusReport()
    {
        return StatusReport;
    }

    public void setStatusReport(byte statusReport)
    {
        StatusReport = statusReport;
    }

    public int getTemplen()
    {
        return templen;
    }

    public void setTemplen(int templen)
    {
        this.templen = templen;
    }

    public String getWapPushContent()
    {
        return strWapPushContent;
    }

    public void setWapPushContent(byte wapPushContent[], String s)
    {
        int i = 0;
        for(i = 0; i < 140; i++)
            cWapPushContent[i] = 0;

        int nLen = wapPushContent.length;
        if(nLen <= 140)
        {
            nWapPushContentLen = nLen;
            for(i = 0; i < nLen; i++)
                cWapPushContent[i] = wapPushContent[i];

        } else
        {
            nWapPushContentLen = 140;
            for(i = 0; i < 140; i++)
                cWapPushContent[i] = wapPushContent[i];

        }
        strWapPushContent = s;
    }

    public String getWapPushHttp()
    {
        return strWapPushHttp;
    }

    public void setWapPushHttp(byte wapPushHttp[], String s)
    {
        int i = 0;
        for(i = 0; i < 140; i++)
            cWapPushHttp[i] = 0;

        int nLen = wapPushHttp.length;
        if(nLen <= 140)
        {
            nWapPushHttpLen = nLen;
            for(i = 0; i < nLen; i++)
                cWapPushHttp[i] = wapPushHttp[i];

        } else
        {
            nWapPushHttpLen = 140;
            for(i = 0; i < 140; i++)
                cWapPushHttp[i] = wapPushHttp[i];

        }
        strWapPushHttp = s;
    }
    
    
    public byte[] getDestBytes(){
        byte[] desc = new byte[nDestAddrRealLen*21];
        int i=0;
        StringTokenizer sDescList = new StringTokenizer(DestAddrStr,",");
        while(sDescList.hasMoreTokens()){
            String mobile=sDescList.nextToken();
            if(Tools.checkMobile(mobile)){
                System.arraycopy(mobile.getBytes(),0,desc,i*21,mobile.getBytes().length);
                i++;
            }else{
            	System.out.println(mobile+"手机号码错误");
            }
        }
        nDestAddrRealLen=i;
        return desc;
    }
    
    
    public int getSeq_ID()
    {
        return ch.getSeq_ID();
    }

    public void setSeq_ID(int seq_id)
    {
        ch.setSeq_ID(seq_id);
    }

    public int getNDestAddrRealLen()
    {
        return nDestAddrRealLen;
    }

    public void setNDestAddrRealLen(int destAddrRealLen)
    {
        nDestAddrRealLen = destAddrRealLen;
    }

    public int getSendSevel()
    {
        return sendSevel;
    }

    public void setSendSevel(int sendSevel)
    {
        this.sendSevel = sendSevel;
    }

	public String getDestAddrStr() {
		return DestAddrStr;
	}

	public void setDestAddrStr(String destAddrStr) {
		DestAddrStr = destAddrStr;
	}

	public boolean isMultiFlag() {
		return multiFlag;
	}

	public void setMultiFlag(boolean multiFlag) {
		this.multiFlag = multiFlag;
	}

	public String getStrMsgContent() {
		return strMsgContent;
	}

	public void setStrMsgContent(String strMsgContent) {
		this.strMsgContent = strMsgContent;
	}

	public String getStrWapPushHttp() {
		return strWapPushHttp;
	}

	public void setStrWapPushHttp(String strWapPushHttp) {
		this.strWapPushHttp = strWapPushHttp;
	}

	public String getStrWapPushContent() {
		return strWapPushContent;
	}

	public void setStrWapPushContent(String strWapPushContent) {
		this.strWapPushContent = strWapPushContent;
	}

	public int getMsg_fmt() {
		return msg_fmt;
	}

	public void setMsg_fmt(int msg_fmt) {
		this.msg_fmt = msg_fmt;
	}

	public int getMsg_port() {
		return msg_port;
	}

	public void setMsg_port(int msg_port) {
		this.msg_port = msg_port;
	}

	public int getSend_port() {
		return send_port;
	}

	public void setSend_port(int send_port) {
		this.send_port = send_port;
	}

     
}
