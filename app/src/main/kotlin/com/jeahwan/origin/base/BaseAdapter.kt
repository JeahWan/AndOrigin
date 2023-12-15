package com.jeahwan.origin.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.jeahwan.origin.App

/**
 * 基于databinding封装的通用adapter
 *
 *
 * 需要用的地方直接new一个 调initList、setOnBindViewHolder即可 不用单独创建类
 *
 * @param <T1> item对应的bean
 * @param <T2> item对应的databinding
</T2></T1> */
open class BaseAdapter<T1, T2 : ViewDataBinding?>(
    var context: Context?,
    @LayoutRes var layout: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: List<T1>?
    private val inflater: LayoutInflater

    private lateinit var bindView: BindView<T1, T2>
    private lateinit var bvForTrackEvent: BVForTrackEvent<T1, T2>
    private var mHeaderView: View? = null
    private var mFooterView1: View? = null
    private var mFooterView2: View? = null
    private var mHasStableIds = false
    fun initList(list: List<T1>?) {
        this.list = list
    }

    fun refresh(list: List<T1>?) {
        if (list == null) return
        this.list = list
        notifyDataSetChanged()
    }

    fun addList(list: List<T1>?) {
        if (list == null || list.isEmpty()) return
        val positionStart = getDataSize()
        (this.list as MutableList).addAll(list)
        notifyItemRangeInserted(positionStart, list.size)
    }

    fun setOnBindViewHolder(bindView: BindView<T1, T2>) {
        this.bindView = bindView
    }

    fun setOnBVForTrackEvent(bvForTrackEvent: BVForTrackEvent<T1, T2>) {
        this.bvForTrackEvent = bvForTrackEvent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return HeaderFooterViewHolder(mHeaderView!!)
        }
        if (mFooterView1 != null && viewType == TYPE_FOOTER_1) {
            return HeaderFooterViewHolder(mFooterView1!!)
        }
        if (mFooterView2 != null && viewType == TYPE_FOOTER_2) {
            return HeaderFooterViewHolder(mFooterView2!!)
        }
        val view = inflater.inflate(layout, parent, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var pos = position
        if (getItemViewType(pos) == TYPE_NORMAL) {
            pos = if (mHeaderView == null) pos else pos - 1
            list?.get(pos)?.let {
                bindView.onBindViewHolder(
                    it,
                    (holder as BaseViewHolder).binding as T2,
                    pos
                )
            }
        }
    }

    var headerView: View?
        get() = mHeaderView
        set(headerView) {
            mHeaderView = headerView
            if (headerView != null) notifyItemInserted(0)
        }
    var footerView1: View?
        get() = mFooterView1
        set(footerView) {
            mFooterView1 = footerView
            if (footerView != null) notifyItemInserted(if (mFooterView2 != null) itemCount - 2 else itemCount - 1)
        }
    var footerView2: View?
        get() = mFooterView2
        set(footerView) {
            mFooterView2 = footerView
            if (mFooterView2 != null) notifyItemInserted(itemCount - 1)
        }

    fun isHasStableIds(hasStableIds: Boolean) {
        setHasStableIds(true)
        mHasStableIds = hasStableIds
    }

    override fun getItemViewType(position: Int): Int {
        if (mHeaderView == null && mFooterView1 == null && mFooterView2 == null) return TYPE_NORMAL
        if (position == 0 && mHeaderView != null) return TYPE_HEADER
        if (mFooterView1 != null) {
            if (mFooterView2 == null && position == itemCount - 1) return TYPE_FOOTER_1
            if (mFooterView2 != null && position == itemCount - 2) return TYPE_FOOTER_1
        }
        return if (mFooterView2 != null && position == itemCount - 1) TYPE_FOOTER_2 else TYPE_NORMAL
    }

    override fun getItemCount(): Int {
        if (list == null) return 0
        if (mHeaderView == null && mFooterView1 == null && mFooterView2 == null) return list!!.size else if (mHeaderView == null && mFooterView1 == null) return list!!.size + 1 else if (mHeaderView == null && mFooterView2 == null) return list!!.size + 1 else if (mFooterView1 == null && mFooterView2 == null) return list!!.size + 1 else if (mHeaderView == null) return list!!.size + 2 else if (mFooterView1 == null) return list!!.size + 2 else if (mFooterView2 == null) return list!!.size + 2
        return list!!.size + 3
    }

    override fun getItemId(position: Int): Long {
        return if (mHasStableIds) position.toLong() else super.getItemId(position)
    }

    fun getDataSize(): Int {
        return list!!.size
    }

    fun getData(): List<T1>? {
        return list
    }

    fun interface BindView<T1, T2> {
        fun onBindViewHolder(data: T1, binding: T2, position: Int)
    }

    fun interface BVForTrackEvent<T1, T2> {

        /**
         * 便于列表滑动曝光时插入埋点需要的业务数据
         */
        fun onBindViewForTrackEvent(data: T1, binding: T2, position: Int)
    }

    class BaseViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(itemView!!)

    }

    class HeaderFooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    companion object {
        const val TYPE_HEADER = 0 // header
        const val TYPE_FOOTER_1 = 1 // footer
        const val TYPE_FOOTER_2 = 2 // footer
        const val TYPE_NORMAL = 3 // 内容
    }

    init {
        if (context == null) {
            context = App.instance
        }
        inflater = LayoutInflater.from(context)
        list = ArrayList()
    }
}