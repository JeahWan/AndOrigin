package com.base.and.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeUtils implements SensorEventListener {
    private static final int SPEED_SHRESHOLD = 4500;//这个值越大需要越大的力气来摇晃手机
    private static final int UPTATE_INTERVAL_TIME = 50;
    private Context mContext;
    private SensorManager sensorManager;
    private Sensor sensor;
    private ShakeListener shakeListener;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;

    public ShakeUtils(Context c) {
        mContext = c;
        start();
    }

    /**
     * 启动
     */
    public void start() {
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * 停止
     */
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < UPTATE_INTERVAL_TIME)
            return;
        lastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;

        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 5000;
        if (speed >= SPEED_SHRESHOLD) {
            shakeListener.onShake();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setShakeListener(ShakeListener shakeListener) {
        this.shakeListener = shakeListener;
    }

    public interface ShakeListener {
        void onShake();
    }
}