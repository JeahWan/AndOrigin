package com.jeahwan.origin.utils

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.jeahwan.origin.App
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @Description:
 * @Date 2015年7月9日 下午2:21:06
 */
object NetWorkUtil {
    private const val NETWORK_WIFI = "WIFI"
    private const val NETWORK_5G = "5G"
    private const val NETWORK_4G = "4G"
    private const val NETWORK_3G = "3G"
    private const val NETWORK_2G = "2G"
    private const val NETWORK_UNKNOWN = "Unknown"
    private const val NET_TYPE_MOBILE = "MOBILE"
    private const val NET_TYPE_NO_NETWORK = "no_network"

    fun getNetType2Int(): Int {
        return when (getNetTypeName(App.instance)) {
            NETWORK_WIFI -> 1
            NETWORK_5G -> 5
            NETWORK_4G -> 4
            NETWORK_3G -> 3
            NETWORK_2G -> 2
            else -> 99
        }
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    fun getNetTypeName(context: Context?): String {
        if (context == null) {
            return ""
        }
        if (isWifiConnected(context)) {
            return NETWORK_WIFI
        }
        val conManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = conManager.activeNetworkInfo
        return if (networkInfo != null && networkInfo.isAvailable) {
            when (networkInfo.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    NETWORK_WIFI
                }
                ConnectivityManager.TYPE_MOBILE -> {
                    when (networkInfo.subtype) {
                        TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_2G
                        TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_3G
                        TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_4G
                        TelephonyManager.NETWORK_TYPE_NR -> NETWORK_5G
                        else -> {
                            val subtypeName = networkInfo.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                NETWORK_3G
                            } else {
                                NETWORK_UNKNOWN
                            }
                        }
                    }
                }
                else -> {
                    NETWORK_UNKNOWN
                }
            }
        } else NETWORK_UNKNOWN
    }

    /**
     * 判断手机是否开启网络 wifi或3G 有一个开着就为true
     *
     * @param context
     * @return
     */
    fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }

    /**
     * 判断手机的网络是否可以 . 无信号，飞行模式都为false
     *
     * @param context
     * @return
     */
    fun isMobileConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mMobileNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable
            }
        }
        return false
    }

    /**
     * 判断wifi是否打开，可用
     *
     * @param context
     * @return
     */
    private fun isWifiConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return mWiFiNetworkInfo?.isConnected ?: false
        }
        return false
    }

    /**
     * 判断网络异常类型并返回异常提示文本
     */
    fun networkStatusInfo(e: Throwable): String? {
        return if (e is SocketTimeoutException ||
            e is ConnectException ||
            e is UnknownHostException
        ) {
            "网络异常"
        } else {
            e.message
        }
    }
}