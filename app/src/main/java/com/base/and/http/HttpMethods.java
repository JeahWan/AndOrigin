package com.base.and.http;

import com.base.and.base.BaseHttpMethods;
import com.base.and.data.Movie;

import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;


/**
 * 网络请求的方法实现
 * Created by Makise on 2017/2/4.
 */

public class HttpMethods extends BaseHttpMethods implements HttpContract.Methods {

    private ConcurrentHashMap<String, Object> mParams;
    private static HttpMethods INSTANCE;

    //构造方法私有
    private HttpMethods() {
    }

    //获取单例
    public static HttpMethods getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpMethods.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpMethods();
                }
            }
        }
        return INSTANCE;
    }

    //获取签名
    public HttpMethods params(ConcurrentHashMap<String, Object> params) {
        //TODO 这里可以增加一些接口请求的公共参数 签名等等
        mParams = params;
//        mParams.put("xxxx", xx);
//        mParams.put("xxxx", xx);
        return this;
    }

    /**
     * 以下开始实现具体的网络请求方法
     */

    @Override
    public void getTopMovie(Observer subscriber) {
        Observable observable = mService.topMovie(mParams)
                .map(new HttpResultFunc<Movie>());
        toSubscribe(observable, subscriber);
    }
}
