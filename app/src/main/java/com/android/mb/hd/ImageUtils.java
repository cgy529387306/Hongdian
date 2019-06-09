package com.android.mb.hd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUtils {
    public static boolean savePicture(Context context, String base64DataStr){

        // 1.去掉base64中的前缀
        String base64Str = base64DataStr.substring(base64DataStr.indexOf(",")+1, base64DataStr.length());
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "mb");// "abc":图片保存的文件夹的名称
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        // 2.拼接图片的后缀，根据自己公司的实际情况拼接，也可从base64中截取图片的格式。
        String imgName = System.currentTimeMillis() + ".png";
        File fileTest = new File(appDir, imgName);
        // 3. 解析保存图片
        byte[] data = Base64.decode(base64Str,Base64.DEFAULT);

        for (int i = 0; i < data.length; i++) {
            if(data[i] < 0){
                data[i] += 256;//调整异常数据
            }
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(fileTest);
            os.write(data);
            os.flush();
            os.close();

            // 4. 其次通知系统刷新图库
            updateAlbum(context, fileTest);
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            return true;
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }catch (IOException e){
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通知图库更新数据
     *  context
     *  fileName
     *  file
     */
    private static void updateAlbum(Context context, File file) {
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

}
