package com.jeahwan.origin.ui

import androidx.fragment.app.Fragment
import com.jeahwan.origin.base.BaseTabFragment
import com.jeahwan.origin.ui.MineFragment

open class TabDemoFragment : BaseTabFragment() {
    override val title: String
        protected get() = "tab页面"

    override fun getTabNames(): Array<String> {
        return arrayOf("测试1", "测试2", "测试3")
    }

    override fun getTabItem(position: Int): Fragment {
        return instantiate(context!!, MineFragment::class.java.name)
    }
}