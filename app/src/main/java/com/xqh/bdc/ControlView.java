package com.xqh.bdc;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.xqh.bdc.bean.Dynamic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.util.Log;
import android.widget.TextView;

public class ControlView {
    
    public static String TAG = ControlView.class.getSimpleName();
    
	private static Context context;
	
	private static Dialog dialog;
	private static ProgressDialog pDialog;
	private static String mid="";
	
	public static boolean isUnFollow=false;
	
	public static String alwayFollwer="";
	/*
	    常量应放置另一静态类管理
		需要补完一个用户搜索
		需要以直观方式添加删除自动取关的过滤用户
	*/
	
	public static void show(View view,String m){
		context=view.getContext();
		SharedPreferencesUtils.init(context);
		WindowSize.init(context);
		 mid=m;
		isUnFollow=SharedPreferencesUtils.getBoolean("isUnFollow",false);
		alwayFollwer=SharedPreferencesUtils.getString("alwayFollwer","");
		
		final RelativeLayout layout=new RelativeLayout(context);
		layout.setBackgroundColor(0xffe0e0e0);
		
		layout.setGravity(17);
		Button openButton=addButton("开始清理动态");
		openButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					solvingView();
				}
			});
		layout.addView(openButton);
		WindowSize.setViewAddress(openButton,0,WindowSize.height*(1/2));
		
		
		Button settingButton=addButton("设置");
		settingButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					settingView();
				}
			});
		layout.addView(settingButton);
		WindowSize.setViewAddress(settingButton,0,WindowSize.height*(1/2)+(WindowSize.height/7)*1);
		
		Button log=addButton("查看运行Log");
		log.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					logView();
				}
			});
		layout.addView(log);
		WindowSize.setViewAddress(log,0,WindowSize.height*(1/2)+(WindowSize.height/7)*3);
		
		Button closeButton=addButton("关闭窗口");
		closeButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
		});
		layout.addView(closeButton);
		WindowSize.setViewAddress(closeButton,0,WindowSize.height*(1/2)+(WindowSize.height/7)*4);
		
		
		layout.post(new Runnable(){
			public void run(){
		    CircularAnim.show(layout).duration(1236).go();
		    }
		});
		
		dialog=new Dialog(context,R.style.DialogTheme);
		dialog.setContentView(layout);
		Window window = dialog.getWindow();
		window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL); 
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); 
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity=Gravity.TOP;
		params.dimAmount = 0.0f;
        window.setAttributes(params);
        dialog.setCanceledOnTouchOutside(false);
		dialog.create();
		dialog.show();
	}
	
	private static void solvingView(){
		pDialog=new ProgressDialog(context);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setTitle("加载动态数据中...");
		pDialog.setIndeterminate(true);
		pDialog.setMessage("开始加载");
		pDialog.setCancelable(false);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.create();
		pDialog.show();
		BiliUtils.getDynamics(handler,mid);
	}
	private static int unfollowCount=0;
	private static int removeCount=0;
	private static int refollowCount=0;
	
	private static Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
				case 10000:
					if(pDialog!=null){
						pDialog.setMessage("已加载"+msg.obj+"条动态...");
					}
				break;
				case 10001:
					if(pDialog!=null){
						pDialog.setMessage("开始清理动态");
						List<Dynamic> list=(ArrayList<Dynamic>)msg.obj;
						
						List uids=Arrays.asList(alwayFollwer.split(","));
						if(uids==null||uids.size()==0){
							if(isNumber(alwayFollwer)){
								uids=new ArrayList();
								uids.add(alwayFollwer);
							}
						}else{
							Toast("加载非自动取关用户数据...");
						}
							
						unfollowCount=0;
						removeCount=0;
						refollowCount=0;
						Map<String,Dynamic> unfollowList=new HashMap<String,Dynamic>();
						
						Map<String,Dynamic> refollowList=new HashMap<String,Dynamic>();
						for(Dynamic dynamic:list){
							if(dynamic.isAfterLotteryTime()){
								BiliUtils.removeDynamic(dynamic.getDynamic_id());
								removeCount++;
								if(isUnFollow){
									if(!uids.contains(""+dynamic.getFid())){
										unfollowList.put(""+dynamic.getFid(),dynamic);
										
										BiliUtils.follow(""+dynamic.getFid(),2);
									}
								}
							}else{
								Log.i("don't remove",dynamic.getDynamic_id());
								refollowList.put(dynamic.getFid()+"",dynamic);
							}
						}
						for(Dynamic dynamic:unfollowList.values()){
							unfollowCount++;
						}
						if(unfollowCount!=0){
						for(Dynamic dynamic:refollowList.values()){
							refollowCount++;
							BiliUtils.follow(""+dynamic.getFid(),1);
						}
						}
						if(unfollowCount==0){
							refollowCount=0;
						}
						if(refollowCount>unfollowCount){
							refollowCount=unfollowCount;
						}
						Toast("已删除"+removeCount+"条已开奖动态，取关"+unfollowCount+"名up主,又关注回"+refollowCount+"名up主");
						pDialog.dismiss();
					}
				break;
			}
		}
	};
	
	
	
	public static boolean isNumber(String str){
        if(str!=null&&!str.isEmpty()&&!str.equals("")){
            Pattern pattern=Pattern.compile("^-?\\d+$",Pattern.CASE_INSENSITIVE);
            Matcher matcher=pattern.matcher(str);
            return matcher.matches();
        }
        return false;
	}
	private static void logView(){
		Dialog dialog=new Dialog(context,R.style.DialogTheme);
		EditText log=new EditText(context);
		log.setText(LogShow.LOG.isEmpty()?"毛都没有":LogShow.LOG);
		log.setTextSize(15);
		
		log.setGravity(Gravity.TOP);
		log.setBackgroundColor(0xfffafafa);
		WindowSize.setViewSize(log,-1,-1);
        
		
		Window window = dialog.getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowSize.width/2;
        params.height = WindowSize.width/2;
        window.setAttributes(params);
		dialog.setContentView(log);
		dialog.setTitle("设置(未补完)");
		dialog.create();
		dialog.show();
	}
	private static void settingView(){
		Dialog dialog=new Dialog(context,R.style.DialogTheme);
		LinearLayout layout=new LinearLayout(context);
		layout.setBackgroundColor(0xfffafafa);
		layout.setOrientation(1);
		
		CheckBox isUnFllowCheckBox= addCheckBox("自动取关",isUnFollow);
		isUnFllowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton cb, boolean bool) {
					isUnFollow=bool;
					SharedPreferencesUtils.commitBoolean("isUnFollow",isUnFollow);
				}
			});
			
		layout.addView(isUnFllowCheckBox);
		
		final EditText input=new EditText(context);
		input.setHint("以逗号隔开不自动取关的uid");
		alwayFollwer=SharedPreferencesUtils.getString("alwayFollwer","");
		
		input.setText(alwayFollwer);
		Button save=addButton("保存输入内容");
		save.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SharedPreferencesUtils.commitString("alwayFollwer",input.getText().toString());
					Toast("已保存输入");
				}
			});
		layout.addView(input);
		layout.addView(save);
		
		Window window = dialog.getWindow();
		
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowSize.width/2;
        params.height = WindowSize.width/2;
        
        window.setAttributes(params);
        
		dialog.setContentView(layout);
		dialog.setTitle("设置(未补完)");
		dialog.create();
		dialog.show();
	}
	private static void Toast(final String str){
		((Activity)context).runOnUiThread(new Runnable(){
				@Override
				public void run(){
					Toast.makeText(context,str,233).show();
				}});
	}
	
	private static CheckBox addCheckBox(String text,boolean flag){
		CheckBox checkBox=new CheckBox(context);
		checkBox.setText(text);
		checkBox.setChecked(flag);
		
		return checkBox;
	}
	private static Button addButton(String text){
		Button button=new Button(context);
		button.setText(text);
	    button.setTextSize(21);
		button.setBackgroundColor(0xfffa7298);
		button.setTextColor(0xfffafafa);
		WindowSize.setViewSize(button,WindowSize.width,WindowSize.height/8);
		WindowSize.setViewMargin(button,32,32,32,32);
		return button;
	}
	
	
	
	
}
