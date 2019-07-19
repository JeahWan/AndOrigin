package com.base.and.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.and.R;
import com.base.and.api.HttpMethods;
import com.base.and.api.ProgressSubscriber;
import com.base.and.base.BaseFragment;
import com.base.and.databinding.FragmentMineBinding;

import java.util.concurrent.ConcurrentHashMap;

public class MineFragment extends BaseFragment<FragmentMineBinding> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine;
    }

    @Override
    public void initData() {
        binding.tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpMethods.getInstance()
                        .params(new ConcurrentHashMap<String, Object>() {{
                            put("type", "1");
                            put("page", "1");
                        }})
                        .apiDemo(new ProgressSubscriber<String>(true) {
                            @Override
                            public void onSuccess(String jsonStr) {
                                binding.tvResult.setText(jsonStr);
                            }
                        });
            }
        });
    }
}
