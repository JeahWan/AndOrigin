package com.base.and.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.base.and.App;
import com.base.and.Constants;
import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.databinding.ActivityMainBinding;
import com.base.and.ui.home.HomeActivity;
import com.base.and.utils.PackageUtils;
import com.base.and.utils.PreferencesUtils;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected View initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        return binding.getRoot();
    }

    @Override
    protected void initData() {
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 判断版本升级了,就打开引导页
        if (App.getInstance().isUpgrade()) {
            PreferencesUtils.putString(mContext, Constants.SP_VERSION_NAME,
                    PackageUtils.getAppVersionName(this)); //将当前版本号存到sp中
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            startNewActivity(intent, R.anim.fade_in_anim, R.anim.fade_out_anim, true);
        } else {
            //展示默认广告页 可以根据需求做服务端图片缓存等逻辑
            showPic(null);
        }
    }

    /**
     * 展示图片
     *
     * @param bitmap
     */
    private void showPic(Bitmap bitmap) {
        AlphaAnimation aa = new AlphaAnimation(1f, 1f);
        aa.setDuration(2000);
        if (bitmap != null) {
            binding.splashImage.setBackgroundDrawable(new BitmapDrawable(bitmap));
        } else {
            //TODO 设置闪屏图片
            binding.splashImage.setBackgroundResource(R.drawable.placeholder);
        }
        binding.splashImage.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                //这里可以做广告已展示次数的统计
            }

            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startNewActivity(intent, R.anim.fade_in_anim, R.anim.fade_out_anim, true);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }
        });
        //广告页点击事件
        binding.splashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        //屏蔽返回键 避免闪屏页退出
//        super.onBackPressed();
    }
}
