package com.base.and.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * 收集设备信息 工具类
 * Created by Makise on 2017/3/14.
 */

public class DeviceInfoUtils {

    /**
     * 获取设备唯一号
     *
     * @return
     */
    public static String getDeviceUniqueNumber(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(
                        context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }

    /**
     * 获取重力仪信息
     *
     * @return
     */
    public static String getGravimeterInfo() {
        //TODO 重力仪逻辑
        return "";
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetTypeName(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo.isConnected()) {
            return "WIFI";
        } else {
            ConnectivityManager conManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                return "未知";
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
    }

    /**
     * 获取网络运营商
     *
     * @param context
     * @return
     */
    private static String getMobileType(Context context) {
        String type = "";
        TelephonyManager iPhoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iNumeric = iPhoneManager.getSimOperator();
        if (iNumeric.length() > 0) {
            if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
                type = "中国移动";
            } else if (iNumeric.equals("46001")) {
                type = "中国联通";
            } else if (iNumeric.equals("46003")) {
                type = "中国电信";
            }
        }
        return type;
    }

    /**
     * 获取机型
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取分辨率
     *
     * @return
     */
    public static String getScreenPixel(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels + "*" + dm.widthPixels;
    }

    /**
     * 获取操作系统版本
     *
     * @return
     */
    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE + "; API/" + android.os.Build.VERSION.SDK;
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionName + "_" + pi.versionCode;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
