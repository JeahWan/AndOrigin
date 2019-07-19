package com.base.and.ui;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.base.and.App;
import com.base.and.JavaScriptInterface;
import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.databinding.ActivityWebviewBinding;
import com.base.and.utils.WebViewCommonSet;

import java.util.HashMap;
import java.util.Map;

/**
 * webview
 * Created by Makise on 2017/2/6.
 */

public class WebViewActivity extends BaseActivity<ActivityWebviewBinding> {
    public static final String URL = "URL";
    public static final String TITLE = "TITLE";
    private String url_load;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_webview;
    }

    @Override
    public void initData() {
        //避免键盘遮挡输入框
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding.webview.setWebViewClient(new MyWebViewClient());
        binding.webview.setWebChromeClient(new MyWebChromeClient());

        url_load = getIntent().getExtras().getString(URL);
        loadUrl(url_load, getExtraHeaders());
        checkParams(url_load);

        //设置标题
        binding.titleLayout.title.setText(getIntent().getExtras().getString(TITLE, ""));
        binding.titleLayout.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //开启js
        WebViewCommonSet.setWebview(WebViewActivity.this, binding.webview, false);

        //js交互
        binding.webview.addJavascriptInterface(new JavaScriptInterface(binding.webview, WebViewActivity.this), JavaScriptInterface.name());
    }

    /**
     * 检查连接参数(参数控制web显示效果)
     *
     * @param url
     */
    private void checkParams(String url) {
        //检查链接包含参数
//        if (url != null) {
//            try {
////                String isShowShare = URLUtil.getParam(url, Constants.WEBVIEW_SHARE, "no");
////                if ("yes".equals(isShowShare)) {
////                    //分享
////                } else {
//                    //无分享 再去判断是否有link（右上显示文案）
////                    String isLink = URLUtil.getParam(url, Constants.WEBVIEW_LINK, "");
////                    if (!TextUtils.isEmpty(isLink)) {
////                        //右上显示链接跳转入口
////                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (binding.webview != null) {
            binding.webview.loadUrl(url, additionalHttpHeaders);
        }
    }

    private Map<String, String> getExtraHeaders() {
        HashMap<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Token", App.getInstance().getUser().token);
        return extraHeaders;
    }

    @Override
    public void onBackPressed() {
        if (binding.webview.canGoBack()) {
            binding.webview.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUrl(url_load, getExtraHeaders());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            //接受所有证书
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String t) {
            super.onReceivedTitle(view, t);
            binding.titleLayout.title.setText(t);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                binding.progressBar.setVisibility(View.GONE);//加载完网页进度条消失
            } else {
                binding.progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                binding.progressBar.setProgress(newProgress);//设置进度值
            }
        }
    }
}
