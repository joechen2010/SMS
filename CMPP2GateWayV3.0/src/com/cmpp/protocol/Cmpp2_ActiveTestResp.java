package com.cmpp.protocol;
import java.io.IOException;

import com.cmpp.common.Const;
import com.cmpp.service.CMPPSocket;
import com.cmpp.tools.Tools;
public class Cmpp2_ActiveTestResp {
		public byte Reserve;
		private int Seq_ID;	

		public int getSeq_ID() {
			return Seq_ID;
		}

		public void setSeq_ID(int seq_ID) {
			Seq_ID = seq_ID;
		}

		public void send(CMPPSocket cmpp) throws IOException{
			byte[] buff=new byte[13];
			System.arraycopy(Tools.int2byte(13), 0, buff, 0, 4);
			System.arraycopy(Tools.int2byte(Const.COMMAND_CMPP_ACTIVE_TEST_REP), 0, buff, 4, 4);
			System.arraycopy(Tools.int2byte(Seq_ID), 0, buff, 8, 4);
			buff[12]=1;
			cmpp.send(buff,0,13);
		}
}
