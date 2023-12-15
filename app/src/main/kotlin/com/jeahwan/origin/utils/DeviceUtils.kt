package com.jeahwan.origin.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.jeahwan.origin.App
import com.jeahwan.origin.Constants
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/8/1
 * desc  : utils about device
</pre> *
 */
object DeviceUtils {
    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    fun getSDKVersionName(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    fun getSDKVersionCode(): Int {
        return Build.VERSION.SDK_INT
    }

    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @get:SuppressLint("HardwareIds")
    val androidID: String
        get() {
            val id = Settings.Secure.getString(
                App.instance.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            return id ?: ""
        }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`,
     * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET, permission.CHANGE_WIFI_STATE])
    fun getMacAddress(): String {
        val macAddress = getMacAddress(*emptyArray<String>())
        if (!TextUtils.isEmpty(macAddress) || wifiEnabled) return macAddress
        wifiEnabled = true
        wifiEnabled = false
        return getMacAddress(*(emptyArray<String>()))
    }

    /**
     * Enable or disable wifi.
     *
     * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     */
    @set:RequiresPermission(permission.CHANGE_WIFI_STATE)
    private var wifiEnabled: Boolean
        private get() {
            @SuppressLint("WifiManagerLeak") val manager =
                App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager.isWifiEnabled
        }
        private set(enabled) {
            @SuppressLint("WifiManagerLeak") val manager =
                App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (enabled == manager.isWifiEnabled) return
            manager.isWifiEnabled = enabled
        }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET])
    fun getMacAddress(vararg excepts: String?): String {
        var macAddress = macAddressByNetworkInterface
        if (isAddressNotInExcepts(macAddress, *excepts as Array<out String>)) {
            return macAddress
        }
        macAddress = macAddressByInetAddress
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = macAddressByWifiInfo
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = macAddressByFile
        return if (isAddressNotInExcepts(macAddress, *excepts)) {
            macAddress
        } else ""
    }

    private fun isAddressNotInExcepts(address: String, vararg excepts: String): Boolean {
        if (TextUtils.isEmpty(address)) {
            return false
        }
        if ("02:00:00:00:00:00" == address) {
            return false
        }
        if (excepts == null || excepts.size == 0) {
            return true
        }
        for (filter in excepts) {
            if (filter != null && filter == address) {
                return false
            }
        }
        return true
    }

    @get:SuppressLint("MissingPermission", "HardwareIds")
    private val macAddressByWifiInfo: String
        private get() {
            try {
                val wifi = App.instance
                    .applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                if (wifi != null) {
                    val info = wifi.connectionInfo
                    if (info != null) {
                        val macAddress = info.macAddress
                        if (!TextUtils.isEmpty(macAddress)) {
                            return macAddress
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }
    private val macAddressByNetworkInterface: String
        private get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (ni == null || !ni.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.size > 0) {
                        val sb = StringBuilder()
                        for (b in macBytes) {
                            sb.append(String.format("%02x:", b))
                        }
                        return sb.substring(0, sb.length - 1)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }
    private val macAddressByInetAddress: String
        private get() {
            try {
                val inetAddress = inetAddress
                if (inetAddress != null) {
                    val ni = NetworkInterface.getByInetAddress(inetAddress)
                    if (ni != null) {
                        val macBytes = ni.hardwareAddress
                        if (macBytes != null && macBytes.size > 0) {
                            val sb = StringBuilder()
                            for (b in macBytes) {
                                sb.append(String.format("%02x:", b))
                            }
                            return sb.substring(0, sb.length - 1)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }

    // To prevent phone of xiaomi return "10.0.2.15"
    private val inetAddress: InetAddress?
        private get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp) continue
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val inetAddress = addresses.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            val hostAddress = inetAddress.hostAddress
                            if (hostAddress.indexOf(':') < 0) return inetAddress
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return null
        }
    private val macAddressByFile: String
        private get() {
            var result = ShellUtils.execCmd("getprop wifi.interface", false)
            if (result.result == 0) {
                val name = result.successMsg
                if (name != null) {
                    result = ShellUtils.execCmd("cat /sys/class/net/$name/address", false)
                    if (result.result == 0) {
                        val address = result.successMsg
                        if (address != null && address.length > 0) {
                            return address
                        }
                    }
                }
            }
            return "02:00:00:00:00:00"
        }

    /**
     * Return the model of device.
     *
     * e.g. MI2SC
     *
     * @return the model of device
     */
    fun getModel(): String {
        var model = Build.MODEL
        model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
        return model
    }

    fun getBrand(): String {
        var brand = Build.BRAND
        brand = brand?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
        return brand
    }

    /**
     * Return the IMEI.
     *
     * If the version of SDK is greater than 28, it will return an empty string.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the IMEI
     */
    @get:RequiresPermission(permission.READ_PHONE_STATE)
    val iMEI: String?
        get() = getImeiOrMeid(true)

    /**
     * Return the MEID.
     *
     * If the version of SDK is greater than 28, it will return an empty string.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the MEID
     */
    @get:RequiresPermission(permission.READ_PHONE_STATE)
    val mEID: String?
        get() = getImeiOrMeid(false)

    @SuppressLint("HardwareIds")
    @RequiresPermission(permission.READ_PHONE_STATE)
    fun getImeiOrMeid(isImei: Boolean): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ""
        }
        val tm = telephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return if (isImei) {
                getMinOne(tm.getImei(0), tm.getImei(1))
            } else {
                getMinOne(tm.getMeid(0), tm.getMeid(1))
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val ids =
                getSystemPropertyByReflect(if (isImei) "ril.gsm.imei" else "ril.cdma.meid")
            if (!TextUtils.isEmpty(ids)) {
                val idArr = ids.split(",".toRegex()).toTypedArray()
                return if (idArr.size == 2) {
                    getMinOne(idArr[0], idArr[1])
                } else {
                    idArr[0]
                }
            }
            var id0 = tm.deviceId
            var id1: String? = ""
            try {
                val method = tm.javaClass.getMethod("getDeviceId", Int::class.javaPrimitiveType)
                id1 = method.invoke(
                    tm,
                    if (isImei) TelephonyManager.PHONE_TYPE_GSM else TelephonyManager.PHONE_TYPE_CDMA
                ) as String?
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (isImei) {
                if (id0 != null && id0.length < 15) {
                    id0 = ""
                }
                if (id1 != null && id1.length < 15) {
                    id1 = ""
                }
            } else {
                if (id0 != null && id0.length == 14) {
                    id0 = ""
                }
                if (id1 != null && id1.length == 14) {
                    id1 = ""
                }
            }
            return getMinOne(id0, id1)
        } else {
            val deviceId = tm.deviceId
            if (isImei) {
                if (deviceId != null && deviceId.length >= 15) {
                    return deviceId
                }
            } else {
                if (deviceId != null && deviceId.length == 14) {
                    return deviceId
                }
            }
        }
        return ""
    }

    private fun getMinOne(s0: String?, s1: String?): String? {
        val empty0 = TextUtils.isEmpty(s0)
        val empty1 = TextUtils.isEmpty(s1)
        if (empty0 && empty1) return ""
        if (!empty0 && !empty1) {
            return if (s0!!.compareTo(s1!!) <= 0) {
                s0
            } else {
                s1
            }
        }
        return if (!empty0) s0 else s1
    }

    private val telephonyManager: TelephonyManager
        private get() = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private fun getSystemPropertyByReflect(key: String): String {
        try {
            @SuppressLint("PrivateApi") val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get", String::class.java, String::class.java)
            return getMethod.invoke(clz, key, "") as String
        } catch (e: Exception) { /**/
        }
        return ""
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return true 为模拟器
     */
    fun notHasLightSensorManager(context: Context): String {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) //光
        return if (null == sensor8) {
            ""
        } else {
            sensor8.name
        }
    }

    fun getDeviceID(): String {
        if ((ActivityCompat.checkSelfPermission(
                App.instance,
                permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED) && !iMEI.isNullOrEmpty()
        ) {
            return iMEI!!
        }
        if (!TextUtils.isEmpty(SPUtils.get().getString(Constants.SPKey.OAID))) {
            return SPUtils.get().getString(Constants.SPKey.OAID)
        }
        return ""
    }

    /**
     * 是否是平板
     *
     * @return 是平板则返回true，反之返回false
     */
    fun isPad(): Boolean {
        val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)
        val x = (dm.widthPixels / dm.xdpi).toDouble().pow(2.0)
        val y = (dm.heightPixels / dm.ydpi).toDouble().pow(2.0)
        val screenInches = sqrt(x + y) // 屏幕尺寸
        return screenInches >= 7.0
    }

    /**
     * 当前是否是鸿蒙系统
     * 根据是否能调用Harmony JAVA API判断
     */
    fun isHarmonyOs(): Boolean {
        return try {
            Class.forName("ohos.utils.system.SystemCapability")
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getHarmonyOsVersion(): String {
        return getProperty("hw_sc.build.platform.version", "");
    }

    private fun getProperty(property: String, defaultValue: String): String {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val declaredMethod = clazz.getDeclaredMethod("get", String::class.java)
            val value = declaredMethod.invoke(clazz, property) as String?
            value ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    /**
     * 获取当前系统语言
     */
    fun currentLanguage(): String {
        val locale: Locale = App.instance.resources.configuration.locale
        return locale.language
    }
}