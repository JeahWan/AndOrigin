package com.base.and;

import android.app.Activity;
import android.app.Application;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.alibaba.fastjson.JSON;
import com.base.and.data.User;
import com.base.and.utils.PackageUtils;
import com.base.and.utils.PreferencesUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义application入口
 * Created by Makise on 2017/2/4.
 */

public class App extends Application {
    private static App instance;
    private static List<Activity> activitys = null; //activity实例集合

    public App() {
        activitys = new LinkedList<>();
    }

    // 单例模式中获取唯一的MyApplication实例
    public static App getInstance() {
        if (null == instance) {
            instance = new App();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        instance = this;
        activitys = new LinkedList<>();
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null) {
            if (activitys.isEmpty()) {
                activitys.add(activity);
            } else if (!activitys.contains(activity)) {
                activitys.add(activity);
            }
        }
    }

    // 移除Activity到容器中
    public void removeActivity(Activity activity) {
        if (activitys != null && activitys.contains(activity)) {
            activitys.remove(activity);
        }
    }

    /**
     * 完全退出
     */
    public void exit() {
        //activity组件finish
        if (activitys != null && !activitys.isEmpty()) {
            for (Activity activity : activitys) {
                if (activity != null) {
                    activity.finish();
                }
            }
        }
        //完全退出
        System.exit(0);
    }

    /**
     * 获取用户
     *
     * @return
     */
    public User getUser() {
        String userInfo = PreferencesUtils.getString(this, Constants.USER_TOKEN);
        if (userInfo != null) {
            return JSON.parseObject(userInfo, User.class);
        } else {
            return new User();
        }
    }

    /**
     * 持久化登录信息
     *
     * @param user
     */
    public void saveUser(User user) {
        if (user == null) {
            user = new User();
        }
        PreferencesUtils.putString(this, Constants.USER_TOKEN, JSON.toJSONString(user));
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {
        // 清除cookie即可彻底清除缓存
        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager.getInstance().removeAllCookie();
    }

    /**
     * 是否版本升级
     *
     * @return true表示升级，false表示没有升级
     */
    public boolean isUpgrade() {
        String versionPre = PreferencesUtils.getString(getApplicationContext(), Constants.SP_VERSION_NAME, null);
        String versionNow = PackageUtils.getAppVersionName(getApplicationContext());
        return !versionNow.equals(versionPre);
    }
}
