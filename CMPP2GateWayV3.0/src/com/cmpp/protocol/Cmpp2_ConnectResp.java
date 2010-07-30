package com.cmpp.protocol;

public class Cmpp2_ConnectResp {
	public int Seq_ID;
    public byte status;                 //状态 0：正确 1：消息结构错 2：非法源地址 3：认证错 4：版本太高 5：其他错误
    public byte[] MD5Pwd=new byte[16];  //ISMG认证码。认证出错时，此项为空
    public byte Version;                //服务器支持的最高版本号
    
    public Cmpp2_ConnectResp(){
    	
    }
}
