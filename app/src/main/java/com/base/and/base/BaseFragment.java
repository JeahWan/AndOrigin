package com.base.and.base;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.base.and.R;
import com.base.and.ui.ContainerActivity;
import com.base.and.ui.WebViewActivity;
import com.base.and.utils.MaterialDialogUtils;
import com.base.and.utils.StatusBarUtil;


public abstract class BaseFragment<V extends ViewDataBinding> extends Fragment {

    protected V binding;
    private MaterialDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStatusBar();
        setGoBack();
        initData();
    }

    /**
     * 设置返回按钮操作 不必子类重复实现
     */
    private void setGoBack() {
        if (binding.getRoot().getRootView().findViewById(R.id.title_layout) != null) {
            if (binding.getRoot().getRootView().findViewById(R.id.title_layout).findViewById(R.id.go_back) != null) {
                binding.getRoot().getRootView().findViewById(R.id.title_layout).findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            }
        }
    }

    /**
     * 沉浸式状态栏设置
     */
    public void setStatusBar() {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(), true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(getActivity());
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(getActivity(), true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(getActivity(), 0x55000000);
        }
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract void initData();

    /**
     * 显示加载框
     *
     * @param title
     */
    public void showDialog(String title) {
        if (dialog != null) {
            dialog.show();
        } else {
            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(getActivity(), title, true);
            dialog = builder.show();
        }
    }

    /**
     * 隐藏加载框
     */
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(getContext(), clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转到webview
     *
     * @param url
     */
    public void startWebActivity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(WebViewActivity.URL, url);
        startActivity(WebViewActivity.class, bundle);
    }

    /**
     * 配置是否可返回
     *
     * @return
     */
    public boolean isBackPressed() {
        return false;
    }
}
