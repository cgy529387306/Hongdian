package com.android.mb.hd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.mb.hd.alipay.AlipayData;
import com.android.mb.hd.alipay.PayResult;
import com.android.mb.hd.wxpay.WXPayUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import static android.content.Context.CLIPBOARD_SERVICE;

public class JavaScriptInterface {

    private Context mContext;

    private JsCallbackHandler mCallbackHandler;

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        mCallbackHandler.aliPayResult(1);
                        Log.d("aliPay","支付成功: " + payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        mCallbackHandler.aliPayResult(0);
                        Log.d("aliPay","支付失败: " + payResult);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    public interface JsCallbackHandler {
        void onLoginComplete(String plat,JSONObject jsonObject);

        void aliPayResult(int code);
    }

    public JavaScriptInterface(Context context,JsCallbackHandler callbackHandler) {
        mContext = context;
        mCallbackHandler = callbackHandler;
    }

    @JavascriptInterface
    public void wxlogin() {
        doLogin(Wechat.NAME);
    }


    @JavascriptInterface
    public void qqlogin() {
        doLogin(QQ.NAME);
    }


    @JavascriptInterface
    public void opentaobao(String content) {
        copyToClip(content);
        ProjectHelper.openTaobao(mContext);
    }

    @JavascriptInterface
    public void openQQ(String content) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin="+content;//uin是发送过去的qq号码
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"请检查是否安装QQ",Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void copystr(String content) {
        copyToClip(content);
        Toast.makeText(mContext, "复制成功，请黏贴",
                Toast.LENGTH_SHORT).show();
    }


    @JavascriptInterface
    public void copyandopenbaidu(String url,String content) {
        try{
            copyToClip(content);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void copyToClip(String content){
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager)mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", content);
        myClipboard.setPrimaryClip(myClip);
    }

    @JavascriptInterface
    public void alipay(String orderInfo) {
        try {
            AlipayData alipayData = JsonHelper.fromJson(orderInfo,AlipayData.class);
            if (alipayData!=null && alipayData.getPayinfo()!=null){
                doAliPay(alipayData.getPayinfo().getPaymenet());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAliPay(final String orderInfo){
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask aliPay = new PayTask((Activity) mContext);
                Map<String, String> result = aliPay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

//    @JavascriptInterface
//    public void share(String json) {
//        try {
//            if (!TextUtils.isEmpty(json)){
//                JSONObject jsonObject = new JSONObject(json);
//                String title = jsonObject.getString("title");
//                String content = jsonObject.getString("content");
//                String url = jsonObject.getString("url");
//                String imageUrl = jsonObject.getString("imageUrl");
//                showShare(title,content,url,imageUrl);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @JavascriptInterface
    public void share(String title,String content,String url,String imgurl) {
        showShare(title,content,url,imgurl);
    }


    @JavascriptInterface
    public void weixinpay(String json) {
        try {
            if (!TextUtils.isEmpty(json)){
                JSONObject jsonObject = new JSONObject(json);
                String AppId = jsonObject.getString("AppId");
                String NonceStr = jsonObject.getString("NonceStr");
                String Package = jsonObject.getString("Package");
                String PartnerId = jsonObject.getString("PartnerId");
                String PrepayId = jsonObject.getString("PrepayId");
                String Sign = jsonObject.getString("Sign");
                String TimeStamp = jsonObject.getString("TimeStamp");
                doWxPay(AppId,NonceStr,Package,PartnerId,PrepayId,Sign,TimeStamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doLogin(final String plat){
        Platform platform = ShareSDK.getPlatform(plat);
        platform.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                arg2.printStackTrace();
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                if (arg0!=null && arg0.getDb()!=null && !TextUtils.isEmpty(arg0.getDb().exportData())){
                    String userInfo = arg0.getDb().exportData();
                    try {
                        JSONObject jsonObject = new JSONObject(userInfo);
                        mCallbackHandler.onLoginComplete(plat,jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        platform.authorize();
    }


    private void showShare(String title,String content,String url,String imageUrl) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(title);
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl(url);
        oks.setImageUrl(imageUrl);
        // 启动分享GUI
        oks.show(mContext);
    }

    private void doWxPay(String AppId,String NonceStr,String Package,String PartnerId,String PrepayId,String Sign,String TimeStamp){
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(AppId)
                .setPartnerId(PartnerId)
                .setPrepayId(PrepayId)
                .setPackageValue(Package)
                .setNonceStr(NonceStr)
                .setTimeStamp(TimeStamp)
                .setSign(Sign)
                .build().toWXPayNotSign(mContext);
    }

    private boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    private void openTaobao(){
        if (isAppInstalled(mContext, "com.taobao.taobao")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.view");
            intent.setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity");
            mContext.startActivity(intent);
        }
    }



}
