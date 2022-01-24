package com.jeahwan.origin.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.airbnb.lottie.LottieAnimationView
import com.jeahwan.origin.R
import com.jeahwan.origin.utils.RxTask.doInMainThread
import com.jeahwan.origin.utils.expandfun.FloatKt.dp2px
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem

class TabItemView constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabItem(
    context!!, attrs, defStyleAttr
) {
    private val mTitle: TextView

    //    private var mMessages ?: RoundMessageView = null
    private val mIcon: ImageView

    override fun getAccessibilityClassName(): CharSequence {
        return TabItemView::class.java.name
    }

    /**
     * 方便初始化的方法
     */
    fun initialize(
        title: String,
        @DrawableRes defaultDrawableRes: Int,
        @RawRes checkedJson: Int,
        checkedHideTitle: Boolean = false,
    ) {
        setTitle(title)
        mTitle.textSize = 10f
        mIcon.setImageDrawable(ContextCompat.getDrawable(context, defaultDrawableRes))
    }

    override fun setChecked(checked: Boolean) {
        doInMainThread {
            if (checked) {
                mTitle.setTextColor((0xffD93B1F).toInt())
            } else {
                mTitle.setTextColor(-0xcfcfd0)
            }
        }
    }

    override fun setMessageNumber(number: Int) {
//        mMessages?.messageNumber = number
    }

    override fun setHasMessage(hasMessage: Boolean) {
//        mMessages?.setHasMessage(hasMessage)
    }

    override fun setDefaultDrawable(drawable: Drawable) {
    }

    override fun setSelectedDrawable(drawable: Drawable) {
    }

    override fun getTitle(): String {
        return mTitle.text.toString()
    }

    override fun setTitle(title: String) {
        mTitle.text = title
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_tab_view, this, true)
        mIcon = findViewById(R.id.icon)
        mTitle = findViewById(R.id.title)
//        mMessages = findViewById(R.id.messages)
    }
}