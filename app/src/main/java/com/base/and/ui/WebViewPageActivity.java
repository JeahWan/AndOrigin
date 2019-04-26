package com.base.and.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.base.and.Constants;
import com.base.and.JavaScriptInterface;
import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.databinding.ActivityWebviewPagerBinding;
import com.base.and.http.subscribers.ProgressDialogHandler;
import com.base.and.utils.StringUtils;
import com.base.and.utils.URLUtil;
import com.base.and.utils.WebViewCommonSet;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * webview
 * Created by Makise on 2017/2/6.
 */

public class WebViewPageActivity extends BaseActivity<ActivityWebviewPagerBinding> {
    private WebView webview;
    private JavaScriptInterface jsInterface;
    private ProgressDialogHandler mProgressDialogHandler;
    private String url_load;

    @Override
    protected View initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview_pager);
        //避免键盘遮挡输入框
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return binding.getRoot();
    }

    @Override
    protected void initData() {
        webview = binding.webview;
        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());

        url_load = getIntent().getStringExtra("url");
        if (StringUtils.isEmpty(url_load)) {
            url_load = "默认地址";
        }
        loadUrl(url_load, getExtraHeaders());
        checkParams(url_load);

        //开启js
        WebViewCommonSet.setWebview(WebViewPageActivity.this, webview, false);

        //js交互
        jsInterface = new JavaScriptInterface(webview, WebViewPageActivity.this);
        webview.addJavascriptInterface(jsInterface, JavaScriptInterface.name());

    }

    /**
     * 检查连接参数(参数控制web显示效果)
     *
     * @param url
     */
    private void checkParams(String url) {
        //检查链接包含参数
        if (url != null) {
            try {
                String isShowShare = URLUtil.getParam(url, Constants.WEBVIEW_SHARE, "no");
                if ("yes".equals(isShowShare)) {
                    //分享
                } else {
                    //无分享 再去判断是否有link（右上显示文案）
                    String isLink = URLUtil.getParam(url, Constants.WEBVIEW_LINK, "");
                    if (!TextUtils.isEmpty(isLink)) {
                        //右上显示链接跳转入口
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (webview != null) {
            webview.loadUrl(url, additionalHttpHeaders);
        }
    }

    private Map<String, String> getExtraHeaders() {
        HashMap<String, String> extraHeaders = new HashMap<>();
        //TODO 实现getToken
//        if (!TextUtils.isEmpty(getToken())) {
//            extraHeaders.put("Token", getToken());
//        } else {
//            extraHeaders.put("Token", "");
//        }
        return extraHeaders;
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
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
//            super.onPageStarted(view, url, favicon);
            //显示loading
            if (mProgressDialogHandler == null) {
                mProgressDialogHandler = new ProgressDialogHandler(WebViewPageActivity.this, true);
            }
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
            //隐藏loading
            if (mProgressDialogHandler != null) {
                mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            }
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
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }
}
