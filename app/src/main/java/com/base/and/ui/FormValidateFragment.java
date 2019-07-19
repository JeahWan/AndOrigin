package com.base.and.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.base.and.R;
import com.base.and.base.BaseFragment;
import com.base.and.databinding.FragmentFormValidateBinding;
import com.base.and.utils.Validate;

public class FormValidateFragment extends BaseFragment<FragmentFormValidateBinding> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_form_validate;
    }

    @Override
    public void initData() {
        binding.tvValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate.build()
                        .add(binding.etName, "姓名不能为空", new Validate.ValidateItem.ValidateListener("姓名输入错误") {
                            @Override
                            public boolean validate(String inputContent) {
                                return inputContent.contains("11");
                            }
                        })
                        .add(binding.etPhone, "手机号不能为空", new Validate.ValidateItem.ValidateListener("手机号输入错误") {
                            @Override
                            public boolean validate(String inputContent) {
                                return inputContent.length() == 11;
                            }
                        })
                        .execValidate(new Validate.ValidateResultListener() {
                            @Override
                            public void onPass() {
                                Toast.makeText(getContext(), "校验通过", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(EditText editText) {

                            }
                        });
            }
        });
    }
}
