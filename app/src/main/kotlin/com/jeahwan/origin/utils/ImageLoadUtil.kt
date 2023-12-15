package com.jeahwan.origin.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class ImageLoadUtil private constructor() {
    /**
     * 加载普通图片
     */
    fun loadImage(
        context: Context?,
        imageUrl: String?,
        imageView: ImageView,
        defaultResId: Int = 0
    ) {
        if (context != null && !isDestroy(context as Activity?)) Glide.with(context)
            .load(if (!TextUtils.isEmpty(imageUrl)) imageUrl else "")
            .apply(
                RequestOptions()
                    .error(defaultResId)
                    .placeholder(defaultResId)
            )
            .into(imageView)
    }

    /**
     * 加载普通图片
     */
    fun loadImage(context: Context?, imageUrl: Int, imageView: ImageView, defaultResId: Int = 0) {
        if (context != null && !isDestroy(context as Activity?)) Glide.with(context)
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .error(defaultResId)
                    .placeholder(defaultResId)
            )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)
    }

    /**
     * 加载普通图片
     */
    fun loadImage(context: Context?, imageUrl: Uri?, imageView: ImageView, defaultResId: Int = 0) {
        if (context != null && !isDestroy(context as Activity?)) Glide.with(context)
            .load(imageUrl ?: "")
            .apply(
                RequestOptions()
                    .error(defaultResId)
                    .placeholder(defaultResId)
            )
            .into(imageView)
    }

    /**
     * 圆图
     */
    fun loadCircleImage(
        context: Context?,
        imageUrl: String?,
        imageView: ImageView,
        defaultResId: Int = 0
    ) {
        if (context != null && !isDestroy(context as Activity?)) Glide.with(context)
            .load(if (!TextUtils.isEmpty(imageUrl)) imageUrl else "")
            .apply(
                RequestOptions.circleCropTransform()
                    .error(defaultResId)
                    .placeholder(defaultResId)
            )
            .into(imageView)
    }

    /**
     * 判断Activity是否Destroy
     */
    fun isDestroy(activity: Activity?): Boolean {
        return activity == null || activity.isFinishing || activity.isDestroyed
    }

    companion object {
        val instance = ImageLoadUtil()
    }
}