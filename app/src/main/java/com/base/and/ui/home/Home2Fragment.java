package com.base.and.ui.home;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.and.R;
import com.base.and.base.BaseFragment;
import com.base.and.databinding.FragmentHome2Binding;

public class Home2Fragment extends BaseFragment<FragmentHome2Binding> {
    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_2, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initData() {

    }
}
