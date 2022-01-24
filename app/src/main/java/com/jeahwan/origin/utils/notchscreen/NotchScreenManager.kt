package com.jeahwan.origin.utils.notchscreen

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import com.jeahwan.origin.utils.RomUtils
import com.jeahwan.origin.utils.notchscreen.INotchScreen.*
import com.jeahwan.origin.utils.notchscreen.impl.*

/**
 * https://github.com/smarxpan/NotchScreenTool
 */
class NotchScreenManager private constructor() {
    private val notchScreen: INotchScreen?
    fun setDisplayInNotch(activity: Activity?) {
        notchScreen?.setDisplayInNotch(activity)
    }

    fun getNotchInfo(activity: Activity?, notchScreenCallback: NotchScreenCallback) {
        val notchScreenInfo = NotchScreenInfo()
        if (notchScreen != null && notchScreen.hasNotch(activity)) {
            notchScreen.getNotchRect(activity, object : NotchSizeCallback {
                override fun onResult(notchRects: List<Rect?>?) {
                    if (notchRects != null && notchRects.isNotEmpty()) {
                        notchScreenInfo.hasNotch = true
                        notchScreenInfo.notchRects = notchRects as List<Rect>?
                    }
                    notchScreenCallback.onResult(notchScreenInfo)
                }
            })
        } else {
            notchScreenCallback.onResult(notchScreenInfo)
        }
    }

    private fun getNotchScreen(): INotchScreen? {
        var notchScreen: INotchScreen? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            notchScreen = AndroidPNotchScreen()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when {
                RomUtils.isHuawei -> {
                    notchScreen = HuaweiNotchScreen()
                }
                RomUtils.isOppo -> {
                    notchScreen = OppoNotchScreen()
                }
                RomUtils.isVivo -> {
                    notchScreen = VivoNotchScreen()
                }
                RomUtils.isXiaomi -> {
                    notchScreen = MiNotchScreen()
                }
            }
        }
        return notchScreen
    }

    companion object {
        val instance = NotchScreenManager()
    }

    init {
        notchScreen = getNotchScreen()
    }
}