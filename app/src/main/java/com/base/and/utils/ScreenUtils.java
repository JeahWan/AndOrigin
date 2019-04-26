package com.base.and.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * ScreenUtils
 * <ul>
 * <strong>Convert between dp and sp</strong>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-14
 */
public class ScreenUtils {

    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static String getScreenPixel(Context context) {
        try {
            return getScreenHeigh(context) + "*" + getScreenWidth(context);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;

    }

    /**
     * 获取屏幕高度（华为不包含虚拟导航栏，魅族却包含虚拟导航栏！！！这是为啥？？）
     *
     * @param context
     * @return
     */
    public static int getScreenHeigh(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 真正获取屏幕高度，包含底部虚拟导航栏
     *
     * @param context
     * @return
     */
    public static int getScreenHeighNotcontain(Context context) {
        int heightPixels;
        WindowManager w = ((Activity) context).getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        heightPixels = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                heightPixels = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        else if (Build.VERSION.SDK_INT >= 17)
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                Display.class.getMethod("getRealSize",
                        android.graphics.Point.class).invoke(d, realSize);
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        return heightPixels;
    }

    public static int dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + 0.5f);
    }

    public static int pxToDpCeilInt(Context context, float px) {
        return (int) (pxToDp(context, px) + 0.5f);
    }

    public static int getScreenHeightVivo(Context context) {
        if (SystemBarConfig.hasNavBar(context) && "vivo".equals(android.os.Build.MANUFACTURER)) {
            //vivo的 返回不带虚拟键的高度
            return getScreenHeigh(context);
        } else {
            //其他的返回 包含虚拟键的高度
            return getScreenHeighNotcontain(context);
        }
    }
}
