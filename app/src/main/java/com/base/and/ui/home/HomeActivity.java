package com.base.and.ui.home;

import android.annotation.TargetApi;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.base.BaseFragment;
import com.base.and.data.HomeTabMessage;
import com.base.and.databinding.ActivityHomeBinding;
import com.base.and.ui.FragmentFactory;
import com.base.and.utils.LogUtil;
import com.base.and.utils.rxbus.RxBus;
import com.base.and.utils.rxbus.RxSubscriptions;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * 首页
 */
public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    //当前显示的fragment btnId
    private static int currentBtn = 0;
    public FragmentManager mainFragmentManager;
    private Disposable mRxSubSticky;

    @Override
    protected View initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.bottomMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeFragment(checkedId, false);
            }
        });
        return binding.getRoot();
    }

    @Override
    protected void initData() {
        if (mainFragmentManager == null) {
            mainFragmentManager = getSupportFragmentManager();
        }
        //Rxbus 实现首页标签的切换
        mRxSubSticky = RxBus.getDefault()
                .toObservableSticky(HomeTabMessage.class)
                .subscribe(new Consumer<HomeTabMessage>() {
                    @Override
                    public void accept(@NonNull HomeTabMessage homeTabMessage) throws Exception {
                        LogUtil.info(HomeActivity.class, homeTabMessage.witchTab + "");
                        //传入R.id.licai anquan wode
                        binding.bottomMenu.check(homeTabMessage.witchTab);
                    }
                });
        RxSubscriptions.add(mRxSubSticky);
    }

    /**
     * 切换Fragment
     *
     * @param fragmentId
     * @param isAddToBackStackAndShowBack
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void changeFragment(int fragmentId, boolean isAddToBackStackAndShowBack) {
        BaseFragment toFragment = FragmentFactory.getInstanceByBtnID(fragmentId);
        if (toFragment == null) {
            return;
        }
        BaseFragment fromFragment = FragmentFactory.getInstanceByBtnID(currentBtn);
        String toTag = toFragment.getClass().getSimpleName();

        FragmentTransaction ft = mainFragmentManager.beginTransaction();
        /**
         * 如果要切换到的Fragment没有被Fragment事务添加，则隐藏被切换的Fragment，添加要切换的Fragment
         * 否则，则隐藏被切换的Fragment，显示要切换的Fragment
         *
         */
        if (!toFragment.isAdded() && toFragment.getTag() == null) {
            ft.add(R.id.main_fragment, toFragment, toTag);
        }
        if (fromFragment != null && fromFragment.isAdded()) {
            ft.hide(fromFragment);
        }
        ft.show(toFragment);
        currentBtn = fragmentId;
        if (isAddToBackStackAndShowBack) {
            ft.addToBackStack(null);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int toFragment = binding.bottomMenu.getCheckedRadioButtonId();
        int tmp = getIntent().getIntExtra("BUTTON", toFragment);
        if (tmp > 0 && tmp != toFragment) {//需要更换当前显示页面
            ((RadioButton) (binding.bottomMenu.findViewById(tmp))).setChecked(true);
            getIntent().removeExtra("BUTTON");
        } else {
            changeFragment(toFragment, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(mRxSubSticky);
    }
}
