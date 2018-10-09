package com.mb.duoyinggj;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;


public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;// 初始化
        AVOSCloud.initialize(this,"DAJ6LM2UjvnAeGEhi192NVpB-gzGzoHsz", "DDJoIhFCd23xRiT25TDa8In8");
        AVOSCloud.setDebugLogEnabled(true);
        AVAnalytics.enableCrashReport(this, true);
    }

    public static Context getAppContext() {
        return  mContext;
    }
}
