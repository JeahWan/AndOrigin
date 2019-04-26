package com.base.and.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.base.and.App;
import com.base.and.R;
import com.base.and.ui.home.HomeActivity;
import com.base.and.utils.PackageUtils;
import com.base.and.utils.StatusBarUtil;

/**
 * activity基类
 * Created by Makise on 2017/2/4.
 */

public abstract class BaseActivity<T> extends AppCompatActivity {
    protected static Context mContext;
    protected T binding;
    private App application;
    private long mExitTime = 0;
    private View view;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        application = App.getInstance();
        view = initBinding();
        application.addActivity(this);
        setStatusBar();
        initData();
    }

    /**
     * 初始化databinding
     *
     * @return
     */
    protected abstract View initBinding();

    /**
     * 数据填充
     */
    protected abstract void initData();

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
     * activity跳转
     *
     * @param intent
     * @param isFinish
     */
    public void startNewActivity(Intent intent, boolean isFinish) {
        startNewActivity(intent, R.anim.push_left_in, R.anim.push_left_out, isFinish);
    }

    /**
     * activity按照一定的动画效果跳转
     *
     * @param intent
     * @param enterAnim
     * @param exitAnim
     * @param isFinish
     */
    public void startNewActivity(Intent intent, int enterAnim, int exitAnim, boolean isFinish) {
        startActivity(intent);
        overridePendingTransition(enterAnim, exitAnim);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 双击返回键退出app
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (PackageUtils.isTopActivity(this, HomeActivity.class.getName())) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();
                    } else {
                        application.exit();
                    }
                }
                return true;
            }
            mExitTime = 0;
            return super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 默认退出动画
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
