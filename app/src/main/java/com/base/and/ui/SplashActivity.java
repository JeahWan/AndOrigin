package com.base.and.ui;

import android.content.Intent;
import android.os.Bundle;

import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.databinding.ActivitySplashBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @Override
    public void initData() {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
