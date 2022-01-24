package com.jeahwan.origin.ui

import android.view.View
import com.jeahwan.origin.R
import com.jeahwan.origin.base.BaseFragment
import com.jeahwan.origin.databinding.FragmentHomeBinding
import com.jeahwan.origin.utils.expandfun.NavKt.startContainerActivity

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home),
    View.OnClickListener {
    override fun initData() {
        binding.tvTab.setOnClickListener(this)
        binding.tvForm.setOnClickListener(this)
        binding.tvDialog.setOnClickListener(this)
        binding.tvCrash.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_tab -> startContainerActivity(TabDemoFragment::class.java.canonicalName)
            R.id.tv_form -> startContainerActivity(FormValidateFragment::class.java.canonicalName)
//            R.id.tv_dialog -> BaseDialog<T>(
//                activity,
//                R.layout.dialog_tips,
//                object : onCreateView<DialogTipsBinding?>(), BaseDialog.OnCreateView<Any> {
//
//                    override fun createView(dialogBinding: Any, dialog: Dialog) {
//                        binding.tvDesc.setText("这里是弹窗提示内容")
//                    }
//                }).setGravity(Gravity.CENTER).setWidth((getScreenWidth(context) * 0.75).toInt())
//                .show()
            R.id.tv_crash -> {
                var i = 5 / 0
            }
        }
    }
}