/*
 * Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 创建日期 2010-7-19
 */
package com.cmpp.common;

public final class Controller {
	private static boolean bRun=true;
	private static boolean bDbCanClose=false;
	public static boolean isBRun() {
		return bRun;
	}

	public synchronized static void setBRun(boolean run) {
		bRun = run;
	}
	public synchronized static boolean getDbCanClose(){
		return bDbCanClose;
	}
	public synchronized static void setDbCanClose(boolean b){
		bDbCanClose=b;
	}
}
