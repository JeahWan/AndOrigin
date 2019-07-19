package com.base.and.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.base.and.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by juemuren on 2018/2/10.
 */

public class ImgDownloadUtils {

    /**
     * 保存二维码到本地相册
     */
    public static void saveImageToPhotos(Context context, String url) {
        Bitmap bmp = returnBitMap(url);
        if (bmp == null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ToastUtils.showToast(App.get(), App.get().getResources().getString(R.string.save_fail));
                }
            });
            return;
        }
        // 首先保存图片
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ToastUtils.showToast(App.get(), App.get().getResources().getString(R.string.save_fail));
                }
            });
            return;
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(App.getInstance(), "保存成功");
            }
        });
    }

    /**
     * 保存二维码到本地相册
     */
    public static void saveImageToPhotos(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ToastUtils.showToast(App.get(), App.get().getResources().getString(R.string.save_fail));
                }
            });
            return;
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(App.getInstance(), "保存成功");
            }
        });
    }

    /**
     * 将URL转化成bitmap形式
     *
     * @param url
     * @return bitmap type
     */
    public static final Bitmap returnBitMap(String url) {
        URL myFileUrl;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
            HttpURLConnection conn;
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}

