package com.android.mb.hd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.android.mb.hd.wxpay.WXPayUtils;

import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class JavaScriptInterface {

    private Context mContext;

    private JsCallbackHandler mCallbackHandler;

    public interface JsCallbackHandler {
        void onLoginComplete(String plat,JSONObject jsonObject);
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
    public void  callphone(String tel) {
        if (!TextUtils.isEmpty(tel)){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + tel);
            intent.setData(data);
            mContext.startActivity(intent);
        }
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

    /**
     * 登录
     * @param plat
     */
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

}
