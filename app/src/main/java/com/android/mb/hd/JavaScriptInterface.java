package com.android.mb.hd;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {

    private Context mContext;
    public JavaScriptInterface(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void wxlogin() {
        Toast.makeText(mContext, "wxLogin", Toast.LENGTH_LONG).show();
    }


    @JavascriptInterface
    public void qqlogin() {
        Toast.makeText(mContext, "qqLogin", Toast.LENGTH_LONG).show();
    }


}
