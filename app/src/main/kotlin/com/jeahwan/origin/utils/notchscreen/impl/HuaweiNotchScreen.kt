package com.jeahwan.origin.utils.notchscreen.impl

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.WindowManager
import com.jeahwan.origin.utils.DensityUtil.calculateNotchRect
import com.jeahwan.origin.utils.notchscreen.INotchScreen
import com.jeahwan.origin.utils.notchscreen.INotchScreen.NotchSizeCallback
import java.util.*

@TargetApi(Build.VERSION_CODES.O)
class HuaweiNotchScreen : INotchScreen {
    override fun hasNotch(activity: Activity?): Boolean {
        var ret = false
        try {
            val cl = activity!!.classLoader
            val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
            ret = get.invoke(HwNotchSizeUtil) as Boolean
        } catch (ignore: Throwable) {
        }
        return ret
    }

    override fun setDisplayInNotch(activity: Activity?) {
        try {
            val window = activity!!.window
            val layoutParams = window.attributes
            val layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx")
            val con = layoutParamsExCls.getConstructor(
                WindowManager.LayoutParams::class.java
            )
            val layoutParamsExObj = con.newInstance(layoutParams)
            val method = layoutParamsExCls.getMethod("addHwFlags", Int::class.javaPrimitiveType)
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT)
            window.windowManager.updateViewLayout(window.decorView, window.decorView.layoutParams)
        } catch (ignore: Throwable) {
        }
    }

    override fun getNotchRect(activity: Activity?, callback: NotchSizeCallback?) {
        try {
            val cl = activity!!.classLoader
            val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = HwNotchSizeUtil.getMethod("getNotchSize")
            val ret = get.invoke(HwNotchSizeUtil) as IntArray
            val rects = ArrayList<Rect?>()
            rects.add(calculateNotchRect(activity, ret[0], ret[1]))
            callback!!.onResult(rects)
        } catch (ignore: Throwable) {
            callback!!.onResult(null)
        }
    }

    companion object {
        /**
         * 刘海屏全屏显示FLAG
         */
        const val FLAG_NOTCH_SUPPORT = 0x00010000

        /**
         * 设置华为刘海屏手机不使用刘海区
         */
        fun setNotDisplayInNotch(activity: Activity) {
            try {
                val window = activity.window
                val layoutParams = window.attributes
                val layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx")
                val con = layoutParamsExCls.getConstructor(
                    WindowManager.LayoutParams::class.java
                )
                val layoutParamsExObj = con.newInstance(layoutParams)
                val method =
                    layoutParamsExCls.getMethod("clearHwFlags", Int::class.javaPrimitiveType)
                method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT)
                window.windowManager.updateViewLayout(
                    window.decorView,
                    window.decorView.layoutParams
                )
            } catch (ignore: Throwable) {
            }
        }
    }
}