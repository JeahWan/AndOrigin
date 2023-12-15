package com.jeahwan.origin.base

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.jeahwan.origin.R
import com.jeahwan.origin.databinding.FragmentBaseTabBinding
import com.jeahwan.origin.utils.DensityUtil
import com.jeahwan.origin.view.ScaleTransitionPagerTitleView
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

abstract class BaseTabFragment : BaseFragment<FragmentBaseTabBinding>(R.layout.fragment_base_tab) {

    protected lateinit var mTabsName: Array<String>

    override fun initData() {
        if (TextUtils.isEmpty(title)) {
            binding.titleLayout.rlRoot.visibility = View.GONE
        } else {
            binding.titleLayout.title = title
        }
        mTabsName = getTabNames()
        binding.viewDivider.visibility = if (showTabDivider()) View.VISIBLE else View.GONE
        binding.viewPager.adapter = object :
            FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return getTabItem(position)
            }

            override fun getCount(): Int {
                return mTabsName.size
            }

            override fun getPageTitle(position: Int): CharSequence {
                return mTabsName[position]
            }
        }
        binding.viewPager.offscreenPageLimit = mTabsName.size
        if (!isAdjustMode) {
            val param = binding.tabs.layoutParams
            param.width = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.tabs.layoutParams = param
        }
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = isAdjustMode
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mTabsName.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                return getTabTitleView(context, index)
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                return getTabIndicator(context)
            }

            override fun getTitleWeight(context: Context, index: Int): Float {
                return getTabWeight(index)
            }
        }
        binding.tabs.navigator = commonNavigator
        //        LinearLayout titleContainer = commonNavigator.getTitleContainer();
//        titleContainer.setDividerPadding(200);
        ViewPagerHelper.bind(binding.tabs, binding.viewPager)
    }

    /**
     * tab的宽度权重 默认平分
     *
     * @param index
     * @return
     */
    protected fun getTabWeight(index: Int): Float {
        return 1f
    }

    /**
     * titlebar标题 传空即不显示
     *
     * @return
     */
    protected abstract val title: String?

    /**
     * tab标题数组
     *
     * @return
     */
    protected abstract fun getTabNames(): Array<String>

    /**
     * tab对应的fragment
     *
     * @param position
     * @return
     */
    protected abstract fun getTabItem(position: Int): Fragment
    protected open var isAdjustMode: Boolean = true

    private fun showTabDivider(): Boolean {
        return false
    }

    /**
     * 调用此方法设置分割线样式
     *
     * @param height
     * @param color
     */
    protected fun setDivider(height: Float, color: Int) {
        val params = binding.viewDivider.layoutParams as LinearLayout.LayoutParams
        params.height = DensityUtil.dp2px(height)
        binding.viewDivider.layoutParams = params
        binding.viewDivider.setBackgroundColor(color)
        binding.viewDivider.visibility = View.VISIBLE
    }

    /**
     * 可以复用此方法自定义tab的titleview样式
     *
     * @param context
     * @param index
     * @return
     */
    protected open fun getTabTitleView(context: Context, index: Int): IPagerTitleView {
        val simplePagerTitleView: SimplePagerTitleView = ScaleTransitionPagerTitleView(context)
        simplePagerTitleView.text = mTabsName[index]
        simplePagerTitleView.textSize = 16f
        simplePagerTitleView.normalColor = -0x99999a
        simplePagerTitleView.selectedColor = -0xcdcdce
        simplePagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
        return simplePagerTitleView
    }

    /**
     * 下划线样式
     *
     * @param context
     * @return
     */
    protected open fun getTabIndicator(context: Context): IPagerIndicator {
        val indicator = LinePagerIndicator(context)
        indicator.mode = LinePagerIndicator.MODE_EXACTLY
        indicator.lineHeight = DensityUtil.dp2px(3f).toFloat()
        indicator.lineWidth = DensityUtil.dp2px(54f).toFloat()
        indicator.roundRadius = DensityUtil.dp2px(2f).toFloat()
        indicator.startInterpolator = AccelerateInterpolator()
        indicator.endInterpolator = DecelerateInterpolator(2.0f)
        indicator.setColors(-0x13e2dc)
        return indicator
    }
}