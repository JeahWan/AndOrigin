package com.jeahwan.origin.utils.notchscreen

import android.app.Activity
import android.graphics.Rect

interface INotchScreen {
    fun hasNotch(activity: Activity?): Boolean
    fun setDisplayInNotch(activity: Activity?)
    fun getNotchRect(activity: Activity?, callback: NotchSizeCallback?)
    interface NotchSizeCallback {
        fun onResult(notchRects: List<Rect?>?)
    }

    interface HasNotchCallback {
        fun onResult(hasNotch: Boolean)
    }

    interface NotchScreenCallback {
        fun onResult(notchScreenInfo: NotchScreenInfo?)
    }

    class NotchScreenInfo {
        var hasNotch = false
        var notchRects: List<Rect?>? = null
    }
}