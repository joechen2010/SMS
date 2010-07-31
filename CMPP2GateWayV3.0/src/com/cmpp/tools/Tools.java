package com.cmpp.tools;

import java.text.DateFormat;
import java.util.Date;

/**
 * <p>Title: 通用工具</p>
 * <p>Description: 主要为类型转换</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class Tools{

    private static String HexCode[] = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "a", "b", "c", "d", "e", "f"
    };

    private Tools(){
    }

    public static String byteToHexString(byte b){
        int n = b;
        if(n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return HexCode[d1]+HexCode[d2];
    }

    public static String byteArrayToHexString(byte b[]){
        String result = "";
        for(int i = 0; i < b.length; i++)
            result = result+byteToHexString(b[i]);
        return result;
    }

    public static int byte2int(byte b[], int offset)
    {
        return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8 | (b[offset + 1] & 0xff) << 16 | (b[offset] & 0xff) << 24;
    }

    public static int byte2int(byte b[])
    {
        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;
    }

    public static long byte2long(byte b[])
    {
        return (long)b[7] & (long)255 | ((long)b[6] & (long)255) << 8 | ((long)b[5] & (long)255) << 16 | ((long)b[4] & (long)255) << 24 | ((long)b[3] & (long)255) << 32 | ((long)b[2] & (long)255) << 40 | ((long)b[1] & (long)255) << 48 | (long)b[0] << 56;
    }

    public static long byte2long(byte b[], int offset)
    {
        return (long)b[offset + 7] & (long)255 | ((long)b[offset + 6] & (long)255) << 8 | ((long)b[offset + 5] & (long)255) << 16 | ((long)b[offset + 4] & (long)255) << 24 | ((long)b[offset + 3] & (long)255) << 32 | ((long)b[offset + 2] & (long)255) << 40 | ((long)b[offset + 1] & (long)255) << 48 | (long)b[offset] << 56;
    }

    public static byte[] int2byte(int n)
    {
        byte b[] = new byte[4];
        b[0] = (byte)(n >> 24);
        b[1] = (byte)(n >> 16);
        b[2] = (byte)(n >> 8);
        b[3] = (byte)n;
        return b;
    }

/**
 *n 为待转数据，buf[]为转换后的数据，offset为buf[]中转换的起始点
 * 转换后数据从低到高位
 */
    public static void int2byte(int n, byte buf[], int offset)
    {
        buf[offset] = (byte)(n >> 24);
        buf[offset + 1] = (byte)(n >> 16);
        buf[offset + 2] = (byte)(n >> 8);
        buf[offset + 3] = (byte)n;
    }

    public static byte[] short2byte(int n)
    {
        byte b[] = new byte[2];
        b[0] = (byte)(n >> 8);
        b[1] = (byte)n;
        return b;
    }

    public static void short2byte(int n, byte buf[], int offset)
    {
        buf[offset] = (byte)(n >> 8);
        buf[offset + 1] = (byte)n;
    }

    public static byte[] long2byte(long n)
    {
        byte b[] = new byte[8];
        b[0] = (byte)(int)(n >> 56);
        b[1] = (byte)(int)(n >> 48);
        b[2] = (byte)(int)(n >> 40);
        b[3] = (byte)(int)(n >> 32);
        b[4] = (byte)(int)(n >> 24);
        b[5] = (byte)(int)(n >> 16);
        b[6] = (byte)(int)(n >> 8);
        b[7] = (byte)(int)n;
        return b;
    }
    public static void long2byte(long n, byte buf[], int offset) {
        buf[offset] = (byte) (int) (n >> 56);
        buf[offset + 1] = (byte) (int) (n >> 48);
        buf[offset + 2] = (byte) (int) (n >> 40);
        buf[offset + 3] = (byte) (int) (n >> 32);
        buf[offset + 4] = (byte) (int) (n >> 24);
        buf[offset + 5] = (byte) (int) (n >> 16);
        buf[offset + 6] = (byte) (int) (n >> 8);
        buf[offset + 7] = (byte) (int) n;
      }
    public static boolean checkMobile(String sMobile){
        String sF2="";
        if(sMobile == null)
            return false;
        if(sMobile.length()!= 11)
            return false;
        sF2 = sMobile.substring(0,1);
        try {
            if(sF2.equals("1"))
                return true;
            else
                return false;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static byte[] HexStrToBytes(String HexStr){//把十六进制度字符串按其字面表示的值写入Byte数组里,字符串是两个一组，比如：0F0A0CFFCC
    	byte Re[]=new byte[HexStr.length() / 2];
    	for (int n=1;n<=HexStr.length()-1;n+=2){
    		Re[n/2]=(byte) (HexToInt(HexStr.substring(n-1,n))*16+HexToInt(HexStr.substring(n,n+1)));
    	}
		return Re;
    }
    public static String DecodeCMPPMsgID(long n)
    {   	
    	byte abyte0[]=long2byte(n);
        StringBuffer stringbuffer = new StringBuffer();
        byte byte0 = 0;
        int l1 = 0;
        byte0 = abyte0[0];
        int j = (byte)(byte0 >>> 4) & 0xf;
        byte0 = abyte0[0];
        byte0 <<= 4;
        int k = (byte)(byte0 >>> 3);
        k &= 0x1f;
        byte0 = abyte0[1];
        byte0 >>>= 7;
        byte0 &= 1;
        k += byte0;
        k &= 0x1f;
        byte0 = abyte0[1];
        byte0 <<= 1;
        int l = (byte)(byte0 >>> 3);
        l &= 0x1f;
        byte0 = abyte0[1];
        byte0 <<= 6;
        int i1 = (byte)(byte0 >>> 2);
        i1 &= 0x3f;
        byte0 = abyte0[2];
        byte0 >>>= 4;
        i1 += byte0;
        i1 &= 0x3f;
        byte0 = abyte0[2];
        byte0 <<= 4;
        int j1 = (byte)(byte0 >>> 2);
        j1 &= 0x3f;
        byte0 = abyte0[3];
        byte0 >>>= 6;
        j1 += byte0;
        j1 &= 0x3f;
        byte0 = abyte0[3];
        byte0 <<= 2;
        byte0 >>>= 2;
        byte0 &= 0x3f;
        l1 = abyte0[4] & 0xff;
        int i = byte0 * 0x10000 + l1 * 256;
        l1 = abyte0[5] & 0xff;
        i += l1;
        i &= 0x3fffff;
        byte0 = abyte0[6];
        l1 = byte0;
        l1 &= 0xff;
        int k1 = l1 * 256;
        byte0 = abyte0[7];
        l1 = byte0;
        l1 &= 0xff;
        k1 += l1;
        k1 &= 0xffff;
        stringbuffer.setLength(0);
        if(j < 10)
            stringbuffer.append("0");
        stringbuffer.append(j);
        if(k < 10)
            stringbuffer.append("0");
        stringbuffer.append(k);
        if(l < 10)
            stringbuffer.append("0");
        stringbuffer.append(l);
        if(i1 < 10)
            stringbuffer.append("0");
        stringbuffer.append(i1);
        if(j1 < 10)
            stringbuffer.append("0");
        stringbuffer.append(j1);
        stringbuffer.append(FormatInt(i, 5));
        stringbuffer.append(FormatInt(k1, 5));
        return stringbuffer.toString();
    }

    private static String FormatInt(int i, int j)
    {
        String s;
        for(s = String.valueOf(i); s.length() < j; s = "0" + s);
        return s;
    }
    public static int HexToInt(String Hex){//把单个字符0-F转成对应的int
    	String lHex=Hex;
    	lHex=Hex.toUpperCase();
    	int n=0;
    	if (lHex=="0") n= 0;
    	else if (lHex.equals("1")) n= 1;
    	else if (lHex.equals("2")) n= 2;
    	else if (lHex.equals("3")) n= 3;
    	else if (lHex.equals("4")) n= 4;
    	else if (lHex.equals("5")) n= 5;
    	else if (lHex.equals("6")) n= 6;
    	else if (lHex.equals("7")) n= 7;
    	else if (lHex.equals("8")) n= 8;
    	else if (lHex.equals("9")) n= 9;
    	else if (lHex.equals("A")) n= 10;
    	else if (lHex.equals("B")) n= 11;
    	else if (lHex.equals("C")) n= 12;
    	else if (lHex.equals("D")) n= 13;
    	else if (lHex.equals("E")) n= 14;
    	else if (lHex.equals("F")) n= 15;   
    	return n;
    }
    
    public static int EndPos(byte[] Buf){  //当把BYTE[]转成字符串时，返回0的位置，截断后面的数据
		int n=0;
		for (n=0;n<Buf.length;n++){
			if (Buf[n]==0) {break;}
		}
		return n;
	}
    
    //日期相关函数
    public static String GetNowStr(){
    	String NowStr="";
		Date date=new Date();
		DateFormat df=DateFormat.getDateTimeInstance();
    	NowStr=df.format(date);
    	return NowStr;
    }
    
    
}