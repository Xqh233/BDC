package com.xqh.bdc;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.util.Log;
import android.widget.LinearLayout;

public class WindowSize {
    public static String TAG = WindowSize.class.getSimpleName();
    private static Context mContext=null;
    private static WindowSize instance=null;
    private static DisplayMetrics mDisplayMetrics=new DisplayMetrics();
    public static int width=1920;
    public static int height=1080;
	public static int statusBarHeight=63;
	public static float density=1.0f;
	public static int dpi=160;
    private WindowSize() {
    }
	public static void setViewSize(View view,int w,int h){
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(w,h);
		view.setLayoutParams(params);
	}
	public static void setViewSize_Marign(View view,int w,int h,int m){
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(w,h);
		ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(params);
		margin.setMargins(m,m,m,m);
		view.setLayoutParams(params);
	}
	public static void setViewMargin(View view,int l,int t,int r,int b){
		ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(view.getLayoutParams());
		margin.setMargins(l,t,r,b);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(margin);
		view.setLayoutParams(params);
	}
	public static void setViewAddress(View view,int x,int y){
		ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(view.getLayoutParams());
		margin.setMargins(x,y,margin.leftMargin,margin.bottomMargin);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(margin);
		view.setLayoutParams(params);
	}
    public static void init(Context context) {
        if (instance == null) {
            instance = new WindowSize();
        }
        mContext = context;
        if (mContext != null) {
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
			statusBarHeight=getStatusBarHeight(context);
			width = mDisplayMetrics.widthPixels;
            height = mDisplayMetrics.heightPixels;
			density=mDisplayMetrics.density;
			dpi=mDisplayMetrics.densityDpi;
        }
    }
	public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}


