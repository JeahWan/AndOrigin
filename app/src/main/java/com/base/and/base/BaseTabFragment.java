package com.base.and.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.base.and.R;
import com.base.and.databinding.FragmentBaseTabBinding;
import com.base.and.utils.DensityUtil;
import com.base.and.view.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public abstract class BaseTabFragment extends BaseFragment<FragmentBaseTabBinding> {
    protected String[] mTabsName;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_base_tab;
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(getTitle())) {
            binding.titleLayout.rlRoot.setVisibility(View.GONE);
        } else {
            binding.titleLayout.title.setText(getTitle());
        }
        mTabsName = getTabNames();
        binding.viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return getTabItem(position);
            }

            @Override
            public int getCount() {
                return mTabsName.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabsName[position];
            }
        });
        binding.viewPager.setOffscreenPageLimit(mTabsName.length);

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTabsName.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                return getTabTitleView(context, index);
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(DensityUtil.dp2px(context, 3));
                indicator.setLineWidth(DensityUtil.dp2px(context, 54));
                indicator.setRoundRadius(DensityUtil.dp2px(context, 2));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(0xffEC1D24);
                return indicator;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return getTabWeight(index);
            }
        });
        binding.tabs.setNavigator(commonNavigator);
//        LinearLayout titleContainer = commonNavigator.getTitleContainer();
//        titleContainer.setDividerPadding(200);
        ViewPagerHelper.bind(binding.tabs, binding.viewPager);
    }

    /**
     * tab的宽度权重 默认平分
     *
     * @param index
     * @return
     */
    protected float getTabWeight(int index) {
        return 1;
    }

    /**
     * titlebar标题 传空即不显示
     *
     * @return
     */
    protected abstract String getTitle();

    /**
     * tab标题数组
     *
     * @return
     */
    protected abstract String[] getTabNames();

    /**
     * tab对应的fragment
     *
     * @param position
     * @return
     */
    protected abstract Fragment getTabItem(int position);

    /**
     * 调用此方法设置分割线样式
     *
     * @param height
     * @param color
     */
    protected void setDivider(int height, int color) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.viewDivider.getLayoutParams();
        params.height = DensityUtil.dp2px(getContext(), height);
        binding.viewDivider.setLayoutParams(params);
        binding.viewDivider.setBackgroundColor(color);
        binding.viewDivider.setVisibility(View.VISIBLE);
    }

    /**
     * 可以复用此方法自定义tab的titleview样式
     *
     * @param context
     * @param index
     * @return
     */
    protected IPagerTitleView getTabTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setText(mTabsName[index]);
        simplePagerTitleView.setTextSize(16);
        simplePagerTitleView.setNormalColor(0xff666666);
        simplePagerTitleView.setSelectedColor(0xff323232);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewPager.setCurrentItem(index);
            }
        });
        return simplePagerTitleView;
    }
}
