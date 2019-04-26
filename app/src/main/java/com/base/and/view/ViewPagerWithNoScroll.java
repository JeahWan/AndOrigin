package com.base.and.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.base.and.utils.rxbus.RxTask;

import java.util.concurrent.TimeUnit;

/**
 * 禁止滑动的viewpager
 * Created by Makise on 2016/8/10.
 */
public class ViewPagerWithNoScroll extends ViewPager {

    public ViewPagerWithNoScroll(Context context) {
        this(context, null);
    }

    public ViewPagerWithNoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        //自定义滑动速度
        new ViewPagerScroller(getContext()).initViewPagerScroll(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
//        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
//        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 下一页
     */
    public void next() {
        RxTask.doInUIThreadDelay(new RxTask.UITask() {
            @Override
            public void doInUIThread() {
                setCurrentItem(getCurrentItem() + 1);
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    /**
     * 上一页
     */
    public void previous() {
        RxTask.doInUIThreadDelay(new RxTask.UITask() {
            @Override
            public void doInUIThread() {
                setCurrentItem(getCurrentItem() - 1);
            }
        }, 200, TimeUnit.MILLISECONDS);
    }
}
