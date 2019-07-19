package com.base.and.base;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.base.and.App;
import com.base.and.ui.ContainerActivity;
import com.base.and.utils.MaterialDialogUtils;
import com.base.and.utils.StatusBarUtil;
import com.noober.background.BackgroundLibrary;


public abstract class BaseActivity<V extends ViewDataBinding> extends AppCompatActivity {
    protected V binding;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BackgroundLibrary.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        App.getInstance().addActivity(this);
        setStatusBar();
        initData();
    }

    /**
     * 沉浸式状态栏设置
     */
    public void setStatusBar() {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(Bundle savedInstanceState);

    /**
     * 初始化内容 获取接口数据等
     */
    public abstract void initData();

    /**
     * 显示加载框
     *
     * @param title
     */
    public void showDialog(String title) {
        if (dialog != null) {
            dialog.show();
        } else {
            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, title, true);
            dialog = builder.show();
        }
    }

    /**
     * 隐藏加载框
     */
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().removeActivity(this);
    }
}
