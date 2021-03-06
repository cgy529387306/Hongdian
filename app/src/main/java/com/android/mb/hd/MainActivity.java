package com.android.mb.hd;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class MainActivity extends AppCompatActivity implements JavaScriptInterface.JsCallbackHandler{
    private WebView webView;
    private String webUrl = "http://www.hongdian888.com/hbapp/index.html";

    public static final int FILE_CHOOSER_RESULT_CODE = 5173;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    public static final String PAY_RESULT_ACTION = "LOCAL_BROADCAST_PAY_RESULT_ACTION";

    private LocalBroadcastManager mLocalBroadcastManager;
    /**
     * 更新用户信息广播接受者
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if (PAY_RESULT_ACTION.equals(intent.getAction())){
                notifyPayResult(intent.getIntExtra("result",0));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(PAY_RESULT_ACTION));
        setContentView(R.layout.activity_main);
        initWebView();
    }



    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            webView.loadUrl("about:blank");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return TextUtils.isEmpty(url) || super.shouldOverrideUrlLoading(webView, url);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        webView.destroy();
        webView = null;
    }

    // region 双击返回
    private static final long DOUBLE_CLICK_INTERVAL = 2000;
    private long mLastClickTimeMills = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
            webView.evaluateJavascript("javascript:getPageStatus()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if ("1".equals(value)){
                        if (System.currentTimeMillis() - mLastClickTimeMills > DOUBLE_CLICK_INTERVAL) {
                            Toast.makeText(MainActivity.this,"再按一次返回退出",Toast.LENGTH_SHORT).show();
                            mLastClickTimeMills = System.currentTimeMillis();
                        }else{
                            finish();
                        }
                    }else{
                       loadJs("javascript:routerPageBack()");
                    }
                }
            });
            return true;
        }
        return false;
    }


    @SuppressLint({"CommitPrefEdits", "AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView(){
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(webViewClient);
        setWebChromeClient();
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);//允许使用js
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("deviceType","1");
        webView.loadUrl(webUrl,headerMap);
        webView.addJavascriptInterface(new JavaScriptInterface(this,this),
                "android");
    }

    @Override
    public void onLoginComplete(String plat, JSONObject jsonObject) {
        if (jsonObject!=null){
            if (Wechat.NAME.equals(plat)){
                wxLoginComplete(jsonObject);
            }else if (QQ.NAME.equals(plat)){
                qqLoginComplete(jsonObject);
            }
        }
    }

    private void setWebChromeClient(){
        webView.setWebChromeClient(new WebChromeClient(){
            // For 3.0+ Devices (Start)
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                uploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILE_CHOOSER_RESULT_CODE);
            }
            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {

                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;
                }
                uploadMessageAboveL = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
                } catch (ActivityNotFoundException e) {
                    uploadMessageAboveL = null;
                    Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                uploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILE_CHOOSER_RESULT_CODE);
            }

            //for Android <3.0
            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                uploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, true);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }


        });
    }

    private void wxLoginComplete(JSONObject jsonObject){
        JSONObject jsonObj = new JSONObject();
        try {
            HashMap<String,String> userInfo = new HashMap<>();
            userInfo.put("nickname",jsonObject.getString("nickname"));
            userInfo.put("unionid",jsonObject.getString("unionid"));
            userInfo.put("headingurl",jsonObject.getString("icon"));
            userInfo.put("token",jsonObject.getString("token"));
            jsonObj = new JSONObject(userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsStr = "javascript:wxLoginComplete('" + jsonObj.toString() + "')";
        loadJs(jsStr);
    }

    private void qqLoginComplete(JSONObject jsonObject){
        JSONObject jsonObj = new JSONObject();
        try {
            HashMap<String,String> userInfo = new HashMap<>();
            userInfo.put("nickname",jsonObject.getString("nickname"));
            userInfo.put("openid",jsonObject.getString("userID"));
            userInfo.put("figureurl_1",jsonObject.getString("icon"));
            userInfo.put("figureurl_2",jsonObject.getString("iconQzone"));
            userInfo.put("token",jsonObject.getString("token"));
            jsonObj = new JSONObject(userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsStr = "javascript:qqLoginComplete('" + jsonObj.toString() + "')";
        loadJs(jsStr);
    }

    private void loadJs(final String jsStr){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(jsStr);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR_MR1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                if (uploadMessageAboveL == null)
                    return;
                uploadMessageAboveL.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessageAboveL = null;
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            uploadMessage.onReceiveValue(result);
            uploadMessage = null;
        } else {
            Toast.makeText(getBaseContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
        }
    }

    public void aliPayResult(int code) {
        notifyPayResult(code);
    }

    @Override
    public void shareComplete(String id) {
        String jsStr = "javascript:shareComplete('" + id + "')";
        loadJs(jsStr);
    }


    private void notifyPayResult(int code){
        if (!TextUtils.isEmpty(JavaScriptInterface.mOrderId)){
            //1:成功 0:失败
            int result = code==1?10:-1;
            String jsStr = "javascript:getOrderstatus('" + JavaScriptInterface.mOrderId+"','" + result + "')";
            loadJs(jsStr);
        }
    }


}
