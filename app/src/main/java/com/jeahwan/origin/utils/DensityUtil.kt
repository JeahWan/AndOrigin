package com.jeahwan.origin.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.WindowManager

/**
 * Created by Monkey on 2018/6/20.
 */
object DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    @JvmStatic
    fun px2dp(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     *
     *
     * 将px转换为sp
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (pxValue * 2 / fontScale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 sp(像素) 的单位 转成为 px
     *
     *
     * 将sp转换为px
     */
    @JvmStatic
    fun sp2px(spValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    @JvmStatic
    fun getScreenWidth(): Int {
        val dm = Resources.getSystem().displayMetrics
        return dm.widthPixels
    }

    fun getScreenHeight(): Int {
        val dm = Resources.getSystem().displayMetrics
        return dm.heightPixels
    }

    /**
     * 获取状态栏高度
     */
    @JvmStatic
    fun getStatusBarHeight(): Int {
        var result = 24
        val resId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        result = if (resId > 0) {
            Resources.getSystem().getDimensionPixelSize(resId)
        } else {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                result.toFloat(), Resources.getSystem().displayMetrics
            ).toInt()
        }
        return result
    }

    @JvmStatic
    fun getNavigationBarHeight(context: Context?): Int {
        if (context != null && checkNavigationBarShow(context as Activity)) {
            val resources = context.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    /**
     * 判断虚拟导航栏是否显示
     *
     * @param context 上下文对象
     * //     * @param window  当前窗口
     * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
     */
    fun checkNavigationBarShow(context: Activity?): Boolean {
        if (context == null)
            return false
        val show: Boolean
        val display = context.window.windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        val decorView = context.window.decorView
        val conf = Resources.getSystem().configuration
        show = if (Configuration.ORIENTATION_LANDSCAPE == conf.orientation) {
            val contentView = decorView.findViewById<View>(R.id.content)
            point.x != contentView.width
        } else {
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            rect.bottom != point.y
        }
        return show
    }

    @JvmStatic
    fun isPortrait(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * 获取屏幕宽高
     */
    fun getScreenSize(context: Context): IntArray {
        val size = IntArray(2)
        val w = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = w.defaultDisplay
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        var widthPixels = metrics.widthPixels
        var heightPixels = metrics.heightPixels
        try {
            val realSize = Point()
            Display::class.java.getMethod("getRealSize", Point::class.java).invoke(d, realSize)
            widthPixels = realSize.x
            heightPixels = realSize.y
        } catch (ignored: Throwable) {
        }
        size[0] = widthPixels
        size[1] = heightPixels
        return size
    }

    /**
     * 通过宽高计算notch的Rect
     *
     * @param notchWidth  刘海的宽
     * @param notchHeight 刘海的高
     */
    @JvmStatic
    fun calculateNotchRect(context: Context, notchWidth: Int, notchHeight: Int): Rect {
        val screenSize = getScreenSize(context)
        val screenWidth = screenSize[0]
        val screenHeight = screenSize[1]
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        if (isPortrait(context)) {
            left = (screenWidth - notchWidth) / 2
            top = 0
            right = left + notchWidth
            bottom = notchHeight
        } else {
            left = 0
            top = (screenHeight - notchWidth) / 2
            right = notchHeight
            bottom = top + notchWidth
        }
        return Rect(left, top, right, bottom)
    }
}