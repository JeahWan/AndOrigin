package com.base.and.http;

import com.alibaba.fastjson.JSON;
import com.base.and.base.BaseHttpMethods;
import com.base.and.data.User;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Function;


/**
 * 网络请求的方法实现
 * Created by Makise on 2017/2/4.
 */

public class HttpMethods extends BaseHttpMethods implements HttpContract.Methods {

    //构造方法私有
    private HttpMethods() {
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //获取签名
    protected HashMap<String, String> genParams(HashMap<String, String> signParams) {
        //TODO 这里可以增加一些接口请求的公共参数 签名等等
//        signParams.clear();
//        signParams.put("xxxx", xx);
//        signParams.put("xxxx", xx);
        return signParams;
    }

    /**
     * 以下开始实现具体的网络请求方法
     */

    @Override
    public void getUserInfo(final String type, Observer subscriber) {
        HashMap<String, String> params = genParams(new HashMap<String, String>() {
            {
                put("type", type);
            }
        });

        Observable observable = mService.UserInfo(params, type)
                .map(new HttpResultFunc())
                .map(new Function<String, User>() {
                    @Override
                    public User apply(String s) {
                        return JSON.parseObject(s, User.class);
                    }
                });
        toSubscribe(observable, subscriber);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }
}
