package com.base.and.ui;

import androidx.fragment.app.Fragment;

import com.base.and.base.BaseTabFragment;

public class TabDemoFragment extends BaseTabFragment {
    @Override
    protected String getTitle() {
        return "tab页面";
    }

    @Override
    protected String[] getTabNames() {
        return new String[]{"测试1", "测试2", "测试3"};
    }

    @Override
    protected Fragment getTabItem(int position) {
        return Fragment.instantiate(getContext(), MineFragment.class.getName());
    }
}
