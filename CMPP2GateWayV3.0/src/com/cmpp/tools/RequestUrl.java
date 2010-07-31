package com.cmpp.tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestUrl {

	public static Boolean toSendUrl(String urlString,int request_count) {
	HttpURLConnection conn = null;
	int error_count = 0 ;
	boolean flag = false ;
	String result = "" ;
	while(error_count < request_count && !flag){
		System.out.println("第"+(error_count+1)+"次请求URL开始...");
		result = "" ;
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine())!= null){
			    result += line;
			}
				reader.close();   
			System.out.println("请求URL成功，返回结果:"+result);
			flag = true ;
		} catch (IOException e) {
				System.out.println("请求URL失败..");
				//System.out.println(e.toString());
				//e.printStackTrace();
				error_count++;
				flag = false ;
		} finally {
			 if (conn != null)
				conn.disconnect();
		}
	}

	return flag ;
}
}