package com.base.and.base;

import android.app.Activity;
import android.app.Dialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.base.and.R;
import com.base.and.utils.DensityUtil;

public class BaseDialog<T extends ViewDataBinding> {
    private Dialog mDialog;
    private Activity mContext;
    private T binding;
    private int mWidth;
    private int mGravity;
    private onCreateView onCreateView;

    public BaseDialog(Activity context, int resId, onCreateView<T> onCreateView) {
        this.mContext = context;
        binding = DataBindingUtil.inflate(context.getLayoutInflater(), resId, null, false);
        //设置默认的关闭按钮、取消按钮事件
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        };
        if (binding.getRoot().getRootView().findViewById(R.id.iv_close) != null) {
            binding.getRoot().getRootView().findViewById(R.id.iv_close).setOnClickListener(onClickListener);
        }
        if (binding.getRoot().getRootView().findViewById(R.id.btn_left) != null) {
            binding.getRoot().getRootView().findViewById(R.id.btn_left).setOnClickListener(onClickListener);
        }
        if (binding.getRoot().getRootView().findViewById(R.id.btn_right) != null) {
            binding.getRoot().getRootView().findViewById(R.id.btn_right).setOnClickListener(onClickListener);
        }
        this.onCreateView = onCreateView;
    }

    public void build() {
        mDialog = new Dialog(mContext, R.style.Theme_AppCompat_Dialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setContentView(binding.getRoot());
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        dialogWindow.setBackgroundDrawable(null);
        //位置
        dialogWindow.setGravity(mGravity == 0 ? Gravity.CENTER : mGravity);
        //设置宽度
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = mWidth == 0 ? (int) (DensityUtil.getScreenWidth(mContext) * 0.7) : mWidth;
        dialogWindow.setAttributes(lp);
        //外部操作ui 此刻两个参数都有值了
        onCreateView.createView(binding, mDialog);
    }

    public BaseDialog setWidth(int width) {
        this.mWidth = width;
        return this;
    }

    public BaseDialog setGravity(int gravity) {
        this.mGravity = gravity;
        return this;
    }

    public void show() {
        build();
        mDialog.show();
    }

    public interface onCreateView<T> {
        void createView(T binding, Dialog dialog);
    }
}
