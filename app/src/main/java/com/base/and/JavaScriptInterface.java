package com.base.and;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * 被js调用的方法
 * Created by Makise on 2017/2/4.
 */

public class JavaScriptInterface {
    private Context mContext;
    private WebView mwebView;

    public JavaScriptInterface(WebView webView, Context context) {
        super();
        mwebView = webView;
        mContext = context;
    }

    public static String name() {
        return "_APP_NAME";
    }

    @JavascriptInterface
    public void test(String params) {
        //实现
    }
}
