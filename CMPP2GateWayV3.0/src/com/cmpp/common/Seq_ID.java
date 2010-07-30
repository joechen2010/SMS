package com.cmpp.common;

public class Seq_ID {
	private int Seq_ID;
	public Seq_ID(){
		resetSeq();
	}
	
	public synchronized int GetSeqID() {
		if (Seq_ID>=999999999) 
		{Seq_ID=1;} 
		else
		{Seq_ID++;}
		return Seq_ID;
	}
	
	public synchronized void resetSeq(){
		Seq_ID=0;
	}
}
