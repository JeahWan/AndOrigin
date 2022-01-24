package com.jeahwan.origin.utils

import android.util.Base64
import com.jeahwan.origin.App
import com.jeahwan.origin.Constants.Server.appSecret
import com.jeahwan.origin.utils.MD5HexHelper.toMD5
import java.util.*

/**
 * Created by Administrator on 2017/11/6.
 */
/**
 * 请求校验工具类
 */
object SignUtils {
    private fun formatUrlMap(paraMap: Map<String, Any>, keyToLower: Boolean): String? {
        var buff: String
        try {
            val infoIds: List<Map.Entry<String, Any>> = ArrayList(paraMap.entries)
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds) { o1, o2 -> (o1.key).compareTo(o2.key) }
            // 构造URL 键值对的格式
            val buf = StringBuilder()
            for (item: Map.Entry<String, Any> in infoIds) {
//                if (!TextUtils.isEmpty(item.getKey())) {
                if (keyToLower) {
                    buf.append(item.key.lowercase(Locale.getDefault()) + item.value)
                } else {
                    buf.append(item.key + item.value)
                }
                //                buf.append("&");
//                }
            }
            buff = buf.toString()
        } catch (e: Exception) {
            return null
        }
        return buff
    }

    fun getSign(
        paraMap: Map<String, Any>,
        time: String,
    ): String {
        val url = formatUrlMap(paraMap, false)
        val token = App.instance.getUser().token
        val data = Base64.encodeToString(
            time.toByteArray(),
            Base64.NO_WRAP
        ) + token + appSecret + url
        return toMD5(
            data.replace("\\[".toRegex(), "").replace("]".toRegex(), "").replace("\"".toRegex(), "")
        )
    }
}