package com.jeahwan.origin.utils.expandfun

import android.os.SystemClock
import android.view.View

object ViewKt {

    val MIN_DELAY_TIME: Long = 500
    var hash = 0
    var lastClickTime: Long = 0

    /**
     * 判断快速点击
     */
    fun View.isFastClick(): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime
        lastClickTime = time
        return if (hash != 0) {
            if (hash == this.hashCode()) {
                timeD in 1 until MIN_DELAY_TIME
            } else {
                hash = this.hashCode()
                false
            }
        } else {
            hash = this.hashCode()
            false
        }
    }

    /**
     * 防止快速点击
     */
    fun View.avoidFastClick(clickAction: () -> Unit) {
        this.setOnClickListener {
            if (this.hashCode() != hash) {
                hash = this.hashCode()
                lastClickTime = System.currentTimeMillis()
                clickAction()
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > MIN_DELAY_TIME) {
                    lastClickTime = System.currentTimeMillis()
                    clickAction()
                }
            }
        }
    }

    /**
     * 5连击执行
     */
    @Suppress("ObjectLiteralToLambda")
    fun View.fiveMultiClick(clickAction: () -> Unit) {
        val mHits = LongArray(5)

        //不要使用lambda简化 需要IgnoreFastClick注解
        this.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // 数组依次先前移动一位
                System.arraycopy(
                    mHits,
                    1,
                    mHits,
                    0,
                    mHits.size - 1
                )
                mHits[mHits.size - 1] = SystemClock.uptimeMillis() // 开机后运行时间
                if (mHits[0] >= mHits[mHits.size - 1] - 500) {
                    clickAction()
                }
            }
        })
    }
}