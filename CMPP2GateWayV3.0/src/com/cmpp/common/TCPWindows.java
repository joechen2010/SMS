package com.cmpp.common;

public class TCPWindows {
	private int WindowSize=0;	//
	private int WindowCount=0;
	private boolean bWndOpen=false;
	public TCPWindows(int WindowSize){
		this.WindowSize=WindowSize;
//		MaxWinWidth=WindowSize;
		WindowCount=0;
		bWndOpen=false;
	}
	public synchronized void resetTcpWindow(){
		WindowCount=0;
		bWndOpen=true;
	}
	public synchronized void closeWindow(){
		bWndOpen=false;
	}
	/*
	public synchronized void resetTcpWindowSize(int n){
		if (n>MaxWinWidth) {
			WindowSize=MaxWinWidth;
			return;
		}
		if (n<=0){
			WindowSize=1;
			return;
		}
		WindowSize=n;
	}
	*/
	public synchronized int changeWindow(int i){
		if (!bWndOpen) return -2;
		if (i==0)
			return WindowCount;
		if (i>0){
			if (WindowCount>=WindowSize) {
				return -1;
			}
			WindowCount++;
			return WindowCount;
		}else{
			if (WindowCount<=0){
				WindowCount=0;
				return 0;
			}
			WindowCount--;
			return WindowCount;
		}
	}
}
