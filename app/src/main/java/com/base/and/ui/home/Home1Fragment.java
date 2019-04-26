package com.base.and.ui.home;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.and.R;
import com.base.and.base.BaseFragment;
import com.base.and.databinding.FragmentHome1Binding;

public class Home1Fragment extends BaseFragment<FragmentHome1Binding> {
    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_1, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initData() {

    }
}
