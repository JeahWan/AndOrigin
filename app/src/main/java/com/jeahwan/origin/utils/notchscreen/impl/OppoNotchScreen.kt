package com.jeahwan.origin.utils.notchscreen.impl

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import com.jeahwan.origin.utils.DensityUtil.isPortrait
import com.jeahwan.origin.utils.notchscreen.INotchScreen
import com.jeahwan.origin.utils.notchscreen.INotchScreen.NotchSizeCallback
import java.util.*

@TargetApi(Build.VERSION_CODES.O)
class OppoNotchScreen : INotchScreen {
    override fun hasNotch(activity: Activity?): Boolean {
        var ret = false
        try {
            ret =
                activity!!.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
        } catch (ignore: Throwable) {
        }
        return ret
    }

    @Deprecated("")
    override fun setDisplayInNotch(activity: Activity?) {
    }

    override fun getNotchRect(activity: Activity?, callback: NotchSizeCallback?) {
        try {
            val screenValue = screenValue
            if (!TextUtils.isEmpty(screenValue)) {
                val split = screenValue.split(":".toRegex()).toTypedArray()
                val leftTopPoint = split[0]
                val leftAndTop = leftTopPoint.split(",".toRegex()).toTypedArray()
                val rightBottomPoint = split[1]
                val rightAndBottom = rightBottomPoint.split(",".toRegex()).toTypedArray()
                val left: Int
                val top: Int
                val right: Int
                val bottom: Int
                if (isPortrait(activity!!)) {
                    left = Integer.valueOf(leftAndTop[0])
                    top = Integer.valueOf(leftAndTop[1])
                    right = Integer.valueOf(rightAndBottom[0])
                    bottom = Integer.valueOf(rightAndBottom[1])
                } else {
                    left = Integer.valueOf(leftAndTop[1])
                    top = Integer.valueOf(leftAndTop[0])
                    right = Integer.valueOf(rightAndBottom[1])
                    bottom = Integer.valueOf(rightAndBottom[0])
                }
                val rect = Rect(left, top, right, bottom)
                val rects = ArrayList<Rect?>()
                rects.add(rect)
                callback!!.onResult(rects)
            }
        } catch (ignore: Throwable) {
            callback!!.onResult(null)
        }
    }

    companion object {
        /**
         * 获取刘海的坐标
         *
         *
         * 属性形如：[ro.oppo.screen.heteromorphism]: [378,0:702,80]
         *
         *
         * 获取到的值为378,0:702,80
         *
         *
         *
         *
         * (378,0)是刘海区域左上角的坐标
         *
         *
         * (702,80)是刘海区域右下角的坐标
         */
        private val screenValue: String
            private get() {
                var value = ""
                val cls: Class<*>
                try {
                    cls = Class.forName("android.os.SystemProperties")
                    val get = cls.getMethod("get", String::class.java)
                    val `object` = cls.newInstance()
                    value = get.invoke(`object`, "ro.oppo.screen.heteromorphism") as String
                } catch (ignore: Throwable) {
                }
                return value
            }
    }
}