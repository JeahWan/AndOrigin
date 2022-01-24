package com.jeahwan.origin.utils.notchscreen.impl

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.jeahwan.origin.utils.notchscreen.INotchScreen
import com.jeahwan.origin.utils.notchscreen.INotchScreen.NotchSizeCallback

@TargetApi(Build.VERSION_CODES.P)
class AndroidPNotchScreen : INotchScreen {
    /**
     * Android P 没有单独的判断方法，根据getNotchRect方法的返回结果处理即可
     */
    override fun hasNotch(activity: Activity?): Boolean {
        return true
    }

    override fun setDisplayInNotch(activity: Activity?) {
        val window = activity!!.window
        // 延伸显示区域到耳朵区
        val lp = window.attributes
        lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = lp
        // 允许内容绘制到耳朵区
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    override fun getNotchRect(activity: Activity?, callback: NotchSizeCallback?) {
        val contentView = activity!!.window.decorView
        contentView.post {
            val windowInsets = contentView.rootWindowInsets
            if (windowInsets != null) {
                val cutout = windowInsets.displayCutout
                if (cutout != null) {
                    val rects = cutout.boundingRects
                    callback!!.onResult(rects)
                    return@post
                }
            }
            callback!!.onResult(null)
        }
    }
}