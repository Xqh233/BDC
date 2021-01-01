package com.xqh.bdc;
 
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xqh.bdc.MainActivity;
import com.xqh.bdc.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import com.xqh.bdc.bean.Dynamic;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends Activity { 

    /*
	 主activity
	 10.28
	*/

	
	
	private ProgressBar progressShow;
	private LinearLayout layout;
	private X5WebView x5WebView;
	private WebChromeClient xWebChromeClient;
	private WebViewClient xWebViewClient;
	

	private String url="https://passport.bilibili.com/login";
    private String curUrl="";
	private String cookies="";
	private String crf_token="";
	private String mid="";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setTheme(R.style.AppTheme);
		getActionBar().hide();
		fullScreen(this);
		setView();
		init();
    }
	
	/*
	 设置界面
	*/
	private void setView(){
		progressShow=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
		progressShow.setVisibility(View.GONE);
		progressShow.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
		x5WebView=new X5WebView(this,null);
		x5WebView.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
		
		layout=new LinearLayout(this);
		
		layout.addView(progressShow);
		layout.addView(x5WebView);
		
		setContentView(layout);
	}
	
	/*
	 初始化
	*/
	private void init(){
		
		progressShow.setMax(100);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		
		
		/*
		 网页加载进度条
		*/
		xWebChromeClient=new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView webView,int progress){
				if(progress==100){
					progressShow.setVisibility(View.GONE);
				}else{
					progressShow.setProgress(progress);
					progressShow.setVisibility(View.VISIBLE);

				}
			}
		};
		x5WebView.setWebChromeClient(xWebChromeClient);
		
		
		xWebViewClient=new WebViewClient(){
			/*
			 防止ssl错误
			 */
			@Override
			public void onReceivedSslError(WebView webView,SslErrorHandler handler,SslError error){
				handler.proceed();
				super.onReceivedSslError(webView,handler,error);
			}
			/*
			 网页加载完毕 
			 获取cookies
			 跳转用户界面
			 清除多余元素
			 */
			@Override
			public void onPageFinished(WebView webView,String target){

				super.onPageFinished(webView,target);
				curUrl=target;
				getCookies(target);
				openSpace();
				setClearScreen();
				getMid();
			}

			/*
			 拦截请求
			 解决scheme报错
			 拦截打开app按钮原h5请求
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView webview,String url){

				if( url.startsWith("http:") || url.startsWith("https:") ) {  
					return false;  
				}  
				try{
					if(url.indexOf("bilibili://space/")!=-1){
						Toast("正在获取动态列表...");
						if(mid.equals("37793909")){
							Toast("宁也配？");
						}else{
							ControlView.show(webview,mid);
						}
						return true;
					}
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  
					startActivity(intent);  
					return true;
				}catch(Exception e){e.printStackTrace();}
				return false;
			}


		};

		x5WebView.setWebViewClient(xWebViewClient);


		x5WebView.loadUrl(url);
    
	}
	

	
	/*
	 跳转到用户界面
	 */

	public void openSpace(){
		if(curUrl.equals("https://m.bilibili.com/")){
			x5WebView.loadUrl("https://m.bilibili.com/space?from=headline");
		}
	}




	/*
	 设置界面
	 关闭加载dialog
	 */
	public void setClearScreen(){
		if(curUrl.indexOf("https://m.bilibili.com/space")!=-1){
			loadJs(getFromAssets("js/setNetPageScreen.js"));
		}
	}




	/*
	 通过重定向url获取 mid
	 */
	public void getMid(){
		if(curUrl.indexOf("space/")!=-1){
			mid=(curUrl.substring(curUrl.indexOf("space/")+6));
			BiliUtils.mid=mid;
			BiliUtils.cookies=cookies;
			BiliUtils.crf=crf_token;
			new AlertDialog.Builder(this)
			.setTitle("关注up？")
			.setMessage("吼不吼哇")
			.setNegativeButton("下次一定",null)
				.setPositiveButton("彳亍", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(mid.equals("285069813")){
							Toast("自个儿关注自个儿？(迷惑)");
						}else{
							BiliUtils.follow("285069813",1);
							Toast("好！");
						}
					}
			})
			.create().show();
		}
	}





	/*
	 获取cookies以进行删除操作
	 */

	public void getCookies(String url){
		CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(url);
		if(cookie!=null&&!cookie.isEmpty()){
			cookies=cookie;
			crf_token=cookies.substring(cookies.indexOf("bili_jct")+9,cookies.indexOf(";",(cookies.indexOf("bili_jct"))));
		}
	}



	/*
	 加载js
	 */



	public void loadJs(final String js){
		runOnUiThread(new Runnable(){
				@Override
				public void run(){
					x5WebView.loadUrl("javascript:"+js);
				}});
	}






	/*
	 获取assets文件内容
	 */

	public String getFromAssets(String fileName){ 
		try { 
			InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName) ); 
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line="";
			String Result="";
			while((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		return "";
	}





	/*
	 返回监听
	 */

	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && x5WebView.canGoBack()) {
            x5WebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







	/*
	 回收
	 */


	@Override
	protected void onDestroy()
	{
		if(x5WebView!=null){
			x5WebView.stopLoading();
            x5WebView.getSettings().setJavaScriptEnabled(false);
            x5WebView.clearHistory();
            x5WebView.clearView();
            x5WebView.removeAllViews();
            x5WebView.destroy();
		}
		LogShow.LOG="";
		super.onDestroy();
	}



	
	/*
	 输出调试信息
	 */
	public void Toast(final String str){
		runOnUiThread(new Runnable(){
				@Override
				public void run(){
					Toast.makeText(MainActivity.this,str,233).show();
				}});
	}
	
	
	
	/*
	 界面全屏
	 */

	private void fullScreen(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = activity.getWindow();
				View decorView = window.getDecorView();
				int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
				decorView.setSystemUiVisibility(option);
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(Color.TRANSPARENT);
			} else {
				Window window = activity.getWindow();
				WindowManager.LayoutParams attributes = window.getAttributes();
				int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
				attributes.flags |= flagTranslucentStatus;
				window.setAttributes(attributes);
			}
		}
	}
} 
