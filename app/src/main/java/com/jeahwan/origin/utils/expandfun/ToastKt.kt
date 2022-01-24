package com.jeahwan.origin.utils.expandfun

import android.view.Gravity
import android.widget.Toast
import com.jeahwan.origin.App
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Jeah on 2016/8/5.
 */
object ToastKt {

    private var toastWeakRef: WeakReference<Toast>? = null
    private var isError = false

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    private fun showShort(message: CharSequence?): Toast? {
        return show(message, Toast.LENGTH_SHORT)
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    private fun showLong(message: CharSequence?): Toast? {
        return show(message, Toast.LENGTH_LONG)
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    private fun show(message: CharSequence?, duration: Int): Toast? {
        if (toastWeakRef?.get() == null) {
            val toast = Toast.makeText(App.instance, message ?: "", duration)
            toastWeakRef = WeakReference(toast)
        } else {
            toastWeakRef?.get()?.setText(message)
        }
        toastWeakRef?.get()?.show()
        return toastWeakRef?.get()
    }

    /**
     * Hide the toast, if any.
     */
    private fun hideToast() {
        toastWeakRef?.get()?.cancel()
    }

    /**
     * 规定时间内只弹一次的toast
     *
     * @param context
     * @param message
     * @param center 是否居中显示
     */
    private fun showSingle(message: CharSequence?, center: Boolean) {
        //判断是否已弹出
        if (isError) return
        isError = true
        val toast = Toast.makeText(App.instance, message ?: "", Toast.LENGTH_SHORT)
        if (center) toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.show()

        //3秒后继续弹出
        Timer().schedule(object : TimerTask() {
            override fun run() {
                isError = false
            }
        }, 3000)
    }

    fun Any.toast(msg: String?, duration: Int) {
        show(msg, duration)
    }

    fun Any.toastShort(msg: String?) {
        showShort(msg)
    }

    fun Any.toastLong(msg: String?) {
        showLong(msg)
    }

    fun Any.toastSingle(msg: String?, center: Boolean) {
        showSingle(msg, center)
    }
}