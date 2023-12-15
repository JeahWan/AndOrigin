package com.jeahwan.origin.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeahwan.origin.R
import com.jeahwan.origin.base.BaseActivity
import com.jeahwan.origin.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun initData() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onBackPressed() {
//        super.onBackPressed();
    }
}