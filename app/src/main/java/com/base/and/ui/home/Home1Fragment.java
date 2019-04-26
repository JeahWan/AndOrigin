package com.base.and.ui.home;

import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.and.BuildConfig;
import com.base.and.R;
import com.base.and.base.BaseFragment;
import com.base.and.data.Movie;
import com.base.and.databinding.FragmentHome1Binding;
import com.base.and.http.HttpMethods;
import com.base.and.http.subscribers.ProgressSubscriber;

import java.util.concurrent.ConcurrentHashMap;

public class Home1Fragment extends BaseFragment<FragmentHome1Binding> {
    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_1, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initData() {
    }

    /**
     * 接口请求示例
     */
    public void getMovie() {
        HttpMethods.getInstance()
                .params(new ConcurrentHashMap<String, Object>() {
                    {
                        put("start", 0);
                        put("count", 1);
                    }
                })
                .getTopMovie(new ProgressSubscriber<Movie>(false) {
                    @Override
                    public void onNext(Movie movie) {
                        //TODO 获得数据
                        if (BuildConfig.DEBUG) Log.d("HomeActivity", movie.toString());
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getMovie();
    }
}
