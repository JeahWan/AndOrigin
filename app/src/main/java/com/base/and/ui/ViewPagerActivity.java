package com.base.and.ui;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.base.and.Constants;
import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.databinding.ActivityViewPagerBinding;
import com.base.and.view.ViewPagerWithNoScroll;

import java.util.ArrayList;
import java.util.List;

/**
 * viewpagerAct 适用于常规流程页面
 * Created by Makise on 2017/2/6.
 */

public class ViewPagerActivity extends BaseActivity<ActivityViewPagerBinding> {
    public ViewPagerWithNoScroll viewPager;
    private List<Fragment> fragmentList;
    //区分要载入的页面
    private String type;

    @Override
    protected View initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_pager);
        return binding.getRoot();
    }

    @Override
    protected void initData() {
        type = getIntent().getStringExtra(Constants.VP_ACT_TYPE);
        fragmentList = new ArrayList<>();
        viewPager = binding.viewPager;

        //add fragment
        switch (type) {
            case "":
        }

        //setAdapter
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        //设置不同页面对返回键的处理
        super.onBackPressed();
    }
}
