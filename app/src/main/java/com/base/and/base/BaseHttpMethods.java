package com.base.and.base;

import com.base.and.BuildConfig;
import com.base.and.Constants;
import com.base.and.http.HttpContract;
import com.base.and.http.ResultException;
import com.base.and.utils.Tools;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 网络请求基类
 * Created by Makise on 2017/2/4.
 */

public class BaseHttpMethods {
    //网络请求超时时间
    private static final int DEFAULT_TIMEOUT = 15;
    protected HttpContract.Services mService;

    protected BaseHttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        //debug包打印log
        httpClientBuilder.addInterceptor(new LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .setLevel(Level.BODY)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .build());

        //设置统一的User-Agent
        httpClientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("User-Agent", Tools.getUserAgent(BaseActivity.getContext(), "and_base"))
                        .build();
                return chain.proceed(request);
            }
        });

        mService = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constants.HOST)
                .build()
                .create(HttpContract.Services.class);
    }

    //添加线程管理并订阅
    protected <T> void toSubscribe(Observable<T> o, Observer<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //防手抖
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     * <p>
     * Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    public class HttpResultFunc<T> implements Function<BaseHttpResult, T> {
        @Override
        public T apply(BaseHttpResult baseHttpResult){
            //TODO 这里可以根据需要增加验签
//            if (!checkSign(baseHttpResult)) {
//                return "";
//            }
            String code = baseHttpResult.code;
            if (!code.equals(Constants.OK)) {
                //异常处理
                throw new ResultException(baseHttpResult);
            }
            return (T) baseHttpResult.data;
        }
    }
}
