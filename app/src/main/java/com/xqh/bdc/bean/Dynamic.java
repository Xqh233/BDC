package com.xqh.bdc.bean;

public class Dynamic {
    /*
	动态 bean类
	    只包括转发动态
		dynamic_id:转发动态id
		origin_id:原动态id
		fid:原动态发布者uid
		uname:原动态发布者用户名
	    isAfterLotteryTime:是否已开奖
		isWinLottery:是否中奖
	*/
	private String dynamic_id;
	private String origin_id;
	private int fid;
	private String uname;
	private boolean isAfterLotteryTime=false;
	private boolean isWinLottery=false;
	
	public void setDynamic_id(String dynamic_id) {
		this.dynamic_id = dynamic_id;
	}

	public String getDynamic_id() {
		return dynamic_id;
	}

	public void setOrigin_id(String origin_id) {
		this.origin_id = origin_id;
	}

	public String getOrigin_id() {
		return origin_id;
	}


	public void setFid(int fid) {
		this.fid = fid;
	}

	public int getFid() {
		return fid;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUname() {
		return uname;
	}

	public void setIsAfterLotteryTime(boolean isAfterLotteryTime) {
		this.isAfterLotteryTime = isAfterLotteryTime;
	}

	public boolean isAfterLotteryTime() {
		return isAfterLotteryTime;
	}

	public void setIsWinLottery(boolean isWinLottery) {
		this.isWinLottery = isWinLottery;
	}

	public boolean isWinLottery() {
		return isWinLottery;
	}}
