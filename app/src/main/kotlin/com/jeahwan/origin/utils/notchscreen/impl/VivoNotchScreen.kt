package com.jeahwan.origin.utils.notchscreen.impl

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import com.jeahwan.origin.utils.DensityUtil.calculateNotchRect
import com.jeahwan.origin.utils.notchscreen.INotchScreen
import com.jeahwan.origin.utils.notchscreen.INotchScreen.NotchSizeCallback
import java.util.*

/**
 * 测试之后发现vivo并不需要适配，因为vivo没有将显示区域绘制到耳朵区的API
 */
@TargetApi(Build.VERSION_CODES.O)
class VivoNotchScreen : INotchScreen {
    override fun hasNotch(activity: Activity?): Boolean {
        return isNotch
    }

    @Deprecated("")
    override fun setDisplayInNotch(activity: Activity?) {
    }

    override fun getNotchRect(activity: Activity?, callback: NotchSizeCallback?) {
        val rects = ArrayList<Rect?>()
        val rect = calculateNotchRect(
            activity!!, getNotchWidth(activity), getNotchHeight(activity)
        )
        rects.add(rect)
        callback!!.onResult(rects)
    }

    companion object {
        val isNotch: Boolean
            get() {
                var value = false
                val mask = 0x00000020
                try {
                    val cls = Class.forName("android.util.FtFeature")
                    val hideMethod =
                        cls.getMethod("isFtFeatureSupport", Int::class.javaPrimitiveType)
                    val `object` = cls.newInstance()
                    value = hideMethod.invoke(`object`, mask) as Boolean
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return value
            }

        /**
         * vivo的适配文档中就告诉是27dp，未告知如何动态获取
         */
        fun getNotchHeight(context: Context?): Int {
            val density = getDensity(context)
            return (27 * density).toInt()
        }

        /**
         * vivo的适配文档中就告诉是100dp，未告知如何动态获取
         */
        fun getNotchWidth(context: Context?): Int {
            val density = getDensity(context)
            return (100 * density).toInt()
        }

        private fun getDensity(context: Context?): Float {
            return Resources.getSystem().displayMetrics.density
        }
    }
}