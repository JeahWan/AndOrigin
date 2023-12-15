package com.jeahwan.origin.utils.notchscreen.impl

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.view.Window
import com.jeahwan.origin.utils.DensityUtil.calculateNotchRect
import com.jeahwan.origin.utils.notchscreen.INotchScreen
import com.jeahwan.origin.utils.notchscreen.INotchScreen.NotchSizeCallback
import java.util.*

@TargetApi(Build.VERSION_CODES.O)
class MiNotchScreen : INotchScreen {
    override fun hasNotch(activity: Activity?): Boolean {
        return isNotch
    }

    override fun setDisplayInNotch(activity: Activity?) {
        val flag = 0x00000100 or 0x00000200 or 0x00000400
        try {
            val method = Window::class.java.getMethod(
                "addExtraFlags",
                Int::class.javaPrimitiveType
            )
            method.invoke(activity!!.window, flag)
        } catch (ignore: Exception) {
        }
    }

    override fun getNotchRect(activity: Activity?, callback: NotchSizeCallback?) {
        val rect = calculateNotchRect(
            activity!!, getNotchWidth(activity), getNotchHeight(activity)
        )
        val rects = ArrayList<Rect?>()
        rects.add(rect)
        callback!!.onResult(rects)
    }

    companion object {
        private val isNotch: Boolean
            private get() {
                try {
                    val getInt = Class.forName("android.os.SystemProperties")
                        .getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
                    val notch = getInt.invoke(null, "ro.miui.notch", 0) as Int
                    return notch == 1
                } catch (ignore: Throwable) {
                }
                return false
            }

        fun getNotchHeight(context: Context?): Int {
            val resourceId = Resources.getSystem().getIdentifier("notch_height", "dimen", "android")
            return if (resourceId > 0) {
                Resources.getSystem().getDimensionPixelSize(resourceId)
            } else 0
        }

        fun getNotchWidth(context: Context?): Int {
            val resourceId = Resources.getSystem().getIdentifier("notch_width", "dimen", "android")
            return if (resourceId > 0) {
                Resources.getSystem().getDimensionPixelSize(resourceId)
            } else 0
        }
    }
}