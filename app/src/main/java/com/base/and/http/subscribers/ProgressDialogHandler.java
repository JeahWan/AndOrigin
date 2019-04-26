package com.base.and.http.subscribers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.base.and.R;


/**
 * 网络请求时显示的loading
 * Created by Makise on 16/8/2.
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private Context context;
    private boolean cancelable;
    private AlertDialog dialog;

    public ProgressDialogHandler(Context context, boolean cancelable) {
        super();
        this.context = context;
        this.cancelable = cancelable;
    }

    private void initProgressDialog() {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }
        if (dialog == null) {
            AlertDialog.Builder builder;
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                builder = new AlertDialog.Builder(context, R.style.dialogTransparent);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(cancelable);
        }
        View view = View.inflate(context, R.layout.dialog_loading, null);
        ImageView iv_load = (ImageView) view.findViewById(R.id.iv_load);
        Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_loading);
        operatingAnim.setInterpolator(new LinearInterpolator());
        iv_load.startAnimation(operatingAnim);
        if (!dialog.isShowing()) {
            dialog.show();
        }
        dialog.getWindow().setContentView(view);
    }

    private void dismissProgressDialog() {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

}
