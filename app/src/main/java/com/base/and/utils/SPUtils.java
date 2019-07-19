package com.base.and.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.base.and.App;


/**
 * Created by juemuren on 2017/11/21.
 */

public class SPUtils {

    private static final String SP_NAME = App.getInstance().getPackageName();
    private static SPUtils instance;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private SPUtils(Context context) {
        if (context == null) {
            throw new NullPointerException("初始化" + SPUtils.class.getName() + "类时Context实例不能为空");
        }
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static synchronized SPUtils get() {
        if (instance == null) {
            instance = new SPUtils(App.getInstance());
        }
        return instance;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

}
