package com.base.and.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.and.R;
import com.base.and.api.ResultException;
import com.base.and.databinding.FragmentRecyclerViewBinding;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListFragment<T extends Object, T2 extends ViewDataBinding> extends BaseFragment<FragmentRecyclerViewBinding> {
    protected List<T> dataList;
    protected int pIndex = 1;
    protected int pageSize = 10;
    private BaseAdapter<T, T2> adapter;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_recycler_view;
    }

    @Override
    public void initData() {
        dataList = new ArrayList<>();
        adapter = new BaseAdapter<>(getContext(), getItemLayout());
        adapter.setOnBindViewHolder(new BaseAdapter.BindView<T2>() {
            @Override
            public void onBindViewHolder(T2 binding, int position) {
                getItemView(binding, position);
            }
        });
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rv.setAdapter(adapter);
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pIndex = 1;
                getData();
            }
        });
        if (enablePaging()) {
            binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    pIndex++;
                    getData();
                }
            });
        }
        if (!TextUtils.isEmpty(getTitle())) {
            binding.titleLayout.rlRoot.setVisibility(View.VISIBLE);
            binding.titleLayout.setTitle(getTitle());
        } else {
            binding.titleLayout.rlRoot.setVisibility(View.GONE);
        }
        getData();
    }

    /**
     * 在接口回调onSuccess中调用
     *
     * @param list 数据
     */
    protected void onSuccessUI(List<T> list) {
        if (binding.refreshLayout != null) {
            binding.refreshLayout.finishRefresh();
            binding.refreshLayout.finishLoadMore();
            binding.refreshLayout.setEnableLoadMore(enablePaging() && (list.size() == pageSize));
        }
        if (pIndex == 1 && list.size() == 0) {
            //没有数据
            binding.tvNoData.setText(getErrorTips());
            Drawable drawable = getResources().getDrawable(getErrorDrawable());
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            binding.tvNoData.setCompoundDrawables(null, drawable, null, null);
            binding.rlNoData.setVisibility(View.VISIBLE);
            binding.rv.setVisibility(View.GONE);
            return;
        }
        binding.rlNoData.setVisibility(View.GONE);
        binding.rv.setVisibility(View.VISIBLE);
        if (pIndex == 1) {
            dataList.clear();
        }
        dataList.addAll(list);
        adapter.initList(dataList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 在接口回调onError中调用
     *
     * @param e onError回调的Throwable
     */
    protected void onErrorUI(Throwable e) {
        if (binding.refreshLayout != null) {
            binding.refreshLayout.finishRefresh();
            binding.refreshLayout.finishLoadMore();
        }
        binding.tvNoData.setText(e instanceof ResultException.NoDataException ? getErrorTips() : "网络信号差，加载失败啦");
        Drawable drawable = getResources().getDrawable(e instanceof ResultException.NoDataException ? getErrorDrawable() : R.drawable.ic_launcher);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        binding.tvNoData.setCompoundDrawables(null, drawable, null, null);
        binding.rlNoData.setVisibility(View.VISIBLE);
        binding.rv.setVisibility(View.GONE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getData();
        }
    }

    /**
     * 默认启用分页
     *
     * @return 子类复写返回false关闭分页
     */
    protected boolean enablePaging() {
        return true;
    }

    protected abstract void getData();

    protected abstract int getItemLayout();

    protected abstract void getItemView(T2 binding, int position);

    protected abstract String getErrorTips();

    protected abstract int getErrorDrawable();

    protected String getTitle() {
        return "";
    }
}
