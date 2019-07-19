package com.base.and.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

import static android.content.ContentValues.TAG;

/**
 * webview的通用设置，开启js，开启缓存，清除缓存等
 * Created by Makise on 2017/2/8.
 */
public class WebViewCommonSet {

    /**
     * webview开启js，开启缓存
     *
     * @param context
     * @param view
     */
    public static void setWebview(Context context, WebView view, boolean openCache) {
        WebSettings mWebSettings = view.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            mWebSettings.setDisplayZoomControls(false);
        } else {

        }
        mWebSettings.setBuiltInZoomControls(false);
//        //阻塞图片下载，使网页内容更快显示
//        mWebSettings.setBlockNetworkImage(true);
        mWebSettings.setUseWideViewPort(false);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setSupportZoom(true);
//        mWebSettings.setUserAgentString(Tools.getUserAgent(context, "and_base"));

        // 开启 DOM storage API 功能
        mWebSettings.setDomStorageEnabled(true);
        if (openCache) {
            //------------------------------------------------- 开启缓存
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
            //开启 database storage API 功能
            mWebSettings.setDatabaseEnabled(true);
            String cacheDirPath = context.getFilesDir().getAbsolutePath() + "/webcache";
            //设置数据库缓存路径
            mWebSettings.setDatabasePath(cacheDirPath);
            //开启 Application Caches 功能
            mWebSettings.setAppCacheEnabled(true);
            //设置  Application Caches 缓存目录
            mWebSettings.setAppCachePath(cacheDirPath);
            mWebSettings.setAppCacheMaxSize(8 * 1024 * 1024);
            //------------------------------------------------ 开启缓存完毕
        }
        view.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    /**
     * 清除Cookie
     *
     * @param context
     */
    public static void clearWebViewCookie(Context context) {
        // 清除cookie即可彻底清除缓存
        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().removeAllCookie();
    }

    /**
     * 清除app的WebView缓存
     */
    public static void clearWebViewCache(Context context) {
        //清理Webview缓存数据库
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(context.getFilesDir().getAbsolutePath() + "/webcache");
        Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath() + "/webviewCache");
        Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

}
