package com.jeahwan.origin.base

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.jeahwan.origin.R
import com.jeahwan.origin.utils.DensityUtil.getScreenWidth
import com.jeahwan.origin.utils.RxTask
import java.util.UUID
import java.util.concurrent.TimeUnit

class BaseDialog<T : ViewDataBinding>(
    private val context: Activity?,
    private val resId: Int,
    private val onCreateView: OnCreateView<T>,
) {
    private var mDialog: Dialog? = null
    private val binding: T? =
        context?.run { DataBindingUtil.inflate(layoutInflater, resId, null, false) }
    private var mWidth = (getScreenWidth() * 0.7).toInt()
    private var mHeight = 0
    private var mGravity = Gravity.CENTER
    private var mWindowAnimationsRes = R.style.dialogBottomEnter // 默认动画

    // 点击dialog外部是否消失
    private var mCancelable = true

    // dialog消失监听
    private var mDismissListener: OnDialogDismissListener? = null

    private var startTime: Long = System.currentTimeMillis()
    private var ignoreTrackEvent: Boolean = false
    private var contentId: String? = null
    private var contentType: String? = null // 1-文章；2-系列课；3-打包课(无)；4-话题；5-作者
    private var pageTile: String = ""

    fun build() {
        if (context == null || binding == null) return
        mDialog =
            Dialog(context, androidx.constraintlayout.widget.R.style.Theme_AppCompat_Dialog).apply {
                val dialogUUID = UUID.randomUUID().toString()
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(mCancelable)
                setCancelable(mCancelable)
                window?.run {
                    setContentView(binding.root)
                    decorView.setPadding(0, 0, 0, 0)
                    setBackgroundDrawable(null)
                    //位置
                    setGravity(mGravity)
                //动画
                setWindowAnimations(mWindowAnimationsRes)
                //设置宽度
                attributes = attributes?.apply {
                    width = mWidth
                    if (mHeight != 0) {
                        height = mHeight
                    }
                }
            }
        }
        //外部操作ui 此刻两个参数都有值了
        onCreateView.createView(binding, mDialog!!)
    }

    fun setWidth(width: Int): BaseDialog<*> {
        mWidth = width
        return this
    }

    fun setHeight(height: Int): BaseDialog<*> {
        mHeight = height
        return this
    }

    fun setGravity(gravity: Int): BaseDialog<*> {
        mGravity = gravity
        return this
    }

    fun setCancelable(cancelable: Boolean): BaseDialog<*> {
        mCancelable = cancelable
        return this
    }

    fun setOnDismissListener(listener: OnDialogDismissListener?): BaseDialog<*> {
        mDismissListener = listener
        return this
    }

    fun dismiss() {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog?.dismiss()
        }
    }

    fun setIgnoreTrackEvent(ignore: Boolean): BaseDialog<*> {
        ignoreTrackEvent = ignore
        return this
    }

    fun setContentId(contentId: String?): BaseDialog<*> {
        this.contentId = contentId
        return this
    }

    fun setContentType(contentType: String?): BaseDialog<*> {
        this.contentType = contentType
        return this
    }

    fun setPageTitle(pageTitle: String): BaseDialog<*> {
        this.pageTile = pageTitle
        return this
    }

    fun show(milliSeconds: Long = 0) {
        RxTask.doInMainThreadDelay({
            if (context != null && !context.isDestroyed) {
                startTime = System.currentTimeMillis()
                build()
                mDialog?.show()
            }
        }, milliSeconds, TimeUnit.MILLISECONDS)
    }

    fun setWindowAnimations(@StyleRes resId: Int): BaseDialog<*> {
        mWindowAnimationsRes = resId
        return this
    }

    interface OnCreateView<T> {
        fun createView(dialogBinding: T, dialog: Dialog)
    }

    interface OnDialogDismissListener {
        fun onDismiss()
    }

    init {
        //设置默认的关闭按钮、取消按钮事件
        binding?.root?.rootView?.findViewById<View?>(R.id.iv_close)?.run {
            setOnClickListener { mDialog?.dismiss() }
        }
    }
}