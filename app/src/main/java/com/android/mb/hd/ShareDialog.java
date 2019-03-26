package com.android.mb.hd;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by cgy on 19/3/26.
 */

public class ShareDialog {

    private Context mContext;
    private String mTitle;
    private String mContent;
    private String mUrl;
    private String mImageUrl;
    private PlatformActionListener mPlatformActionListener;


    private BottomSheetDialog mDialog;

    public ShareDialog(Context context, String title, String content, String url, String imageUrl,PlatformActionListener platformActionListener) {
        this.mContext = context;
        this.mTitle = title;
        this.mContent = content;
        this.mUrl = url;
        this.mImageUrl = imageUrl;
        this.mPlatformActionListener = platformActionListener;
        if (mDialog==null){
            this.mDialog = create();
        }
    }

    public void show(){
        if (mDialog!=null){
            if (mDialog.isShowing()){
                mDialog.dismiss();
            }else{
                mDialog.show();
            }
        }
    }


    public BottomSheetDialog create(){
        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        View view = View.inflate(mContext, R.layout.dialog_bottom_share, null);
        dialog.setContentView(view);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ShareAdapter());
        return dialog;
    }

    public void shareSina(){
        Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
        Platform.ShareParams shareParams = new  Platform.ShareParams();
        shareParams.setText(mTitle);
        shareParams.setUrl(mUrl);
        shareParams.setImageUrl(mImageUrl);
        shareParams.setShareType(Platform.SHARE_IMAGE);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(shareParams);
    }

    public void shareWeChat(){
        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        Platform.ShareParams shareParams = new  Platform.ShareParams();
        shareParams.setTitle(mTitle);
        shareParams.setText(mContent);
        shareParams.setUrl(mUrl);
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(shareParams);
        mDialog.dismiss();
    }

    public void shareWeChatMoments(){
        Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
        Platform.ShareParams shareParams = new  Platform.ShareParams();
        shareParams.setTitle(mTitle);
        shareParams.setText(mContent);
        shareParams.setUrl(mUrl);
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(shareParams);
    }

    public void shareQQ(){
        Platform platform = ShareSDK.getPlatform(QQ.NAME);
        Platform.ShareParams shareParams = new  Platform.ShareParams();
        shareParams.setTitle(mTitle);
        shareParams.setTitleUrl(mUrl);
        shareParams.setText(mContent);
        shareParams.setImageUrl(mImageUrl);
        platform.setPlatformActionListener(mPlatformActionListener);
        shareParams.setShareType(Platform.SHARE_IMAGE);
        platform.share(shareParams);
        mDialog.dismiss();
    }

    public void shareQzone(){
        Platform platform = ShareSDK.getPlatform(QZone.NAME);
        Platform.ShareParams shareParams = new  Platform.ShareParams();
        shareParams.setTitle(mTitle);
        shareParams.setTitleUrl(mUrl);
        shareParams.setImageUrl(mImageUrl);
        shareParams.setText(mContent);
        shareParams.setShareTencentWeibo(false);
        shareParams.setShareType(Platform.SHARE_IMAGE);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(shareParams);
        mDialog.dismiss();
    }

    public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private int[] mResId = {R.drawable.ssdk_oks_classic_sinaweibo,R.drawable.ssdk_oks_classic_wechat,R.drawable.ssdk_oks_classic_wechatmoments,
                R.drawable.ssdk_oks_classic_qq,R.drawable.ssdk_oks_classic_qzone};
        private String[] mNameArray = {"新浪微博","微信好友","微信朋友圈","QQ","QQ空间"};

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share,parent, false);;
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.icon.setImageResource(mResId[position]);
            myViewHolder.name.setText(mNameArray[position]);
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position==0){
                        shareSina();
                    }else if (position==1){
                        shareWeChat();
                    }else if (position==2){
                        shareWeChatMoments();
                    }else if (position==3){
                        shareQQ();
                    }else if (position==4){
                        shareQzone();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mNameArray.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            private ImageView icon;

            private TextView name;

            public MyViewHolder(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.share_icon);
                name = itemView.findViewById(R.id.share_name);
            }
        }
    }

}
