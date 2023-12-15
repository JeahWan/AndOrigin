package com.jeahwan.origin.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.jeahwan.origin.App
import com.jeahwan.origin.R
import com.jeahwan.origin.base.BaseActivity
import com.jeahwan.origin.base.BaseFragment
import com.jeahwan.origin.databinding.ActivityMainBinding
import com.jeahwan.origin.utils.*
import com.jeahwan.origin.utils.expandfun.ToastKt.toastShort
import com.jeahwan.origin.view.TabItemView
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var navigationController: NavigationController
    private var exitTime: Long = 0
    private lateinit var tabList: ArrayList<TabEntry>
    private var mNavigationBarShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
    }

    override fun initData() {
        //初始化底部Button
        initTab()
    }

    private fun initTab() {
        tabList = object : ArrayList<TabEntry>() {
            //增减、调整顺序只需要调整这里一处 所有相关逻辑都是自适应的
            init {
                add(
                    TabEntry(
                        TabItemView(this@MainActivity).apply {
                            initialize(
                                HOME,
                                R.drawable.icon_tab_1,
                                R.drawable.icon_tab_1_checked,
                                false
                            )
                        }, HomeFragment(), TabEnum.HOME
                    )
                )
                add(
                    TabEntry(
                        TabItemView(this@MainActivity).apply {
                            initialize(
                                MINE,
                                R.drawable.icon_tab_2,
                                R.drawable.icon_tab_2_checked,
                                false
                            )
                        }, MineFragment(), TabEnum.MINE
                    )
                )
            }
        }

        //默认选中第一个
        showFragment(tabList[0].fragment)
        val tabBuilder = binding.pagerBottomTab.custom().apply {
            tabList.forEach { addItem(it.tab) }
        }
        navigationController = tabBuilder.build().apply {
            //底部按钮的点击事件监听
            addTabItemSelectedListener(object : OnTabItemSelectedListener {
                override fun onSelected(index: Int, old: Int) {
                    showFragment(tabList[index].fragment)
                }

                override fun onRepeat(index: Int) {
                    (tabList[index].fragment as BaseFragment<*>).refreshCurrentItem()
                }
            })
        }
    }

    private fun showFragment(fragment: Fragment) {
        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded && null == supportFragmentManager.findFragmentByTag(fragment.javaClass.canonicalName)) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayout, fragment, fragment.javaClass.canonicalName)
                .commitAllowingStateLoss()
        }
        for (tabEntry in tabList) {
            if (tabEntry.fragment !== fragment) {
                /*先隐藏其他fragment*/
                supportFragmentManager.beginTransaction().hide(tabEntry.fragment)
                    .commitAllowingStateLoss()
            }
        }
        supportFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (onFragmentBackPressed()) {
            return
        }
        when {
            navigationController.selected != 0 -> {
                navigationController.setSelect(0)
            }
            System.currentTimeMillis() - exitTime > 2000 -> {
                toastShort("再次按返回键退出")
                exitTime = System.currentTimeMillis()
            }
            else -> App.instance.exit()
        }
    }

    /**
     * 处理back
     *
     * @return 是否已经处理
     */
    private fun onFragmentBackPressed(): Boolean {
        val currentFragment = tabList[navigationController.selected].fragment
        return (currentFragment is BaseFragment<*>
                && currentFragment.onFragmentBackPressed())
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState);
    }

    private fun getTabEntry(tabEnum: TabEnum) = tabList.singleOrNull { it.tabEnum == tabEnum }

    /**
     * 切换底部tab
     */
    fun setSelectTab(tabEnum: TabEnum, circlePath: String = "1") {
        for (i in tabList.indices) {
            if (tabEnum == tabList[i].tabEnum) {
                navigationController.setSelect(i)
                break
            }
        }
    }

    val tabHeight by lazy {
        binding.pagerBottomTab.measuredHeight
    }

    enum class TabEnum {
        HOME, MINE
    }

    class TabEntry(val tab: TabItemView, val fragment: Fragment, val tabEnum: TabEnum)
    companion object {
        const val TAB_ENUM = "TAB_ENUM"

        const val HOME = "首页"
        const val MINE = "我的"
    }
}