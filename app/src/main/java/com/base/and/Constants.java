package com.base.and;

/**
 * 常量
 * Created by Makise on 2017/2/4.
 */

public class Constants {
    //viewpagerAct区分页面用到的常量
    public static final String VP_ACT_TYPE = "VP_ACT_TYPE";
    //接口返回的状态码
    public static final String OK = "OK";
    //测试接口地址
    public static final String TOP_MOVIE = "/v2/movie/top250";
    /**
     * SP Key
     */
    //version
    public static final String SP_VERSION_NAME = "SP_VERSION_NAME";
    //user_token
    public static final String USER_TOKEN = "USER_TOKEN";
    /**
     * webview约定参数
     */
    public static final String WEBVIEW_SHARE = "_share_";
    public static final String WEBVIEW_LINK = "_link_";
    //指向buil.gradle文件，debug包走测试地址，release包走线上地址
    public static String HOST = BuildConfig.HOST;
}
