package com.cmpp.protocol;

import java.io.DataOutputStream;
import java.io.IOException;

import com.cmpp.common.Const;
import com.cmpp.service.CMPPSocket;




public class Logout {
	private int Total_Length;
	private int Command_ID;
	public int Seq_ID;
	public Logout(){
		
	}
	public void send(CMPPSocket cmpp) throws IOException{
		Command_ID=Const.COMMAND_CMPP_TERMINATE;
		Total_Length=12;
		DataOutputStream dis=new DataOutputStream(cmpp.getOutputStream());
		dis.writeInt(Total_Length);
		dis.writeInt(Command_ID);
		dis.writeInt(Seq_ID);
	}
}
