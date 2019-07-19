package com.base.and.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.WindowManager;

import com.base.and.App;
import com.base.and.R;
import com.base.and.base.BaseFragment;
import com.noober.background.BackgroundLibrary;

import java.lang.ref.WeakReference;


public class ContainerActivity extends FragmentActivity {
    public static final String FRAGMENT = "fragment";
    public static final String BUNDLE = "bundle";
    private static final String FRAGMENT_TAG = "content_fragment_tag";
    protected WeakReference<Fragment> mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BackgroundLibrary.inject(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = fm.getFragment(savedInstanceState, FRAGMENT_TAG);
        }
        if (fragment == null) {
            fragment = initFromIntent(getIntent());
        }
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.content, fragment);
        trans.commitAllowingStateLoss();
        mFragment = new WeakReference<>(fragment);
        App.getInstance().addActivity(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, mFragment.get());
    }

    protected Fragment initFromIntent(Intent data) {
        if (data == null) {
            throw new RuntimeException(
                    "you must provide a page info to display");
        }
        try {
            String fragmentName = data.getStringExtra(FRAGMENT);
            if (fragmentName == null || "".equals(fragmentName)) {
                throw new IllegalArgumentException("can not find page fragmentName");
            }
            Class<?> fragmentClass = Class.forName(fragmentName);
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            Bundle args = data.getBundleExtra(BUNDLE);
            if (args != null) {
                fragment.setArguments(args);
            }
            return fragment;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("fragment initialization failed!");
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment instanceof BaseFragment) {
            if (!((BaseFragment) fragment).isBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().removeActivity(this);
    }
}
