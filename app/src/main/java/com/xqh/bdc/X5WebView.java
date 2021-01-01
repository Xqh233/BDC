package com.xqh.bdc;

import android.annotation.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import com.tencent.smtt.sdk.*;

public class X5WebView extends WebView
{
	private WebViewClient client = new WebViewClient() {
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return false;
	   }
	};
	@SuppressLint("SetJavaScriptEnabled")
	public X5WebView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		Log.w("X5WebView","构造方法1");
		this.setWebViewClient(client);
		initWebViewSettings();
		//setBackgroundColor(Color.BLACK);
		this.getView().setClickable(true);
	}
	@SuppressLint("SetJavaScriptEnabled")
	public X5WebView(Context arg0, AttributeSet arg1,int arg2) {
		super(arg0, arg1,arg2);
		Log.w("X5WebView","构造方法2");
		this.setWebViewClient(client);
		initWebViewSettings();
		//setBackgroundColor(Color.BLACK);
		this.getView().setClickable(true);
	
	}

	private void initWebViewSettings() {
		WebSettings webSetting = this.getSettings();
		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setAllowFileAccess(true);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setPluginsEnabled(true);
		webSetting.setSupportMultipleWindows(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		//webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
	    webSetting.setPluginState(WebSettings.PluginState.ON);
		webSetting.setCacheMode(WebSettings.LOAD_NORMAL);
	}
}

