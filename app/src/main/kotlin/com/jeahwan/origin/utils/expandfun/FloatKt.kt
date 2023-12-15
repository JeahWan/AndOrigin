package com.jeahwan.origin.utils.expandfun

import android.content.res.Resources

/*
 * float类扩展
 */
object FloatKt {

    /**
     * dp转px
     */
    fun Float.dp2px(): Int = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()


    /**
     * px转dp
     */
    fun Float.px2dp(): Int = (this / Resources.getSystem().displayMetrics.density + 0.5f).toInt()

}