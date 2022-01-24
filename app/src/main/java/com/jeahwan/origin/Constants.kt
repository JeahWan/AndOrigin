package com.jeahwan.origin

import com.jeahwan.origin.ui.MainActivity
import com.jeahwan.origin.utils.SPUtils

object Constants {
    object Id {
        //微信
        const val WECHAT_ID = ""
        const val WECHAT_SECRET = ""

        //友盟
        const val UMENG_ID = ""
        const val UMENG_PUSH_ID = ""
    }

    object Server {
        //埋点参数中用到了环境标识 这里的顺序不能调整 1 生产 2 测试 3 预发
        val hosts = arrayOf(
            //生产
            arrayOf(
                //app secret
                "",
                //接口
                "",
                //h5
                "",
                //直播
                "",
                //埋点
                "",
            ),
            //测试
            arrayOf(
                "",
                "",
                "",
                "",
                "",
            ),
            //预发
        )

        //生产包固定取值，测试包取sp值
        //必须使用get 否则切换环境会有问题
        val buildTypeIndex: Int
            get() = if (!BuildConfig.DEBUG) 0 else SPUtils.get()
                .getInt(SPKey.SERVER_ARRAY_INDEX, 1)

        val appSecret: String
            get() = hosts[buildTypeIndex][0]

        val apiHost: String
            get() = hosts[buildTypeIndex][1]
        val h5Host: String
            get() = hosts[buildTypeIndex][2]
        val liveHost: String
            get() = hosts[buildTypeIndex][3]

    }

    object SPKey {
        //数据缓存
        const val OAID = "OAID"
        //测试包的特殊功能
        const val UNLOCK_COURSE = "UNLOCK_COURSE"
        const val CUSTOM_CHANNEL = "CUSTOM_CHANNEL"
        const val CUSTOM_TOKEN = "CUSTOM_TOKEN"
        const val SERVER_ARRAY_INDEX = "SERVER_ARRAY_INDEX"
        const val PUSH_SWITCH_TIPS = "PUSH_SWITCH_TIPS"

        //音频播放相关
        const val PLAY_SPEED = "PLAY_SPEED"
        const val REPORT_INTERVAL = "REPORT_INTERVAL"

        const val USER_INFO = "USER_INFO"

        //默认协议是否已同意
        const val DEFAULT_PRIVACY_AGREED = "DEFAULT_PRIVACY_AGREED"
    }

    object BundleKey {
        //通用
        const val EXTRA_DATA = "EXTRA_DATA"
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_TYPE = "EXTRA_TYPE"

        //推送消息
        const val PUSH_MSG_TYPE = "PUSH_MSG_TYPE"
        const val PUSH_MSG_DATA = "PUSH_MSG_DATA"
    }

    /**
     * 用于一些业务逻辑的临时存储、控制
     */
    object Temp {
        var isSplashClick = false//记录闪屏点击 MainAct处理逻辑
        var pushData = ""  //push数据 -不为空表示push启动；为空表示非push启动
        var tab1: MainActivity.TabEnum? = null //首页对应的一级tab，首页、发现、学习中心、圈子、我的
    }
}