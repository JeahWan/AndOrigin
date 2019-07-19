package com.base.and.base;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 基于databinding封装的通用adapter
 *
 * 需要用的地方直接new一个 调initList、setOnBindViewHolder即可 不用单独创建类
 *
 * @param <T1> item对应的bean
 * @param <T2> item对应的databinding
 */
public class BaseAdapter<T1, T2 extends ViewDataBinding> extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {
    private List<T1> list;
    private LayoutInflater inflater;
    @LayoutRes
    private int layout;
    private BindView<T2> bindView;

    public BaseAdapter(Context context, @LayoutRes int layout) {
        if (context == null) return;
        inflater = LayoutInflater.from(context);
        this.layout = layout;
        list = new ArrayList<>();
    }

    public void initList(List<T1> list) {
        this.list = list;
    }

    public void setOnBindViewHolder(BindView<T2> bindView) {
        this.bindView = bindView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layout, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        bindView.onBindViewHolder((T2) holder.getBinding(), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface BindView<T2> {
        void onBindViewHolder(T2 binding, int position);
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding b;

        public BaseViewHolder(View itemView) {
            super(itemView);
            b = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return b;
        }
    }
}
