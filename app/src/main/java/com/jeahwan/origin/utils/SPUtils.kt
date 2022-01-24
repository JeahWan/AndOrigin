package com.jeahwan.origin.utils

import android.content.Context
import android.content.SharedPreferences
import com.jeahwan.origin.App

/**
 * Created by juemuren on 2017/11/21.
 */
class SPUtils private constructor(context: Context?) {
    private val sp: SharedPreferences
    private val editor: SharedPreferences.Editor
    fun putString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String?, defaultValue: String = ""): String {
        return if (sp.getString(key, defaultValue).isNullOrEmpty()) "" else sp.getString(
            key,
            defaultValue
        )!!
    }

    fun putInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInt(key: String?, defaultValue: Int = 0): Int {
        return sp.getInt(key, defaultValue)
    }

    fun putFloat(key: String?, value: Float) {
        editor.putFloat(key, value)
        editor.commit()
    }

    fun getFloat(key: String?, defaultValue: Float = 0f): Float {
        return sp.getFloat(key, defaultValue)
    }

    fun putLong(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.commit()
    }

    fun getLong(key: String?, defaultValue: Long = 0): Long {
        return sp.getLong(key, defaultValue)
    }

    fun putBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBoolean(key: String?, defaultValue: Boolean = false): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    fun remove(key: String?) {
        editor.remove(key)
        editor.commit()
    }

    companion object {
        private val SP_NAME = App.instance.packageName
        private var instance: SPUtils? = null

        @Synchronized
        fun get(): SPUtils {
            if (instance == null) {
                instance = SPUtils(App.instance)
            }
            return instance!!
        }
    }

    init {
        if (context == null) {
            throw NullPointerException("初始化" + SPUtils::class.java.name + "类时Context实例不能为空")
        }
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        editor = sp.edit()
    }
}