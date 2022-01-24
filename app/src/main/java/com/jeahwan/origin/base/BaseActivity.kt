package com.jeahwan.origin.base

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.ktx.immersionBar
import com.jeahwan.origin.BuildConfig
import com.jeahwan.origin.R
import com.jeahwan.origin.utils.expandfun.FloatKt.dp2px
import com.noober.background.BackgroundLibrary
import java.util.*

abstract class BaseActivity<V : ViewDataBinding>(private val layoutId: Int) : AppCompatActivity() {
    protected lateinit var binding: V
    var pageName = this::class.java.canonicalName
    var pageUUID = UUID.randomUUID().toString()
    var startTime: Long = System.currentTimeMillis()
    var pageTitle = ""
    var contentId: String? = null
    var metaData: String? = null

    /**
     * 初始化内容 获取接口数据等
     */
    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        BackgroundLibrary.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        //沉浸式状态栏设置 默认超过状态栏 黑色字 子类可再次修改
        immersionBar {
            fitsSystemWindows(false)
            statusBarDarkFont(true, 0.2f)
            navigationBarDarkIcon(true)
            navigationBarColor(R.color.white)
        }
        initData()
    }

    /**
     * 字体大小不跟随系统变化 避免ui错乱
     *
     * @return
     */
    override fun getResources(): Resources {
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }
}