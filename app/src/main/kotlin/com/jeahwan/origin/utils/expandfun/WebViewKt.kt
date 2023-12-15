package com.jeahwan.origin.utils.expandfun

import android.os.Build
import android.webkit.WebView
import com.google.gson.Gson

object WebViewKt {

    /**
     * webview调起js，并传递数据
     * [key]->业务方法名
     * [mapParam]->业务方法对应的参数
     */
    fun WebView.tuneUpJavascript(key: String, mapParam: Map<String, Any?>? = null) {
        try {
            val map = mapOf("K" to key, "V" to (mapParam ?: mapOf()))
            var mapJson = Gson().toJson(map).toString().replace("\"", "\\\"")
            mapJson = "\"" + mapJson + "\""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //这个方法大于4.4就能用，但 opp手机和vivo手机会出现 so 异常 所以大于6.0使用
                evaluateJavascript("callJsMethod($mapJson)", null)
            } else {
                loadUrl("javascript:callJsMethod($mapJson)")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}