package com.cmpp.main;
import com.cmpp.common.ConfigBundle;
import com.cmpp.database.*;
import com.cmpp.protocol.CMPP2_Param;
import com.cmpp.service.*;
import com.cmpp.stdout.ConsoleMonitor;

public class CMPPEntry {
	
	public static void main(String[] arg) throws Exception {	
		String IOD_IP=ConfigBundle.getString("SMC_IP");
		String IOD_Port = ConfigBundle.getString("SMC_PORT");
		String IOD_Pwd=ConfigBundle.getString("PASSWORD");
		String IOD_SPID=ConfigBundle.getString("ICPID");
		String DBIP=ConfigBundle.getString("DB_IP");
		String DBUserName=ConfigBundle.getString("DB_USER");
		String DBPwd=ConfigBundle.getString("DB_PASSWORD");
		String DBName=ConfigBundle.getString("DB_NAME");
		String RegionName=ConfigBundle.getString("REGION_NAME");		//区域名称
		int nDBType=Integer.parseInt(ConfigBundle.getString("DB_TYPE"));
		int nTcpWindowSize=Integer.parseInt(ConfigBundle.getString("TCPWINDOWS_SIZE"));
		int nTcpConnectionNums=Integer.parseInt(ConfigBundle.getString("CONNECTION_NUM"));
		int nDBBatchSendCount=Integer.parseInt(ConfigBundle.getString("DB_BATCHSENDCOUNT"));
		
		System.out.println("\n\n");
		System.out.println("                    ================                           ");
		System.out.println("O=================   CMPP 2.0 (SP端) Ver1.0  ==============O");
		System.out.println("|                   ================                          |");
		System.out.println("|            J J J J     M  M   M  M     B B B                |");
		System.out.println("|               J        M   M  M  M     B    B               |");
		System.out.println("|               J        M    M    M     B B B                |");
		System.out.println("|            J  J        M    M    M     B    B               |");
		System.out.println("|             J J        M    M    M     B B B                |");
		System.out.println("O=============================================================O");
		
		System.out.println("* SMC_IP            ="+IOD_IP);
		System.out.println("* SMC_PORT          ="+IOD_Port);
		System.out.println("* ICPID             ="+IOD_SPID);
		System.out.println("* SP_PASSWORD       ="+IOD_Pwd);
		System.out.println("* REGION_NAME       ="+RegionName);
		System.out.println("* DB_IP             ="+DBIP);
		System.out.println("* DB_USER           ="+DBUserName);
		System.out.println("* DB_PASSWORD       ="+DBPwd);
		System.out.println("* DB_DBNAME         ="+DBName);
		System.out.println("* DB_TYPE           ="+nDBType);
		System.out.println("* CPWINDOWS         ="+nTcpWindowSize);
		System.out.println("* TCPCONNECTIONNUMS ="+nTcpConnectionNums);
		
		System.out.println("==============================================================");
		
		nTcpWindowSize=16;
		
		ConsoleMonitor cm=new ConsoleMonitor(RegionName);
		DS_Params dp=new DS_Params(DBIP,DBUserName,DBPwd,DBName,nDBType,nDBBatchSendCount);
		CMPP2_Param cpp=new CMPP2_Param(IOD_SPID,IOD_Pwd,IOD_IP,Integer.parseInt(IOD_Port),(byte)0x20,nTcpWindowSize);		
		CMPPConnectMgr ccMgr=new CMPPConnectMgr(cpp,dp,cm);			
		for (int n=1;n<=nTcpConnectionNums;n++){
			ccMgr.createCMPP();
		}
		
		(new Thread(new KeyInputListen(ccMgr,cm))).start();
		ConfigBundle.closeCfg();
	}

}
