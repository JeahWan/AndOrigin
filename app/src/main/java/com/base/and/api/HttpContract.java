package com.base.and.api;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 网络请求的契约类
 * Created by Makise on 2017/2/4.
 */

public interface HttpContract {
    interface Services {
        /**
         * 测试网络请求
         *
         * @param param
         * @return
         */
        @FormUrlEncoded
        @POST("satinApi")
        Observable<BaseData<String>> apiDemo(@FieldMap Map<String, Object> param);
    }

    interface Methods {
        void apiDemo(Observer<String> subscriber);
    }
}
