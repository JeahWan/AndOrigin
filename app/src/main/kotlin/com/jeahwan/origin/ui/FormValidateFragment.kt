package com.jeahwan.origin.ui

import android.widget.EditText
import android.widget.Toast
import com.jeahwan.origin.R
import com.jeahwan.origin.base.BaseFragment
import com.jeahwan.origin.databinding.FragmentFormValidateBinding
import com.jeahwan.origin.utils.Validate
import com.jeahwan.origin.utils.Validate.ValidateItem.ValidateListener
import com.jeahwan.origin.utils.Validate.ValidateResultListener

class FormValidateFragment  : BaseFragment<FragmentFormValidateBinding>(R.layout.fragment_form_validate){
    override fun initData() {
        binding.tvValidate.setOnClickListener {
            Validate.build()
                .add(binding.etName, "姓名不能为空", object : ValidateListener("姓名输入错误") {
                    override fun validate(inputContent: String): Boolean {
                        return inputContent.contains("11")
                    }
                })
                .add(binding.etPhone, "手机号不能为空", object : ValidateListener("手机号输入错误") {
                    override fun validate(inputContent: String): Boolean {
                        return inputContent.length == 11
                    }
                })
                .execValidate(object : ValidateResultListener {
                    override fun onPass() {
                        Toast.makeText(context, "校验通过", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFail(editText: EditText) {}
                })
        }
    }
}