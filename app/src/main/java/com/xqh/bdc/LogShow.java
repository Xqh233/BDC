package com.xqh.bdc;

public class LogShow {
    
	/*
	这玩意是真切的屎功能
	*/
    public static String TAG = LogShow.class.getSimpleName();
    
	public static String LOG="";
	public static void startLog(){
		LOG+="*********** start *************\n";
	}
	public static void endLog(){
		LOG+="*********** end *************\n";
	}
	public static void addLog(String tag,String log){
		LOG+=tag+":"+log+"\n";
	}
}
