package com.jeahwan.origin.ui

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.gyf.immersionbar.ktx.immersionBar
import com.jeahwan.origin.BuildConfig
import com.jeahwan.origin.R
import com.jeahwan.origin.base.BaseFragment
import com.noober.background.BackgroundLibrary
import java.lang.ref.WeakReference

class ContainerActivity : FragmentActivity() {

    companion object {
        const val FRAGMENT = "fragment"
        const val BUNDLE = "bundle"
        private const val FRAGMENT_TAG = "content_fragment_tag"
    }

    var mFragment: WeakReference<Fragment>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        BackgroundLibrary.inject(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        val fm = supportFragmentManager
        var fragment: Fragment? = null
        if (savedInstanceState != null) {
            fragment = fm.getFragment(savedInstanceState, FRAGMENT_TAG)
        }
        if (fragment == null) {
            fragment = initFromIntent(intent)
        }
        val trans = supportFragmentManager
            .beginTransaction()
        trans.replace(R.id.content, fragment)
        trans.commitAllowingStateLoss()
        mFragment = WeakReference(fragment)
        //沉浸式状态栏设置
        immersionBar {
            fitsSystemWindows(false)
            statusBarDarkFont(true, 0.2f)
            navigationBarDarkIcon(true)
            navigationBarColor(R.color.white)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, FRAGMENT_TAG, mFragment?.get()!!)
    }

    private fun initFromIntent(data: Intent?): Fragment {
        if (data == null) {
            throw RuntimeException(
                "you must provide a page info to display"
            )
        }
        try {
            val fragmentName = data.getStringExtra(FRAGMENT)
            if (fragmentName == null || "" == fragmentName) {
                finish()
                require(!BuildConfig.DEBUG) { "can not find page fragmentName" }
            }
            val fragmentClass = Class.forName(fragmentName!!)
            val fragment = fragmentClass.newInstance() as Fragment
            val args = data.getBundleExtra(BUNDLE)
            if (args != null) {
                fragment.arguments = args
            }
            return fragment
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        throw RuntimeException("fragment initialization failed!")
    }

    /**
     * 设置背景颜色(主要用于处理某些白色背景状态栏非白色)
     */
    fun setBackgroundColor(@ColorInt color: Int) {
        findViewById<View>(R.id.content).setBackgroundColor(color)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    /**
     * 处理back
     *
     * @return 是否已经处理
     */
    private fun onFragmentBackPressed(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content)
        return (currentFragment is BaseFragment<*>
                && currentFragment.onFragmentBackPressed())
    }

    override fun onBackPressed() {
        if (onFragmentBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
    }

    val pageName: String
        get() {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.content)
            if (currentFragment is BaseFragment<*>) {
                return currentFragment.pageName ?: ""
            }
            return ""
        }
}