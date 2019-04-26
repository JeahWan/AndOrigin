package com.base.and.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Description: 系统工具类
 * @Date 2015年7月9日 下午1:33:13
 */
public class Tools {
    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取 deviceId
     *
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = "0";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return deviceId;
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (!TextUtils.isEmpty(tm.getDeviceId())) {
                deviceId = tm.getDeviceId();
            }
        } catch (Exception e) {
            Log.d("Tools", "tm.getDeviceId fail!!!");
        }
        return deviceId;
    }

    /**
     * 获取 user-agent
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getUserAgent(Context context, String appName) {

        //useragent 示例：appName/2.5.1 (HM 2A; Android 4.4.4; API/19) Channel/xxx_store NetType/WIFI Pixel/1280*720
        String versionName, channelId, netTypeName, screenPixel;
        try {
            versionName = PackageUtils.getAppVersionName(context);
        } catch (Exception e) {
            versionName = "";
        }
        try {
            channelId = getChannelId(context);
        } catch (Exception e) {
            channelId = "";
        }
        try {
            netTypeName = NetWorkUtil.getNetTypeName(context);
        } catch (Exception e) {
            netTypeName = "";
        }
        try {
            screenPixel = ScreenUtils.getScreenPixel(context);
        } catch (Exception e) {
            screenPixel = "";
        }
        return String.format(appName + "/%s (" + android.os.Build.MODEL + "; Android "
                        + android.os.Build.VERSION.RELEASE + "; API/"
                        + android.os.Build.VERSION.SDK + ")"
                        + " Channel/%s NetType/%s Pixel/%s",
                versionName,
                channelId,
                netTypeName,
                screenPixel);
    }

    /**
     * 获取手机型号等信息
     *
     * @return
     */
    public static String getMobileInfo() {
        return "手机型号: " + android.os.Build.MODEL + ",SDK版本: " + android.os.Build.VERSION.SDK
                + ",系统版本: " + android.os.Build.VERSION.RELEASE + "------";
    }

    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSizeLong();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocksLong();
        // 返回SD卡空闲大小
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 友盟设备识别信息输出
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
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
        return null;
    }

    /**
     * 判断是否有虚拟键盘，但是对于vivo这个奇葩来说没用。。。目前是2016年11月1日，希望日后vivo改进吧
     *
     * @param activity
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return true;
        }

        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    /**
     * 获取 SimSerialNumber
     *
     * @return
     */
    public static String getSimSerialNumber(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * 获取 DeviceSoftwareVersion
     *
     * @return
     */
    public static String getDeviceSoftwareVersion(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceSoftwareVersion();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    /**
     * 从assets的ChannelConfig.json文件中解析出当前渠道id
     * <p>
     * 适用于使用talkingdata多渠道打包工具
     *
     * @return 渠道标识
     */
    public static String getChannelId(Context context) {
        AssetManager s = context.getAssets();
        try {
            InputStream is = s.open("ChannelConfig.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String json = new String(buffer, "utf-8");
            is.close();
            JSONObject obj;
            try {
                obj = new JSONObject(json);
                String channelId = obj.getString("td_channel_id");
                if (!channelId.isEmpty()) {
                    return channelId;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}