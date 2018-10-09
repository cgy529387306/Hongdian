package com.mb.duoyinggj;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOnlineConfigureListener;
import com.mb.duoying.R;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private String webUrl;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
        initUrlConfig();
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
        webView.destroy();
        webView = null;
    }

    // region 双击返回
    private static final long DOUBLE_CLICK_INTERVAL = 2000;
    private long mLastClickTimeMills = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
            if (webView.canGoBack()) {
                webView.goBack(); //goBack()表示返回WebView的上一页面
                return true;
            }else {
                if (System.currentTimeMillis() - mLastClickTimeMills > DOUBLE_CLICK_INTERVAL) {
                    Toast.makeText(MainActivity.this,"再按一次返回退出",Toast.LENGTH_SHORT).show();
                    mLastClickTimeMills = System.currentTimeMillis();
                    return true;
                }
                finish();
                return true;
            }
        }
        return false;
    }

    private void initUrlConfig(){
        AVAnalytics.setOnlineConfigureListener(new AVOnlineConfigureListener() {
            @Override
            public void onDataReceived(JSONObject data) {
                String requestUrl = AVAnalytics.getConfigParams(MainActivity.this, "requestUrl");
                if (!TextUtils.isEmpty(requestUrl) && !requestUrl.equals(webUrl)){
                    webUrl = requestUrl;
                    editor.putString("requestUrl", webUrl);
                    editor.apply();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(webUrl);
                    }
                });
            }
        });
        AVAnalytics.updateOnlineConfig(MainActivity.this);
    }

    @SuppressLint("CommitPrefEdits")
    private void initWebView(){
        sharedPreferences = getSharedPreferences("url_config", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        webUrl = sharedPreferences.getString("requestUrl", "https://dy866.com");

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(webViewClient);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
    }
}
