package com.base.and.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * @Description:
 * @Date 2015年7月9日 下午2:21:06
 */
public class NetWorkUtil {

    public static final String NET_TYPE_WIFI = "WIFI";
    public static final String NET_TYPE_MOBILE = "MOBILE";
    public static final String NET_TYPE_NO_NETWORK = "no_network";

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetTypeName(Context context) {
        if (isWifiConnected(context)) {
            return NET_TYPE_WIFI;
        }
        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return "";
        }
        int subtype = networkInfo.getSubtype();
        switch (subtype) {
            case 1:
            case 2:
            case 4:
            case 11:
            case 7:
                return "2G";
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
                return "3G";
            default:
                return "4G";
        }
    }

    /**
     * 判断手机是否开启网络 wifi或3G 有一个开着就为true
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断手机的网络是否可以 . 无信号，飞行模式都为false
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断wifi是否打开，可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWiFiNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * 判断网络异常类型并返回异常提示文本
     */
    public static String networkStatusInfo(Throwable e) {
        String errorMessage;
        if (e instanceof SocketTimeoutException ||
                e instanceof ConnectException ||
                e instanceof UnknownHostException) {
            errorMessage = "网络异常";
        } else {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }
}
