package com.base.and.utils;

/**
 * Created by Administrator on 2017/11/6.
 */

import android.util.Base64;

import com.base.and.App;
import com.base.and.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 请求校验工具类
 */
public class SignUtils {

    public static String formatUrlMap(Map<String, Object> paraMap, boolean keyToLower) {
        String buff = "";
        Map<String, Object> tmpMap = paraMap;
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {

                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
//                if (!TextUtils.isEmpty(item.getKey())) {
                String key = item.getKey();
                Object val = item.getValue();

                if (keyToLower) {
                    buf.append(key.toLowerCase() + val);
                } else {
                    buf.append(key + val);
                }
//                buf.append("&");
//                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff;
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }

    public static String getSign(Map<String, Object> paraMap, String time) {
        String url = formatUrlMap(paraMap, false);
        String token = App.getInstance().getUser().token;
        String data = Base64.encodeToString(time.getBytes(), Base64.NO_WRAP) + token + Constants.ID.APP_SECRET + url;

        String result = EncryptHelper.toMD5(data.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "").replaceAll("\\\\", ""));

        return result;
    }

}