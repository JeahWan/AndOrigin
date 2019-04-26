package com.base.and.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Description: 缓存工具类，例如图片缓存sd卡等(这里描述这个类的作用)
 * @Date 2015年9月11日 下午1:48:41
 */
public class CacheUtils {

    //默认图片保存路径
    public static final String PATHCACHE = Environment.getExternalStorageDirectory()
            + "/appName/cache/picture/";


    public static void cacheBitmapToSDCard(Bitmap bit, String name) {
        name = name + ".png";
        File fileDir = new File(PATHCACHE);
        if (!fileDir.exists()) {
            boolean bool = fileDir.mkdirs(); // 创建文件夹
        }
        File f = new File(PATHCACHE, name);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bit.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFormSDCard(String name) {
        name = name + ".png";
        File mFile = new File(PATHCACHE + name);
        //若该文件存在
        if (mFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(PATHCACHE + name);
            return bitmap;
        } else {
            return null;
        }
    }
}
