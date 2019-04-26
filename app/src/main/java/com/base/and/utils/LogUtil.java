package com.base.and.utils;

import android.text.TextUtils;
import android.util.Log;

import com.base.and.BuildConfig;

/**
 * 日志记录
 */
public class LogUtil {


    public static void info(String msg) {
        info(LogUtil.class, msg);
    }

    public static void info(@SuppressWarnings("rawtypes") Class clazz, String msg) {

        if (BuildConfig.DEBUG && !TextUtils.isEmpty(msg)) {
            Log.i(clazz.getSimpleName(), msg);
        }
    }
}
