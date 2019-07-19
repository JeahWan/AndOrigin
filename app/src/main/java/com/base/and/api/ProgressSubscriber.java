package com.base.and.api;

import android.content.Context;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.base.and.App;
import com.base.and.utils.MaterialDialogUtils;
import com.base.and.utils.NetWorkUtil;
import com.base.and.utils.ToastUtils;

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

    private MaterialDialog dialog;

    private Context context;
    private boolean showToast;

    public ProgressSubscriber() {
        this(false);
    }

    public ProgressSubscriber(boolean needProgress) {
        this.context = App.getInstance();
        this.needProgress = needProgress;
        //默认弹toast
        this.showToast = true;
        if (needProgress) {
            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(App.getInstance().getCurrentActivity(), "加载中", true);
            dialog = builder.show();
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
        if (dialog != null) {
            dialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
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
     * 请求成功
     * 只是想换个方法名 所以包装一下
     *
     * @param t
     */
    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    public abstract void onSuccess(T t);

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
            ToastUtils.showError(App.getInstance(), "网络异常，请检查您的网络", false);
            return;
        }
        //网络异常的不弹出、code 3 是单点登录被踢掉，不要显示toast，会把json串打出来。
        if (e instanceof SocketTimeoutException ||
                e instanceof ConnectException ||
                e instanceof UnknownHostException ||
                (e instanceof ResultException && ((ResultException) e).errorCode == 3))
            return;
        //非空 toast
        if (!TextUtils.isEmpty(e.getMessage()) && showToast) {
            ToastUtils.showError(App.getInstance(), e.getMessage(), false);
        }
    }
}