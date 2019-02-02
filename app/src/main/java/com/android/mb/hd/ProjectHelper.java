package com.android.mb.hd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by cgy on 19/2/2.
 */

public class ProjectHelper {
    /**
     * 检测该包名所对应的应用是否存在
     *
     * @param packageName
     * @return
     */
    private static boolean checkPackage(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    public static void openTaobao(Context context){
        try {
            Intent intent2 = new Intent();
            intent2.setAction(Intent.ACTION_VIEW);
            Uri uri;
            if (checkPackage("com.taobao.taobao")) {
                uri = Uri.parse(context.getString(R.string.taobao_app_shop));
            }else{
                uri = Uri.parse(context.getString(R.string.taobao_web_shop));
            }
            intent2.setData(uri);
            context.startActivity(intent2);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
