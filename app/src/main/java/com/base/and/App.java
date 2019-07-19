package com.base.and;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.and.data.User;
import com.base.and.ui.CrashActivity;
import com.base.and.ui.SplashActivity;
import com.base.and.utils.SPUtils;
import com.base.and.utils.ShakeUtils;
import com.base.and.utils.StringUtils;
import com.base.and.utils.crash.CaocConfig;

import java.util.LinkedList;
import java.util.List;

public class App extends Application {
    private static App instance;
    private static List<Activity> activitys = null; //activity实例集合
    private User user;
    private AlertDialog dialog;

    public App() {
        activitys = new LinkedList<>();
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化全局异常崩溃
        initCrash();
        //测试包打开环境切换功能
        if (BuildConfig.DEBUG) {
            initSwitchHostDialog();
        }
    }

    private void initSwitchHostDialog() {
        //地址数组
        final String[][] addrArr = new String[][]{
                {
                        "测试",
                        Constants.Url.OFF_LINE_SERVER
                },
                {
                        "生产",
                        Constants.Url.ON_LINE_SERVER
                }
        };
        //弹窗显示的数组
        final String[] showArr = new String[addrArr.length];
        for (int i = 0; i < addrArr.length; i++) {
            if (SPUtils.get().getString(Constants.SP_TOKEN.API_HOST, Constants.Url.OFF_LINE_SERVER).equals(addrArr[i][1])) {
                addrArr[i][0] = addrArr[i][0] + "(当前环境)";
            }
            showArr[i] = ((i == 0 ? "\n" : "") +
                    addrArr[i][0] + "\n" +
                    addrArr[i][1] + "").replace("/base-mobile-client/api/v1/", "");
        }
        new ShakeUtils(this).setShakeListener(new ShakeUtils.ShakeListener() {
            @Override
            public void onShake() {
                if (dialog == null && getCurrentActivity() != null) {
                    //初始化弹窗
                    dialog = new AlertDialog.Builder(getCurrentActivity()).setItems(showArr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //保存地址到sp
                            SPUtils.get().putString(Constants.SP_TOKEN.API_HOST, addrArr[i][1]);
                            //退出登录
                            App.getInstance().saveUser(null);
                            //退出app
                            App.getInstance().exit();
                        }
                    }).create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setTitle("切换环境(选择后重新打开app即可)");
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.gravity = Gravity.CENTER;
                    dialog.getWindow().setAttributes(params);
                }
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        });
    }

    private void initCrash() {
        if (!BuildConfig.DEBUG) {
            //线上错误处理只弹toast即可
            CrashHandler.getInstance().init(this);
            return;
        }
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(BuildConfig.DEBUG) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
//                .errorDrawable(R.drawable.ic_launcher) //错误图标
                .restartActivity(SplashActivity.class) //重新启动后的activity
                .errorActivity(CrashActivity.class) //崩溃后的错误activity
                .eventListener(null) //崩溃后的错误监听
                .apply();
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

    public User getUser() {
        if (user == null) {
            String user = SPUtils.get().getString(Constants.SP_TOKEN.USER_INFO);
            if (!TextUtils.isEmpty(user)) {
                //解密
                user = StringUtils.decryptPassword(user, Constants.ID.APP_SECRET);
                this.user = JSONObject.parseObject(user, User.class);
            } else {
                this.user = new User();
            }
        }
        return user;
    }

    public void saveUser(User user) {
        if (user == null) {
            //退出登录
//            MainActivity.LOGOUT_REFRESH = true;
            user = new User();
        }
        this.user = user;
        //加密存储
        String loginModuleStr = StringUtils.encryptPassword(JSON.toJSONString(this.user), Constants.ID.APP_SECRET);
        SPUtils.get().putString(Constants.SP_TOKEN.USER_INFO, loginModuleStr);
    }

    /**
     * 取得当前运行的activity 提供给dialog使用
     *
     * @return
     */
    public Activity getCurrentActivity() {
        return activitys != null && !activitys.isEmpty() ? activitys.get(activitys.size() - 1) : null;
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
}
