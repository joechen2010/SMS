package com.cmpp.common;

import java.util.*;
import java.io.*;
public class ConfigBundle {
	private static Properties p=null;
    /**
     * init()
     */
    public ConfigBundle()
    {
    }
	public static String getString(String s) {
		if (p == null) {
			String cfgLocation = System.getProperty("Configure");
			if (cfgLocation==null || cfgLocation.length()<1){
				cfgLocation=System.getProperty("user.dir");
			}
			//int pos=cfgLocation.lastIndexOf('\\');
			//if (pos>0){
			//	cfgLocation=cfgLocation.substring(0,pos);
			//}
			cfgLocation=cfgLocation+"\\cfg\\cmpp.properties";
			try {
				InputStream in = new BufferedInputStream(new FileInputStream(
						cfgLocation));
				p = new Properties();
				p.load(in);
				in.close();
				return p.getProperty(s);
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
				p.clear();
				p=null;
				return null;
			}catch (IOException ee){
				System.out.println(ee);
				p.clear();
				p=null;
				return null;
			}
		}else{
			return p.getProperty(s,"999");
		}
	}
	public static void closeCfg(){
		if (p!=null){
			p.clear();
			p=null;
		}
	}

}
