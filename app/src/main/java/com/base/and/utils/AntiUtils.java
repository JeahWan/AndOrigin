package com.base.and.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Monkey on 2018/11/5.
 */

public class AntiUtils {
    /**
     * 获得基带版本
     *
     * @return String
     */
    public static String getBaseBandVersion() {
        String version = "";
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Object object = clazz.newInstance();
            Method method = clazz.getMethod("get", String.class, String.class);
            Object result = method.invoke(object, "gsm.version.baseband", "no message");
            version = (String) result;
        } catch (Exception e) {
        }
        return version;
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return true 为模拟器
     */
    public static String notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
        if (null == sensor8) {
            return "";
        } else {
            return sensor8.getName();
        }
    }

    /**
     * 判断蓝牙是否有效来判断是否为模拟器
     *
     * @return true 为模拟器
     */
    public static String notHasBlueTooth() {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba == null) {
            return "";
        } else {
            // 如果有蓝牙不一定是有效的。获取蓝牙名称，若为null 则默认为模拟器
            String name = ba.getName();
            if (TextUtils.isEmpty(name)) {
                return "";
            } else {
                return name;
            }
        }
    }


}
