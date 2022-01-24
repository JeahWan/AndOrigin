package com.jeahwan.origin.base

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeahwan.origin.R
import com.jeahwan.origin.databinding.FragmentRecyclerViewBinding
import java.util.*

abstract class BaseRecyclerViewFragment<dataClass, layoutBinding : ViewDataBinding> :
    BaseFragment<FragmentRecyclerViewBinding>(R.layout.fragment_recycler_view) {
    protected lateinit var dataList: MutableList<dataClass>
    protected lateinit var adapter: BaseAdapter<dataClass, layoutBinding>
    private var mNoMoreDataFooterView: View? = null
    protected var mHasLoad = false // 数据是否已加载

    override fun initData() {
        //init
        pageSize = uIConfig.pageSize
        dataList = ArrayList()
        adapter = BaseAdapter<dataClass, layoutBinding>(context, uIConfig.itemLayoutId).apply {
            setOnBindViewHolder { data, binding, position ->
                getView(
                    data,
                    binding,
                    position
                )
            }
            setOnBVForTrackEvent { data, binding, position ->
                getVForTrackEvent(
                    data,
                    binding,
                    position
                )
            }
            isHasStableIds(true)
            binding.rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            binding.rv.adapter = this
        }
        //set
        setTitleBarAndListener()
        //loadData
        if (uIConfig.firstGetDataRefresh) {
            binding.refreshLayout.autoRefresh()
        } else {
            getData(false)
        }
    }

    private fun setTitleBarAndListener() {
        //设置title
        with(binding.titleLayout) {
            if (!TextUtils.isEmpty(uIConfig.title)) {
                rlRoot.visibility = View.VISIBLE
                title = uIConfig.title
                rlRoot.setOnClickListener {
                    val linearLayoutManager = binding.rv.layoutManager as LinearLayoutManager?
                        ?: return@setOnClickListener
                    val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                    if (firstVisibleItem > 5 && binding.rv.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        binding.rv.scrollToPosition(5)
                        binding.rv.smoothScrollToPosition(0)
                    }
                }
            } else {
                rlRoot.visibility = View.GONE
            }
            //是否显示返回键、分割线
            hideLeft = uIConfig.hideBackNav
            hideDivider = uIConfig.hideTitleBarDivider
        }
        //是否启用下拉刷新
        with(binding.refreshLayout) {
            setEnableRefresh(uIConfig.enableRefresh)
            setOnRefreshListener {
                pIndex = 1
                finishLoadMoreWithNoMoreData(false)
                getData(false)
            }
        }
        //启用分页、上拉加载
        if (uIConfig.enablePaging) {
            mNoMoreDataFooterView = addNoMoreDataView()
            binding.refreshLayout.setOnLoadMoreListener {
                pIndex++
                getData(false)
            }
        }
    }

    /**
     * 在接口回调onSuccess中调用
     *
     * @param list 数据
     */
    protected fun onSuccessUI(list: List<dataClass>?) {
        mHasLoad = true
        binding.refreshLayout.finishRefresh()
        binding.refreshLayout.finishLoadMore()
        if (pIndex == 1 && (list == null || list.isEmpty())) {
            //没有数据
            showNoDataUI()
            return
        }
        //有数据才去启用分页 避免第一页就能上拉
        binding.refreshLayout.setEnableLoadMore(uIConfig.enablePaging)
        //添加headerView
        if (addHeaderView(binding.rv) != null && adapter.headerView == null) {
            adapter.headerView = addHeaderView(binding.rv)
        }
        if (addFooterView(binding.rv) != null && adapter.footerView1 == null) {
            adapter.footerView1 = addFooterView(binding.rv)
        }
        if (list!!.size < pageSize) {
            //没有更多数据
            finishLoadMoreWithNoMoreData(true)
        }
        binding.svNoData.visibility = View.GONE
        binding.rv.visibility = View.VISIBLE
        if (pIndex == 1) {
            //刷新
            dataList.clear()
            dataList.addAll(list)
            adapter.initList(dataList)
            adapter.notifyDataSetChanged()
        } else {
            //加载更多
            val positionStart = dataList.size + if (addHeaderView(binding.rv) != null) 1 else 0
            dataList.addAll(list)
            adapter.notifyItemRangeInserted(positionStart, list.size)
        }
    }

    /**
     * 在接口回调onError中调用
     *
     */
    protected fun onErrorUI() {
        binding.refreshLayout.setEnableLoadMore(mHasLoad)
        binding.refreshLayout.finishRefresh()
        binding.refreshLayout.finishLoadMore()
        if (adapter.getDataSize() <= 0) { // 已经展示过数据，不显示失败
//            showNoDataUI("网络信号差，加载失败啦", R.drawable.icon_network_error)
        }
    }

    /**
     * 显示无数据的UI
     *
     * @param errorTips
     * @param errorDrawable
     */

    private fun showNoDataUI(errorTips: String, errorDrawable: Int) {
        context?.let {
            with(binding.tvNoData) {
                text = errorTips
                setCompoundDrawables(
                    null,
                    ContextCompat.getDrawable(it, errorDrawable)
                        ?.apply { setBounds(0, 0, minimumWidth, minimumHeight) },
                    null,
                    null
                )
            }
            binding.svNoData.visibility = View.VISIBLE
            binding.rv.visibility = View.INVISIBLE
            binding.tvRefresh.visibility = if (uIConfig.enableRefresh) View.INVISIBLE else View.VISIBLE
            binding.tvRefresh.setOnClickListener { getData(true) }
        }
    }

    protected fun showNoDataUI() {
        //子类单独调用了此方法 需要额外处理下finishLoading
        binding.refreshLayout.finishRefresh()
        binding.refreshLayout.setEnableLoadMore(false)
        showNoDataUI(uIConfig.errorTips, uIConfig.errorDrawable)
    }

    /**
     * 没有更多数据的ui、状态处理
     *
     * @param isNoMoreData
     */
    protected open fun finishLoadMoreWithNoMoreData(isNoMoreData: Boolean) {
        if (uIConfig.enablePaging) {
            finishLoadMoreWithNoMoreData(
                adapter,
                binding.refreshLayout,
                mNoMoreDataFooterView,
                isNoMoreData
            )
        }
    }

    protected open fun addHeaderView(parent: ViewGroup?): View? {
        return null
    }

    private fun addFooterView(parent: ViewGroup?): View? {
        return null
    }

    private fun addNoMoreDataView(): View {
        return getNoMoreDataFooterView(binding.rv)
    }

    /**
     * 配置ui
     * 标题、错误提示、是否分页等
     *
     * @return
     */
    protected abstract val uIConfig: UIConfig

    /**
     * 接口请求 获取数据
     *
     * @param disableLoading 为了提醒复写时关掉接口的loading
     */
    protected abstract fun getData(disableLoading: Boolean)

    /**
     * item的view逻辑
     *
     * @param data
     * @param binding
     * @param position
     */
    protected abstract fun getView(data: dataClass, binding: layoutBinding, position: Int)

    /**
     * item的view逻辑 埋点相关
     *
     * @param data
     * @param binding
     * @param position
     */
    protected abstract fun getVForTrackEvent(data: dataClass, binding: layoutBinding, position: Int)

    override fun lazyLoad(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        if (!mHasLoad) {
            initData()
        }
    }

    /**
     * UI等配置
     */
    class UIConfig {
        //titlebar配置 页面标题 是否隐藏返回键、分割线等配置
        var title: String? = null
        var hideBackNav = false
        var hideTitleBarDivider = false

        //item的布局id
        var itemLayoutId = 0

        //无数据的提示、图
        var errorTips = "暂无数据"
        var errorDrawable = 0
//        var errorDrawable = R.drawable.icon_data_empty

        //是否分页 每页条数
        var enablePaging = true
        var pageSize = 20

        //是否启用下拉刷新
        var enableRefresh = true

        //初次加载是否显示下拉刷新（触发autoRefresh）
        var firstGetDataRefresh = false
    }
}