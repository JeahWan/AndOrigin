package com.base.and.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.WindowManager;

import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.databinding.ActivityMainBinding;
import com.base.and.utils.StatusBarUtil;
import com.base.and.utils.ToastUtils;
import com.base.and.view.TabItemView;

import java.util.ArrayList;
import java.util.List;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    public static MainActivity instance;
    private List<Fragment> mFragments;
    private NavigationController navigationController;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        instance = this;
        //初始化Fragment
        initFragment();
        //初始化底部Button
        initBottomTab();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new MineFragment());
        //默认选中第一个
        showFragment(mFragments.get(0));
    }

    private void initBottomTab() {
        navigationController = binding.pagerBottomTab.custom()
                .addItem(newItem(R.drawable.icon_tab_1, R.drawable.icon_tab_1_checked, "首页"))
                .addItem(newItem(R.drawable.icon_tab_2, R.drawable.icon_tab_2_checked, "我的"))
//                .setDefaultColor(ContextCompat.getColor(this, R.color.black))
                .build();
        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                StatusBarUtil.setStatusBarDarkTheme(MainActivity.this, index != 3);
                StatusBarUtil.setStatusBarColor(MainActivity.this, index == 3 ? 0xffff2209 : 0xffffffff);
                showFragment(mFragments.get(index));
            }

            @Override
            public void onRepeat(int index) {
            }
        });
    }

    private void showFragment(Fragment fragment) {
        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment).commit();
        }
        for (Fragment frag : mFragments) {
            if (frag != fragment) {
                /*先隐藏其他fragment*/
                getSupportFragmentManager().beginTransaction().hide(frag).commit();
            }
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    @Override
    public void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setStatusBarColor(MainActivity.this, 0xffffffff);
    }

    //创建一个Item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        TabItemView tab = new TabItemView(this);
        tab.initialize(drawable, checkedDrawable, text);
        tab.setTextDefaultColor(0xFF333333);
        tab.setTextCheckedColor(0xFF1296DB);
        return tab;
    }

    @Override
    public void onBackPressed() {
        if (navigationController.getSelected() != 0) {
            navigationController.setSelect(0);
        } else if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showShort(this, "再次按返回键退出");
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }
}