package com.jeahwan.origin.utils

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import androidx.interpolator.view.animation.FastOutLinearInInterpolator

object AnimUtil {

    fun shake(view: View?, shakeFactor: Float, duration: Long): ObjectAnimator {
        val rotateValuesHolder = PropertyValuesHolder.ofKeyframe(
            View.ROTATION,
            Keyframe.ofFloat(0f, 0f),
            Keyframe.ofFloat(0.1f, 0f),
            Keyframe.ofFloat(0.2f, shakeFactor),
            Keyframe.ofFloat(0.3f, -shakeFactor),
            Keyframe.ofFloat(0.4f, shakeFactor),
            Keyframe.ofFloat(0.5f, -shakeFactor),
            Keyframe.ofFloat(0.6f, shakeFactor),
            Keyframe.ofFloat(0.7f, -shakeFactor),
            Keyframe.ofFloat(0.8f, shakeFactor),
            Keyframe.ofFloat(0.9f, 0f),
            Keyframe.ofFloat(1.0f, 0f)
        )
        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, rotateValuesHolder)
        objectAnimator.duration = duration
        objectAnimator.repeatCount = ValueAnimator.INFINITE
        objectAnimator.repeatMode = ValueAnimator.REVERSE
        return objectAnimator
    }

    fun scaleAnim(view: View) {
        val anim = ScaleAnimation(
            1f, 0.7f, 1f, 0.7f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.duration = 200
        anim.interpolator = AnticipateOvershootInterpolator()
        view.startAnimation(anim)
    }

    /**
     * 展开
     */
    fun expand(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val viewHeight = view.measuredHeight
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                view.layoutParams.height = (viewHeight * interpolatedTime).toInt()
                view.requestLayout()
            }
        }
        animation.duration = 300
        animation.interpolator = FastOutLinearInInterpolator()
        view.startAnimation(animation)
    }

    /**
     * 收起
     */
    fun collapse(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val viewHeight = view.measuredHeight
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height = viewHeight - (viewHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }
        }
        animation.duration = 300
        animation.interpolator = FastOutLinearInInterpolator()
        view.startAnimation(animation)
    }

    fun setVoteProgressAnim(view: View, viewWidth: Int) {
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                view.layoutParams.width = (viewWidth * interpolatedTime).toInt()
                view.requestLayout()
            }
        }
        animation.duration = 500
        animation.interpolator = FastOutLinearInInterpolator()
        view.startAnimation(animation)
    }
}