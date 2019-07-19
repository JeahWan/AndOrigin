package com.base.and.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.base.and.R;

import java.util.List;

/**
 * 基于viewflipper实现的无限轮播视图
 * 可配置进入移出方向，可配置停留时间
 * Created by gao on 2016/12/14.
 */
public class AutoLoopByViewFlipper extends FrameLayout {

    private Context context;
    private ViewFlipper vf_container;
    private Animation inAnim;
    private Animation outAnim;
    private int flipInterval;
    private OnItemClickListener mOnItemClickListener;

    public AutoLoopByViewFlipper(Context context) {
        this(context, null);
    }

    public AutoLoopByViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        init();
    }

    /**
     * 从下向上的进入动画
     *
     * @return
     */
    public static Animation inFromDownAnimation() {
        Animation inFromDown = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, +0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromDown.setDuration(350);
        inFromDown.setInterpolator(new AccelerateInterpolator());
        return inFromDown;
    }

    /**
     * 从下向上的移出动画
     *
     * @return
     */
    public static Animation outToUpAnimation() {
        Animation outtoUp = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, +0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f);
        outtoUp.setDuration(350);
        outtoUp.setInterpolator(new AccelerateInterpolator());
        return outtoUp;
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater.from(context).inflate(R.layout.layout_autoloop_viewflipper, this, true);
        vf_container = findViewById(R.id.vf_container);
        inAnim = inFromDownAnimation();
        outAnim = outToUpAnimation();
        flipInterval = 3000;
    }

    /**
     * 设置进入 移出动画
     *
     * @param inAnim
     * @param outAnim
     * @return
     */
    public AutoLoopByViewFlipper setAnimation(Animation inAnim, Animation outAnim) {
        this.inAnim = inAnim;
        this.outAnim = outAnim;
        return this;
    }

    /**
     * 设置轮播间隔
     *
     * @param flipInterval
     * @return
     */
    public AutoLoopByViewFlipper setInterval(int flipInterval) {
        this.flipInterval = flipInterval;
        return this;
    }

    /**
     * 赋值子view
     *
     * @param childViews
     */
    public AutoLoopByViewFlipper setChildViews(List<View> childViews) {
        vf_container.removeAllViews();
        for (int i = 0; i < childViews.size(); i++) {
            vf_container.addView(childViews.get(i));
        }
        return this;
    }

    /**
     * 设置点击事件的监听
     *
     * @param listener
     * @return
     */
    public AutoLoopByViewFlipper setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        vf_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = vf_container.getDisplayedChild();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(index);
                }
            }
        });
        return this;
    }

    /**
     * 开启轮询
     */
    public void startLoop() {
        if (vf_container.getChildCount() == 1) {
            return;
        }
        vf_container.setInAnimation(inAnim);
        vf_container.setOutAnimation(outAnim);
        vf_container.setFlipInterval(flipInterval);
        vf_container.startFlipping();
    }

    /**
     * 操作轮询状态
     *
     * @param boo true表示开启轮询，false表示中断
     */
    public void operationLoop(boolean boo) {
        if (boo) {
            vf_container.startFlipping();
        } else {
            vf_container.stopFlipping();
        }
    }

    /**
     * 向外提供viewflipper对象
     *
     * @return
     */
    public ViewFlipper getViewFlipper() {
        return vf_container;
    }

    /**
     * 控件条目点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
