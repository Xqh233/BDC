package com.xqh.bdc;


import android.content.SharedPreferences;
import android.content.Context;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
public class SharedPreferencesUtils{
    private static SharedPreferences mSharedPreferences = null;
    private static Editor mEditor = null;

    public static void init(Context context){
        if( mSharedPreferences == null ){
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void removeKey(String key){
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

    public static void removeAll(){
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

    public static void commitString(String key,String value){
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key,value);
        mEditor.commit();
    }
    public static String getString(String key , String value){
        return mSharedPreferences.getString(key,value);
    }
    public static void commitInt(String key,int value){
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key,value);
        mEditor.commit();
    }

    public static int getInt(String key , int value){
        return mSharedPreferences.getInt(key ,  value);
    }

    public static void commitLong(String key,long value){
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key,value);
        mEditor.commit();
    }

    public static Long getLong(String key , long value){
        return mSharedPreferences.getLong( key ,  value);
    }

	public static void commitBoolean(String key,boolean value){
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key,value);
        mEditor.commit();
    }

    public static boolean getBoolean(String key , boolean value){
        return mSharedPreferences.getBoolean( key ,value);
    }
}


