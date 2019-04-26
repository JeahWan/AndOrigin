package com.base.and.base;

import com.base.and.BuildConfig;
import com.base.and.Constants;
import com.base.and.http.HttpContract;
import com.base.and.http.ResultException;
import com.base.and.utils.Tools;

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
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
        if (BuildConfig.DEBUG)
            httpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

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
                .addConverterFactory(GsonConverterFactory.create())
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
    public class HttpResultFunc implements Function<BaseHttpResult, String> {
        @Override
        public String apply(BaseHttpResult baseHttpResult) throws Exception {
            //TODO 这里可以根据需要增加验签
//            if (!checkSign(baseHttpResult)) {
//                return "";
//            }
            String code = baseHttpResult.code;
            if (!code.equals(Constants.OK)) {
                throw new ResultException(baseHttpResult);
            }
            return baseHttpResult.data;
        }
    }
}
