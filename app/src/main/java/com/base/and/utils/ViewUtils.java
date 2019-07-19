package com.base.and.utils;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ViewUtils {

    public static View view;
    private static long lastClickTime = 0;

    /**
     * @note 获取该activity所有view
     * @author liuh
     */
    public static List<View> getAllChildViews(Activity activity) {
        View view = activity.getWindow().getDecorView();
        return getAllChildViews(view);
    }

    private static List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                allchildren.addAll(getAllChildViews(viewchild));
                if (viewchild instanceof RecyclerView) {
                    for (int i1 = 0; i1 < ((RecyclerView) viewchild).getChildCount(); i1++) {
                        allchildren.add(((RecyclerView) viewchild).getChildAt(i1));
                    }
                }
            }
        }
        return allchildren;
    }

    public static boolean isFastDoubleClick(View _view) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        if (view != null) {
            if (view.equals(_view)) {
                return 0 < timeD && timeD < 800;
            } else {
                view = _view;
                return false;
            }
        } else {
            view = _view;
            return false;
        }
    }

    /**
     * hook 点击事件 防止连点
     *
     * @param view
     */
    private void hookOnClickListener(View view) {
        try {
            // 得到 View 的 ListenerInfo 对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            // 得到 原始的 OnClickListener 对象
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            final View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);
            // 用自定义的 OnClickListener 替换原始的 OnClickListener
            mOnClickListener.set(listenerInfo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), "hook", Toast.LENGTH_SHORT).show();
                    if (ViewUtils.isFastDoubleClick(v)) return;
                    if (originOnClickListener != null) {
                        originOnClickListener.onClick(v);
                    }
                }
            });
        } catch (Exception e) {
//            log.warn("hook clickListener failed!", e);
        }
    }
}
