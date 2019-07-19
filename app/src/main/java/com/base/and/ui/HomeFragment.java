package com.base.and.ui;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.and.R;
import com.base.and.base.BaseDialog;
import com.base.and.base.BaseFragment;
import com.base.and.databinding.DialogTipsBinding;
import com.base.and.databinding.FragmentHomeBinding;
import com.base.and.utils.DensityUtil;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements View.OnClickListener {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public void initData() {
        binding.tvTab.setOnClickListener(this);
        binding.tvForm.setOnClickListener(this);
        binding.tvDialog.setOnClickListener(this);
        binding.tvCrash.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tab:
                startContainerActivity(TabDemoFragment.class.getCanonicalName());
                break;
            case R.id.tv_form:
                startContainerActivity(FormValidateFragment.class.getCanonicalName());
                break;
            case R.id.tv_dialog:
                new BaseDialog<>(getActivity(), R.layout.dialog_tips, new BaseDialog.onCreateView<DialogTipsBinding>() {
                    @Override
                    public void createView(DialogTipsBinding binding, Dialog dialog) {
                        binding.tvDesc.setText("这里是弹窗提示内容");
                    }
                }).setGravity(Gravity.CENTER).setWidth((int) (DensityUtil.getScreenWidth(getContext()) * 0.75)).show();
                break;
            case R.id.tv_crash:
                int i = 5 / 0;
                break;
        }
    }
}
