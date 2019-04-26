package com.base.and.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Makise on 2016/8/5.
 */
public class ToastUtil {
    // Toast
    private static Toast toast;

    private static boolean isError;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static Toast showShort(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();

        return toast;
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static Toast showShort(Context context, int message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();

        return toast;
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static Toast showLong(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();

        return toast;
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static Toast show(Context context, CharSequence message, int duration) {
        if (null == toast) {
            toast = Toast.makeText(context, message, duration);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
        return toast;
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (null == toast) {
            toast = Toast.makeText(context, message, duration);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * Hide the toast, if any.
     */
    public static void hideToast() {
        if (null != toast) {
            toast.cancel();
        }
    }

    /**
     * 规定时间内只弹一次的toast
     *
     * @param context
     * @param message
     */
    public static void showError(Context context, CharSequence message, boolean center) {
        //判断是否已弹出
        if (isError) return;
        isError = true;

        if (toast != null)
            toast = null;
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        if (center)
            toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        //3秒后继续弹出
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isError = false;
            }
        }, 3000);
    }
}
