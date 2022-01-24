package com.jeahwan.origin.utils

import android.app.Activity
import java.lang.ref.WeakReference

class MyActivityManager private constructor() {
    private var sCurrentActivityWeakRef: WeakReference<Activity>? = null
    val currentActivity: Activity?
        get() {
            var currentActivity: Activity? = null
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef!!.get()
            }
            return currentActivity
        }

    fun setCurrentActivityByWeak(activity: Activity) {
        sCurrentActivityWeakRef = WeakReference(activity)
    }

    companion object {
        val instance = MyActivityManager()
    }
}