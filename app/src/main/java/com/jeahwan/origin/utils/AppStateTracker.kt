package com.jeahwan.origin.utils

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.jeahwan.origin.App

/**
 * app前后台状态判断
 * https://juejin.im/post/5b87f409e51d4538b0640f58
 */
object AppStateTracker {
    const val STATE_FOREGROUND = 0
    const val STATE_BACKGROUND = 1
    var currentState = 0

    fun track(
        application: Application,
        appTurnIntoForeground: (Activity) -> Unit,
        appTurnIntoBackGround: () -> Unit
    ) {
        application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {

            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                super.onActivityCreated(activity, bundle)
                App.instance.addActivity(activity)
                MyActivityManager.instance.setCurrentActivityByWeak(activity)
            }

            private var resumeActivityCount = 0
            override fun onActivityStarted(activity: Activity) {
                if (resumeActivityCount == 0) {
                    currentState = STATE_FOREGROUND
                    appTurnIntoForeground(activity)
                }
                resumeActivityCount++
            }

            override fun onActivityResumed(activity: Activity) {
                MyActivityManager.instance.setCurrentActivityByWeak(activity)
            }

            override fun onActivityStopped(activity: Activity) {
                resumeActivityCount--
                if (resumeActivityCount == 0) {
                    currentState = STATE_BACKGROUND
                    appTurnIntoBackGround()
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                super.onActivityDestroyed(activity)
                App.instance.removeActivity(activity)
            }
        })
    }

    private open class SimpleActivityLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }
}