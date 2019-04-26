package com.base.and.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: URLutils
 * @Date 2015年9月2日 下午5:11:37
 */
public class URLUtil {
    /**
     * 获取url参数值
     *
     * @param url
     * @param key
     * @param def
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getParam(String url, String key, String def)
            throws UnsupportedEncodingException {
        Map<String, String> m = parseUrlParams(url);
        String val = def;
        if (m.get(key) != null) {
            val = m.get(key);
        }
        return val;
    }

    public static int getParam2Int(String url, String key, int def)
            throws UnsupportedEncodingException {
        String val = getParam(url, key, def + "");
        return Integer.parseInt(val);
    }

    public static Map<String, String> parseUrlParams(String url)
            throws UnsupportedEncodingException {
        Map<String, String> tmp = new HashMap<String, String>();
        url = URLDecoder.decode(url, "UTF-8");
        url = URLDecoder.decode(url, "UTF-8");
        if (!StringUtils.isBlank(url)) {
            int s = url.indexOf("?");
            if (s != -1) {
                String params = url.substring(s + 1);
                String[] kns = params.split("&");
                for (String kn : kns) {
                    String[] tt = kn.split("=");
                    if (tt.length == 2) {
                        tmp.put(tt[0], tt[1]);
                    }
                }
            }

        }
        return tmp;
    }
}
