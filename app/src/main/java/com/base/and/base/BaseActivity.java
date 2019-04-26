package com.base.and.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.base.and.utils.StatusBarUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * activity基类
 * Created by Makise on 2017/2/4.
 */

public abstract class BaseActivity<T> extends AppCompatActivity {
    protected static Context mContext;
    public SystemBarTintManager tintManager;
    public SystemBarTintManager.SystemBarConfig config;
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
        initTintManager();
        setPixelInsetTop(false, Color.BLUE);
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
     * 初始化沉浸式状态栏相关代码
     */
    private void initTintManager() {
        // 4.4 festuree
        // create our manager instance after the content view is set
        tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        config = tintManager.getConfig();
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
     * 设置statusBar，默认为true，即默认忽略了statusBar的高度。
     *
     * @param flag  false不顶上去
     * @param color 指定statusBar 颜色
     */
    public void setPixelInsetTop(boolean flag, int color) {
        //设置状态栏颜色
        if (flag) {
            //顶上去的
            view.setPadding(0, 0, 0, config.getPixelInsetBottom());
            tintManager.setTintResource(color);
        } else {
            //不顶上去的
            if (!StatusBarUtils.setStatusBarBlack(getWindow(), true)) {
                //小米和魅族的设置深色状态栏，不成功就直接设置成黑色状态栏
                color = Color.BLACK;
            }
            view.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
            tintManager.setTintResource(color);
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
