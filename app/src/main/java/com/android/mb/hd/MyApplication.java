package com.android.mb.hd;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;// 初始化
    }

    public static Context getAppContext() {
        return  mContext;
    }
}
