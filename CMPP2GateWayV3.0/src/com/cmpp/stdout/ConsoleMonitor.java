/**
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 文件：ConsoleMonitor.java
 * 功能：网关监控窗口
 * 
 */
package com.cmpp.stdout;
import com.cmpp.tools.Tools;
public class ConsoleMonitor extends Monitor {
	public String strRegionId=null;	//
	
	public ConsoleMonitor(String strRegionId){
		this.strRegionId=strRegionId;
	}
	public synchronized void sout(String s){
		String str="["+Tools.GetNowStr()+"] ("+strRegionId+") "+s;
		//System.out.print("\n");
		System.out.println(str);
		//System.out.print("\r                                                                   \r");
		//System.out.print(str);
	}
	public synchronized void soutNoReturn(String s){
		String str="["+Tools.GetNowStr()+"] ("+strRegionId+") "+s;
		System.out.print("\r                                                        \r");
		System.out.print(str+"\r");
	}
	public String getStrRegionId() {
		return strRegionId;
	}
	public void setStrRegionId(String strRegionId) {
		this.strRegionId = strRegionId;
	}
	
}
