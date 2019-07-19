package com.base.and;

import com.base.and.utils.SPUtils;

public class Constants {
    public static class Url {
        //协议地址
        public static final String ON_LINE_SERVER = "https://www.apiopen.top/";
        public static final String OFF_LINE_SERVER = "https://www.apiopen.top/";

        public static String getHost() {
            return !BuildConfig.DEBUG ? ON_LINE_SERVER : SPUtils.get().getString(SP_TOKEN.API_HOST, OFF_LINE_SERVER);
        }
    }

    public static class SP_TOKEN {
        public static final String USER_INFO = "USER_INFO";
        public static final String API_HOST = "API_HOST";
        public static final String API_HOST_H5 = "API_HOST_H5";
    }

    public static class ID {
        public static final String APP_SECRET = "4c77e29fg45lb45b531d98d9565f1";
    }

    public static class Code {
        public static final int OK = 200;
    }

    public static class Regex {
        /**
         * Regex of simple mobile.
         */
        public static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
        /**
         * Regex of exact mobile.
         * <p>china mobile: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 178, 182, 183, 184, 187, 188, 198</p>
         * <p>china unicom: 130, 131, 132, 145, 155, 156, 166, 171, 175, 176, 185, 186</p>
         * <p>china telecom: 133, 153, 173, 177, 180, 181, 189, 199</p>
         * <p>global star: 1349</p>
         * <p>virtual operator: 170</p>
         */
        public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(16[6])|(17[0,1,3,5-8])|(18[0-9])|(19[8,9]))\\d{8}$";
        /**
         * Regex of telephone number.
         */
        public static final String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}";
        /**
         * Regex of id card number which length is 15.
         */
        public static final String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        /**
         * Regex of id card number which length is 18.
         */
        public static final String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
        /**
         * Regex of email.
         */
        public static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        /**
         * Regex of url.
         */
        public static final String REGEX_URL = "[a-zA-z]+://[^\\s]*";
        /**
         * Regex of Chinese character.
         */
        public static final String REGEX_ZH = "^[\\u4e00-\\u9fa5]+$";
        /**
         * Regex of username.
         * <p>scope for "a-z", "A-Z", "0-9", "_", "Chinese character"</p>
         * <p>can't end with "_"</p>
         * <p>length is between 6 to 20</p>
         */
        public static final String REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
        /**
         * Regex of date which pattern is "yyyy-MM-dd".
         */
        public static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
        /**
         * Regex of ip address.
         */
        public static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    }
}
