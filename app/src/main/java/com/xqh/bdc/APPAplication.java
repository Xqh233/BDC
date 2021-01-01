package com.xqh.bdc;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import com.tencent.smtt.sdk.QbSdk;

public class APPAplication extends Application 
{ 
    private QbSdk.PreInitCallback cb;
	@Override
	public void onCreate() {
		super.onCreate();
		//初始化x5webview的核心
		cb= new QbSdk.PreInitCallback() {
			@Override
			public void onViewInitFinished(boolean arg0) {
				Log.d("app", " onViewInitFinished is " + arg0);
				Toast.makeText(getApplicationContext(),"X5内核初始化"+(arg0?"成功":"失败"),0).show();
				if(!arg0){
					init();
				}
			}
			@Override
			public void onCoreInitFinished() {
			}
		};
		init();
	}
	private void init(){
		QbSdk.initX5Environment(getApplicationContext(),  cb);
	}
}

