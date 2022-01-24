package com.jeahwan.origin.utils.expandfun

import android.text.Html
import android.text.Spannable
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

/*
 * textview类扩展
 */
object TextViewKt {

    /**
     * 设置字体
     */
    fun TextView.setTypeFaceExcludeFontPadding(@FontRes fontRes: Int) {
        typeface = ResourcesCompat.getFont(context, fontRes)
        includeFontPadding = false
    }

    /**
     * 富文本
     */
    fun TextView.setHtmlText(htmlStr: String) {
        text = Html.fromHtml(htmlStr)
    }

    /**
     * 清除html标签
     */
    fun TextView.setClearHtmlText(htmlStr: String) {
        text = (Html.fromHtml(htmlStr) as Spannable).toString()
    }
}