package com.jeahwan.origin.utils.config

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.jeahwan.origin.App
import com.jeahwan.origin.utils.CommonUtils
import java.io.File

/**
 * webview的通用设置，开启js，开启缓存，清除缓存等
 * Created by Jeah on 2017/2/8.
 */
object WebViewCommonSet {
    /**
     * webview开启js，开启缓存
     *
     * @param context
     * @param view
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun setWebview(context: Context, view: WebView, openCache: Boolean) {
        val mWebSettings = view.settings
        mWebSettings.javaScriptEnabled = true
        mWebSettings.defaultTextEncodingName = "UTF-8"
        mWebSettings.allowFileAccess = true
        mWebSettings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebSettings.displayZoomControls = false
        mWebSettings.builtInZoomControls = false
        //        //阻塞图片下载，使网页内容更快显示
//        mWebSettings.setBlockNetworkImage(true);
        mWebSettings.useWideViewPort = false
        mWebSettings.loadWithOverviewMode = true
        mWebSettings.setSupportZoom(true)

        // 开启 DOM storage API 功能
        mWebSettings.domStorageEnabled = true
        if (openCache) {
            //------------------------------------------------- 开启缓存
            mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            mWebSettings.cacheMode = WebSettings.LOAD_DEFAULT //设置 缓存模式
            //开启 database storage API 功能
            mWebSettings.databaseEnabled = true
            val cacheDirPath = context.filesDir.absolutePath + "/webcache"
            //设置数据库缓存路径
            mWebSettings.databasePath = cacheDirPath
            //开启 Application Caches 功能
            mWebSettings.setAppCacheEnabled(true)
            //设置  Application Caches 缓存目录
            mWebSettings.setAppCachePath(cacheDirPath)
            mWebSettings.setAppCacheMaxSize((8 * 1024 * 1024).toLong())
            //------------------------------------------------ 开启缓存完毕
        }
        view.setOnLongClickListener { true }
        mWebSettings.userAgentString = "${mWebSettings.userAgentString};app/Android;appVersion:${
            CommonUtils.getVersionName(App.instance)
        };appBuild:${CommonUtils.getVersionCode()}"
    }

    /**
     * 清除Cookie
     *
     * @param context
     */
    fun clearWebViewCookie(context: Context?) {
        // 清除cookie即可彻底清除缓存
        CookieSyncManager.createInstance(context)
        CookieManager.getInstance().removeAllCookie()
    }

    /**
     * 清除app的WebView缓存
     */
    fun clearWebViewCache(context: Context) {
        //清理Webview缓存数据库
        try {
            context.deleteDatabase("webview.db")
            context.deleteDatabase("webviewCache.db")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //WebView 缓存文件
        val appCacheDir = File(context.filesDir.absolutePath + "/webcache")
        Log.e(ContentValues.TAG, "appCacheDir path=" + appCacheDir.absolutePath)
        val webviewCacheDir = File(context.cacheDir.absolutePath + "/webviewCache")
        Log.e(ContentValues.TAG, "webviewCacheDir path=" + webviewCacheDir.absolutePath)

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir)
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir)
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    fun deleteFile(file: File) {
        if (file.exists()) {
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                val files = file.listFiles()
                for (i in files.indices) {
                    deleteFile(files[i])
                }
            }
            file.delete()
        }
    }
}