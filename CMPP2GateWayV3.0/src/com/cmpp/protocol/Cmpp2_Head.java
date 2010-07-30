package com.cmpp.protocol;

public class Cmpp2_Head {
	private int Total_Length;
	private int Command_ID;
	private int Seq_ID;

	public Cmpp2_Head(){
	}

	public int getCommand_ID() {
		return Command_ID;
	}

	public void setCommand_ID(int command_ID) {
		Command_ID = command_ID;
	}

	public int getSeq_ID() {
		return Seq_ID;
	}

	public void setSeq_ID(int seq_ID) {
		Seq_ID = seq_ID;
	}

	public int getTotal_Length() {
		return Total_Length;
	}

	public void setTotal_Length(int total_Length) {
		Total_Length = total_Length;
	}
	
}
