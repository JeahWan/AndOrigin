package com.base.and.http;

import com.base.and.Constants;
import com.base.and.base.BaseHttpResult;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 网络请求的契约类
 * Created by Makise on 2017/2/4.
 */

public interface HttpContract {
    interface Services {
        @FormUrlEncoded
        @POST(Constants.TEST_URL)
        Observable<BaseHttpResult> UserInfo(
                @FieldMap Map<String, String> param,
                @Field("type") String type
        );
    }

    interface Methods {
        void getUserInfo(String type, Observer subscriber);
    }
}
