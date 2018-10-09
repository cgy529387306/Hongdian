package com.android.mb.hd;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JsHandler {


    @JavascriptInterface
    public void wxlogin() {
        Toast.makeText(MyApplication.getAppContext(), "wxLogin", Toast.LENGTH_LONG).show();
    }


    @JavascriptInterface
    public void qqlogin() {
        Toast.makeText(MyApplication.getAppContext(), "qqLogin", Toast.LENGTH_LONG).show();
    }

}
