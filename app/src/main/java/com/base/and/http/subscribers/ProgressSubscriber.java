package com.base.and.http.subscribers;

import android.content.Context;
import android.text.TextUtils;

import com.base.and.base.BaseActivity;
import com.base.and.utils.NetWorkUtil;
import com.base.and.utils.ToastUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.observers.DisposableObserver;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by Makise on 16/8/2.
 */
public abstract class ProgressSubscriber<T> extends DisposableObserver<T> {

    private boolean needProgress;

    private ProgressDialogHandler mProgressDialogHandler;

    private Context context;
    private boolean showToast;

    public ProgressSubscriber(boolean needProgress) {
        this.context = BaseActivity.getContext();
        this.needProgress = needProgress;
        //默认弹toast
        this.showToast = true;
        if (needProgress) {
            mProgressDialogHandler = new ProgressDialogHandler(context, true);
        }
    }

    /**
     * 调用此方法隐藏toast提示
     *
     * @return
     */
    public ProgressSubscriber hideToast() {
        this.showToast = false;
        return this;
    }

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
        if (!this.isDisposed()) {
            this.dispose();
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        if (needProgress) {
            showProgressDialog();
        }
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        if (needProgress) {
            dismissProgressDialog();
        }
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (needProgress)
            dismissProgressDialog();
        if (!NetWorkUtil.isNetworkConnected(context)) {
            ToastUtil.showError(context, "网络异常，请检查您的网络", true);
            return;
        }
        //网络异常的不弹出
        if (e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof UnknownHostException)
            return;
        //非空 toast
        if (!TextUtils.isEmpty(e.getMessage()) && showToast) {
            ToastUtil.showError(context, e.getMessage(), false);
        }
    }
}