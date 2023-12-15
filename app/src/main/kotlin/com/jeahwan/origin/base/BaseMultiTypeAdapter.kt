package com.jeahwan.origin.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.jeahwan.origin.App
import com.jeahwan.origin.base.BaseMultiTypeAdapter.ViewTypeDelegate
import java.util.LinkedList

/**
 * 多类型条目的adapter 需要用的地方直接new一个即可 不用单独创建类
 * 将每个类型的条目业务逻辑抽离至[ViewTypeDelegate]类中
 *
 * 使用方法：创建[BaseMultiTypeAdapter]对象，并调用[addItemViewTypeDelegate]添加所有的条目类型，如果单类型条目只需add一次即可
 */
class BaseMultiTypeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listDelegate = LinkedList<ViewTypeDelegate<*, *>>()
    private val mapDelegate = mutableMapOf<Int, ViewTypeDelegate<*, *>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewTypeDelegate = mapDelegate[viewType]
        return viewTypeDelegate!!.onCreateViewHolderDelegate(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewTypeDelegate = mapDelegate[getItemViewType(position)]
        var indexOf = listDelegate.indexOf(viewTypeDelegate)
        var frontSize = 0
        while (indexOf > 0) {
            frontSize += listDelegate[--indexOf].getDataSize()
        }
        val realPosition = if (position >= frontSize) position - frontSize else position
        viewTypeDelegate!!.onBindViewHolderDelegate(holder, realPosition)
    }

    override fun getItemViewType(position: Int): Int {
        var index = 0
        for (element in listDelegate) {
            index += element.getDataSize()
            if (position < index) {
                val viewType = element.getItemViewTypeDelegate()
                if (!mapDelegate.containsKey(viewType)) {
                    mapDelegate[viewType] = element
                }
                return viewType
            }
        }
        return position
    }

    override fun getItemCount(): Int {
        var index = 0
        for (element in listDelegate) {
            index += element.getDataSize()
        }
        return index
    }

    /**
     * 添加多种类型的条目代理
     */
    fun addItemViewTypeDelegate(viewTypeDelegate: ViewTypeDelegate<*, *>) {
        viewTypeDelegate.attachWithAdapter(this)
        listDelegate.add(viewTypeDelegate)
        notifyDataSetChanged()
    }

    /**
     * 设置单一类型条目代理
     */
    fun setItemViewTypeDelegate(viewTypeDelegate: ViewTypeDelegate<*, *>) {
        listDelegate.clear()
        addItemViewTypeDelegate(viewTypeDelegate)
    }

    abstract class ViewTypeDelegate<Data, DB : ViewDataBinding>(
        var context: Context?,
        @LayoutRes layout: Int
    ) {

        private val mLayout: Int
        private var data: MutableList<Data>
        private var baseMultiTypeAdapter: BaseMultiTypeAdapter? = null

        fun onCreateViewHolderDelegate(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(context)
            val binding: DB = DataBindingUtil.inflate(inflater, mLayout, parent, false)
            return BaseAdapter.BaseViewHolder(binding.root)
        }

        /**
         * +hash是为了保证[mapDelegate]的key不重复
         */
        fun getItemViewTypeDelegate(): Int {
            return mLayout + hashCode()
        }

        @Suppress("UNCHECKED_CAST")
        fun onBindViewHolderDelegate(holder: RecyclerView.ViewHolder, position: Int) {
            val binding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView) as DB
            onBindViewHolderDelegateCallback(data[position], binding, position)
        }

        fun getDataSize(): Int {
            return data.size
        }

        /**
         * 数据刷新
         * 可变参数[viewTypeDelegate]作用是将与自己冲突的viewType数量置空
         * eg.空布局view刷新时必须将原有的正常列表清空（空viewType.refresh(list, 正常ViewTypeDelegate)）
         */
        fun refresh(list: MutableList<Data>, vararg viewTypeDelegate: ViewTypeDelegate<*, *>) {
            if (viewTypeDelegate.isNotEmpty()) {
                for (delegate in viewTypeDelegate) {
                    delegate.clearList()
                }
            }
            this.data = list
            baseMultiTypeAdapter?.notifyDataSetChanged()
        }

        fun addList(list: MutableList<Data>) {
            this.data.addAll(list)
            baseMultiTypeAdapter?.notifyDataSetChanged()
        }

        fun clearList() {
            this.data.clear()
        }

        fun attachWithAdapter(adapter: BaseMultiTypeAdapter) {
            baseMultiTypeAdapter = adapter
        }

        abstract fun onBindViewHolderDelegateCallback(data: Data, binding: DB, position: Int)

        abstract fun onBindViewHolderDelegateForTrackEventCallback(
            data: Data,
            binding: DB,
            position: Int
        )

        init {
            if (context == null) {
                context = App.instance
            }
            mLayout = layout
            data = mutableListOf()
        }
    }
}