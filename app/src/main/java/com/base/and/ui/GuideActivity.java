package com.base.and.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.base.and.R;
import com.base.and.base.BaseActivity;
import com.base.and.databinding.ActivityGuideBinding;
import com.base.and.ui.home.HomeActivity;
import com.base.and.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 */
public class GuideActivity extends BaseActivity<ActivityGuideBinding> {
    private List<View> viewList; // Tab页面列表
    private int[] imageIds;

    @Override
    protected View initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide);
        return binding.getRoot();
    }

    @Override
    protected void initData() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewList = new ArrayList<>();
        //TODO 引导页图片集合
//        imageIds = new int[]{
//                R.drawable.help_1,
//                R.drawable.help_2,
//                R.drawable.help_3,
//                R.drawable.help_4
//        };

        for (int i = 0; i < imageIds.length; i++) {
            View view = View.inflate(this, R.layout.item_guide, null);
            ImageView im = (ImageView) view.findViewById(R.id.image_bg);
            im.setImageResource(imageIds[i]);
            viewList.add(view);
            if ((imageIds.length - 1) == i) {
                Button btn = (Button) view.findViewById(R.id.btn);
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ViewUtils.isFastDoubleClick(v)) return;
                        startNewActivity(new Intent(GuideActivity.this, HomeActivity.class), true);
                    }
                });
            }
        }
        binding.viewpager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        });
    }
}