package com.jeahwan.origin

import android.app.Activity
import android.app.Application
import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.google.gson.Gson
import com.jeahwan.origin.base.BaseFragment
import com.jeahwan.origin.data.User.CustomerData
import com.jeahwan.origin.ui.ContainerActivity
import com.jeahwan.origin.ui.CrashActivity
import com.jeahwan.origin.ui.SplashActivity
import com.jeahwan.origin.utils.AppStateTracker
import com.jeahwan.origin.utils.MyActivityManager
import com.jeahwan.origin.utils.RxTask
import com.jeahwan.origin.utils.SPUtils
import com.jeahwan.origin.utils.StringUtils
import java.lang.ref.SoftReference
import java.util.LinkedList
import java.util.concurrent.TimeUnit

class App : Application() {
    var pushDeviceToken: String? = null
    private var activityList: MutableList<Activity> = LinkedList() //activity实例集合
    private lateinit var user: CustomerData
    private var currentFragment: SoftReference<BaseFragment<*>>? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        //初始化user信息
        loadUserForSP()
        //CrashHandler
        initCrash()
        //app前后台判断，用于调用用户信息接口、上报使用时长等
        initAppStatus()
        //WebView多进程问题
        configWebViewCacheDirWithAndroidP()
    }

    private fun initAppStatus() {
        AppStateTracker.track(this,
            {
            },
            {
            })
    }

    /**
     * 通过name获取activity或fragment实例，避免在类中使用静态instance字段的方式
     */
    fun <T> getInstanceByClassName(name: Class<T>): T? = activityList.lastOrNull {
        with(it::class.java.canonicalName) {
            this == name.canonicalName || (this == ContainerActivity::class.java.canonicalName && (it as ContainerActivity).mFragment?.get() != null && it.mFragment?.get()!!::class.java.canonicalName == name.canonicalName)
        }
    }?.let { (if (it is ContainerActivity) it.mFragment?.get() else it) as T }


    private fun initCrash() {
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
            .trackActivities(true) //是否跟踪Activity
            .restartActivity(SplashActivity::class.java) //重新启动后的activity
            .errorActivity(CrashActivity::class.java) //崩溃后的错误activity
            .eventListener(object : CustomActivityOnCrash.EventListener {
                override fun onLaunchErrorActivity() {
                }

                override fun onRestartAppFromErrorActivity() {

                }

                override fun onCloseAppFromErrorActivity() {

                }
            })
            .apply()
    }

    /**
     * Android P 以及之后版本不支持同时从多个进程使用具有相同数据目录的WebView
     * 为其它进程webView设置目录
     */
    private fun configWebViewCacheDirWithAndroidP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (packageName != processName) { //判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    // 添加Activity到容器中
    fun addActivity(activity: Activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity)
        }
    }

    // 移除Activity到容器中
    fun removeActivity(activity: Activity) {
        activityList.remove(activity)
    }

    fun getUser(): CustomerData {
        if (BuildConfig.DEBUG) {
            //测试功能
            val token = SPUtils.get().getString(Constants.SPKey.CUSTOM_TOKEN)
            if (!TextUtils.isEmpty(token)) {
                user.token = token
            }
            if (SPUtils.get().getBoolean(Constants.SPKey.UNLOCK_COURSE)) {
                user.vipStatus = 1
                user.vipInvalidTime = "无期限"
            }
        }
        return user
    }

    private fun loadUserForSP() {
        //app启动 从sp加载user信息
        var userStr = SPUtils.get().getString(Constants.SPKey.USER_INFO)
        if (!TextUtils.isEmpty(userStr)) {
            //解密
            userStr = StringUtils.decryptPassword(userStr, Constants.Id.WECHAT_SECRET)
            user = try {
                //加解密的key不一致会导致解密失败 parse转换出错
                Gson().fromJson(userStr, CustomerData::class.java)
            } catch (e: Exception) {
                CustomerData()
            }
        } else {
            user = CustomerData()
        }
    }

    /**
     * 参数为空即刷新本地sp 用于更新部分字段（如vipStatus）等情况
     */
    fun saveUser(customerData: CustomerData? = null) {
        //缓存赋值
        customerData?.let {
            user = customerData
        }
        //加密存储 供重启后取出
        val loginModuleStr =
            StringUtils.encryptPassword(
                Gson().toJson(user),
                Constants.Id.WECHAT_SECRET
            )
        SPUtils.get().putString(Constants.SPKey.USER_INFO, loginModuleStr)
    }

    /**
     * 判断是否登录
     */
    val isLogin: Boolean get() = !TextUtils.isEmpty(user.token)

    /**
     * 取得当前运行的activity 提供给dialog使用
     * @return
     */
    fun getCurrentActivity(): Activity? = MyActivityManager.instance.currentActivity

    /**
     * 退出app
     * 默认上传数据 app更新的回调中不需要
     */
    fun exit(milliseconds: Long = 0) {
        RxTask.doInMainThreadDelay({
            //activity组件finish
            if (activityList.isNotEmpty()) {
                for (activity in activityList) {
                    activity.finish()
                }
            }
        }, milliseconds, TimeUnit.MILLISECONDS)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}