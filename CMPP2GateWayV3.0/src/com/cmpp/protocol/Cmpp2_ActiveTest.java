package com.cmpp.protocol;
import java.io.IOException;

import com.cmpp.common.Const;
import com.cmpp.service.CMPPSocket;
import com.cmpp.tools.Tools;




public class Cmpp2_ActiveTest {
	private int Total_Length;
	private int Command_ID;
	public int Seq_ID;
	
	public Cmpp2_ActiveTest(){
	}
	
	
	
	public void send(CMPPSocket cmpp) throws IOException{
		int Len=0;
		byte[] Buf=new byte[1000];
		Command_ID=Const.COMMAND_CMPP_ACTIVE_TEST;
		Total_Length=12;
		
		System.arraycopy(Tools.int2byte(Total_Length),0,Buf,Len,4);
		Len=Len+4;
		System.arraycopy(Tools.int2byte(Command_ID),0,Buf,Len,4);
		Len=Len+4;
		System.arraycopy(Tools.int2byte(Seq_ID),0,Buf,Len,4);
		Len=Len+4;
		cmpp.send(Buf,0,Len);
	}
}
