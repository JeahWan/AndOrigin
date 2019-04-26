package com.base.and.http;

import com.base.and.Constants;
import com.base.and.base.BaseHttpResult;
import com.base.and.data.Movie;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 网络请求的契约类
 * Created by Makise on 2017/2/4.
 */

public interface HttpContract {
    interface Services {
        /**
         * 获取豆瓣top250电影
         *
         * @param param
         * @return
         */
        @GET(Constants.TOP_MOVIE)
        Observable<BaseHttpResult<Movie>> topMovie(@QueryMap Map<String, Object> param);
    }

    interface Methods {
        void getTopMovie(Observer subscriber);
    }
}
