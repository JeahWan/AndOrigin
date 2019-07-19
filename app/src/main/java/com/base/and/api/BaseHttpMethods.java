package com.base.and.api;


import com.alibaba.fastjson.JSONObject;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.base.and.App;
import com.base.and.BuildConfig;
import com.base.and.Constants;
import com.base.and.utils.AntiUtils;
import com.base.and.utils.DeviceUtils;
import com.base.and.utils.HttpsUtils;
import com.base.and.utils.SignUtils;
import com.base.and.utils.VersionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //debug包打印log
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(BuildConfig.DEBUG) //是否开启日志打印
                        .setLevel(Level.BODY) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
//                        .addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                        .build())
                //设置统一的User-Agent
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //添加公共参数
                        Map<String, Object> params = addParams(chain.request());
                        //添加headers
                        String time = String.format("%010d", System.currentTimeMillis() / 1000);
                        Request.Builder builder = chain.request().newBuilder()
                                .addHeader("token", App.getInstance().getUser().token)
                                .addHeader("timestamp", time)
                                .addHeader("osName", "android")
                                .addHeader("content-type", "application/json; charset=utf-8")
                                .addHeader("signature", SignUtils.getSign(params, time));
                        //设置MediaType
                        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), JSONObject.toJSON(params).toString());
                        switch (chain.request().method()) {
                            case "GET":
                                builder.get();
                                break;
                            case "POST":
                                builder.post(requestBody);
                                break;
                            case "PUT":
                                builder.put(requestBody);
                                break;
                        }
                        return chain.proceed(builder.build());
                    }
                })
                .sslSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory, HttpsUtils.getSslSocketFactory().trustManager)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                .build();

        mService = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constants.Url.getHost())
                .build()
                .create(HttpContract.Services.class);
    }

    /**
     * 添加公共参数
     *
     * @param request
     * @return
     */
    private Map<String, Object> addParams(Request request) {
        Map<String, Object> params = new HashMap<>();
        switch (request.method()) {
            case "GET":
                HttpUrl requestUrl = request.url();
                for (int i = 0; i < requestUrl.querySize(); i++) {
                    params.put(requestUrl.queryParameterName(i), requestUrl.queryParameterValue(i));
                }
                break;
            case "POST":
            case "PUT":
                FormBody formBody = (FormBody) request.body();
                for (int i = 0; i < formBody.size(); i++) {
                    try {
                        params.put(formBody.encodedName(i), URLDecoder.decode(formBody.encodedValue(i), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        params.put("deviceId", DeviceUtils.getAndroidID());
        params.put("deviceModel", DeviceUtils.getModel());
        params.put("osName", "android");
        params.put("osVersion", DeviceUtils.getSDKVersionCode() + "");
        params.put("appChannel", VersionUtils.getChannel(App.getInstance(), "UMENG_CHANNEL"));
        params.put("appVersion", VersionUtils.getVersionName(App.getInstance()));
        params.put("blueTooth", AntiUtils.notHasBlueTooth());
        params.put("lightSensor", AntiUtils.notHasLightSensorManager(App.getInstance()));
        return params;
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
    public class HttpResultFunc implements Function<BaseData, Object> {
        @Override
        public Object apply(BaseData baseHttpResult) {
            int code = baseHttpResult.code;
            if (code != Constants.Code.OK || baseHttpResult.data == null) {
                //异常处理
                throw new ResultException(baseHttpResult);
            }
            return baseHttpResult.data;
        }
    }
}
