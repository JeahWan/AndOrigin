package com.base.and.ui;

import com.base.and.R;
import com.base.and.base.BaseFragment;
import com.base.and.ui.home.Home1Fragment;
import com.base.and.ui.home.Home2Fragment;

/**
 * Created by Makise on 2017/2/7.
 */

public class FragmentFactory {
    private static BaseFragment home1Fragment1 = new Home1Fragment();
    private static BaseFragment home1Fragment2 = new Home2Fragment();

    public static BaseFragment getInstanceByBtnID(int btnId) {
        BaseFragment fragment = null;
        switch (btnId) {
            case R.id.btn_1:
                fragment = home1Fragment1;
                break;
            case R.id.btn_2:
                fragment = home1Fragment2;
                break;

        }
        return fragment;
    }
}
