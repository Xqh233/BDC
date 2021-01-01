package com.xqh.bdc;
import com.xqh.bdc.bean.Dynamic;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;
import org.json.JSONArray;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import org.jsoup.Connection;

public class BiliUtils {
    
	/*
	获取动态列表
	获取动态抽奖信息
	删除动态
	取关用户
	10.28
	*/
    public static String TAG = BiliUtils.class.getSimpleName();
    
	public static String cookies="";
	public static String crf="";
	public static String mid="";
	
	private static final String mobileUserAgent="Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36";
	private static final String getDynamicUrl="https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history";
	private static final String getLottUrl="https://api.vc.bilibili.com/lottery_svr/v1/lottery_svr/lottery_notice";
	private static final String modifyUrl="https://api.bilibili.com/x/relation/modify";
	private static final String removeDynamicUrl="https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/rm_dynamic";
	
	/*
	    计数
	*/
	public static int DynamicCount=0;
	/*
	 动态列表
	*/
	private static List<Dynamic> dynamics=new ArrayList<Dynamic>();
	
	/*
	    获取动态列表
	*/
	public static List<Dynamic> getDynamics(Handler handler,final String mid){
		new LoadDataThread(handler,mid).start();
		return dynamics;
	}
	
	/*
	    获取抽奖详情
	*/
	private static void getLott(String uid,Dynamic dynamic){
		try{
			Document document=Jsoup.connect(getLottUrl)
				.data("dynamic_id",dynamic.getOrigin_id())
				.ignoreHttpErrors(true)
				.ignoreContentType(true)
				.userAgent(mobileUserAgent)
				.timeout(5000)
				.get();
			String json=document.text();
			JSONObject jsonObj=new JSONObject(json);
			LogShow.startLog();
			LogShow.addLog("获取动态中奖情况","目前获取动态id:"+dynamic.getDynamic_id());
			LogShow.addLog("获取动态中奖情况",(jsonObj.getInt("code")==0)?"可获取中奖信息":"非互动动态或已过期");
			if(jsonObj.getInt("code")==0){
			jsonObj=jsonObj.getJSONObject("data");
			dynamic.setIsAfterLotteryTime(jsonObj.getInt("status")==2);
		   }else{
			   dynamic.setIsAfterLotteryTime(true);
		   }
			LogShow.addLog("获取动态中奖情况","是否过期:"+dynamic.isAfterLotteryTime());
			
		   LogShow.endLog();
		}catch(Exception e){}
		
	}
	public static void removeDynamic(final String dynamic_id){
		get(new Runnable(){
				@Override
				public void run(){
					try{
						Log.i("remove",dynamic_id);
						Connection connection=Jsoup.connect(removeDynamicUrl);
						connection.data("dynamic_id",dynamic_id)
							.data("csrf",crf)
							.data("csrf_token",crf)
							.header("Referer","https://space.bilibili.com")
							.header("content-type","application/x-www-form-urlencoded")
							.header("accept","application/json, text/plain, */*")
							.header("cookie",cookies)
							.ignoreHttpErrors(true)
							.ignoreContentType(true)
							.userAgent(mobileUserAgent)
							.method(org.jsoup.Connection.Method.POST)
							.timeout(5000);
					    Connection.Response response= connection.execute();
						LogShow.startLog();
						LogShow.addLog("删除动态","传入id:"+dynamic_id);
						LogShow.addLog("删除动态","cookies:"+cookies);
						LogShow.addLog("删除动态","csrf:"+crf);
						LogShow.addLog("删除动态","回调信息:"+response.body());
						
						LogShow.endLog();
						Log.i("remove",response.body());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
	}
	
	public static void follow(final String fid,final int type){
		get(new Runnable(){
			@Override
			public void run(){
				try{
					//7920565
					Connection connection=Jsoup.connect(modifyUrl);
					connection.data("fid",fid)
						.data("act",""+type)
						.data("csrf",crf)
						.data("re_src","11")
						.data("jsonp","jsonp")
						.header("Referer","https://space.bilibili.com")
						.header("Origin","https://space.bilibili.com")
						.header("cookie",cookies)
						.ignoreHttpErrors(true)
						.ignoreContentType(true)
						.userAgent(mobileUserAgent)
						.method(org.jsoup.Connection.Method.POST)
						.timeout(5000);
					connection.execute();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private static void get(Runnable runnable){
		Thread thread=new Thread(runnable);
		thread.start();
		try {
			thread.join();
		} catch (Exception e) {
		}
	}
	
	private static class LoadDataThread extends Thread{
		
		private static Handler handler;
		private String mid;
		private static int type=0;
		public LoadDataThread(Handler h,String m){
			this.handler=h;
			this.mid=m;
		}
		
		@Override
		public void run(){
			Log.i(TAG,"start");
			
				DynamicCount=0;
				getDynamic(mid,"");
				Message msg=new Message();
				msg.what=10001;
				msg.obj=dynamics;
				handler.sendMessage(msg);
			
		}

		
		/*
		 内部递归获取动态列表方法
		 */
		private static void getDynamic(String mid,String dynamic_id){
			try{
				Document document=Jsoup.connect(getDynamicUrl)
					.data("visitor_uid",mid)
					.data("host_uid",mid)
					.data("offset_dynamic_id",dynamic_id)
					.ignoreHttpErrors(true)
					.ignoreContentType(true)
					.userAgent(mobileUserAgent)
					.timeout(5000)
					.get();
				JSONObject jsonObj=new JSONObject(document.text()).getJSONObject("data");
				long next_offset=jsonObj.getLong("next_offset");
				int has_more=jsonObj.getInt("has_more");


				
				
				if(has_more!=0){
					JSONArray cards=jsonObj.getJSONArray("cards");
					Log.i(TAG,cards.toString());
					DynamicCount+=cards.length();
					
					Message msg=new Message();
					msg.what=10000;
					msg.obj=DynamicCount;
					handler.sendMessage(msg);
					
					for(int i=0;i<cards.length();i++){
						Dynamic dynamic=new Dynamic();
						JSONObject bean=cards.getJSONObject(i);
						JSONObject card=new JSONObject(bean.getString("card"));
						
						//Log.i("???",bean.has("dynamic_id_str")+"has dynamic");
						boolean isOrigin=(card.has("origin_extension"));
						if(isOrigin){
							String dynamic_id_str=bean.getJSONObject("desc").get("dynamic_id_str")+"";	
							dynamic.setDynamic_id(dynamic_id_str);
						}

						if(isOrigin){
							JSONObject item=card.getJSONObject("item");
							String orig_id=item.getLong("orig_dy_id")+"";
							dynamic.setOrigin_id(orig_id);
							
							JSONObject origin_user=card.getJSONObject("origin_user").getJSONObject("info");
							int uid=origin_user.getInt("uid");
							dynamic.setFid(uid);
							String name=origin_user.getString("uname");
							dynamic.setUname(name);
							getLott(""+uid,dynamic);
							
							dynamics.add(dynamic);
						}

					}
					getDynamic(mid,next_offset+"");
				}
			}catch(Exception e){e.printStackTrace();}
		}
		
	}
}
