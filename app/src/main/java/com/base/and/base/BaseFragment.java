package com.base.and.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * fragment基类
 * Created by Makise on 2017/2/4.
 */

public abstract class BaseFragment<T> extends Fragment {

    public BaseActivity activity;
    protected T binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (BaseActivity) getActivity();
        View view = initBinding(inflater, container);
        initData();
        return view;
    }

    /**
     * 初始化databinding
     *
     * @return
     */
    protected abstract View initBinding(LayoutInflater inflater, ViewGroup container);

    /**
     * 数据填充
     */
    protected abstract void initData();
}
