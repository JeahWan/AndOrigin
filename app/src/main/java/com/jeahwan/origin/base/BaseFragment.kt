package com.jeahwan.origin.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jeahwan.origin.R
import com.jeahwan.origin.ui.ContainerActivity
import com.jeahwan.origin.utils.ImageLoadUtil
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.*

abstract class BaseFragment<V : ViewDataBinding>(
    private val layoutId: Int = R.layout.fragment_empty_view
) : Fragment() {
    protected lateinit var binding: V
    protected var pIndex = 1
    protected var pageSize = 10
    private var onResumed = false
    private var mHidden = false
    var pageName = this::class.java.canonicalName
    var pageUUID = UUID.randomUUID().toString()
    var pageTitle = ""
    var contentId: String? = null
    var metaData: String? = null
    var contentType: String? = null // 1-文章；2-系列课；3-打包课(无)；4-话题；5-作者
    var startTime: Long = System.currentTimeMillis()
    private var rvArray: Array<RecyclerView> = emptyArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            layoutId,
            container,
            false
        )
        //使用了compose就不走binding了
        return initUI() ?: binding.root
    }

    /**
     * 递归变为查找是否有recyclerview
     */
    private fun findRecyclerView(root: View) {
        if (root is ViewGroup) {
            val childCount = root.childCount
            for (i in 0 until childCount) {
                val child = root.getChildAt(i)
                if (child is RecyclerView) {
                    rvArray = rvArray.plus(child)
                }
                findRecyclerView(child)
            }
        }
    }

    /**
     * 初始化compose ui
     * todo 注意！！！ 为了兼容老代码 使用compose时需要指定泛型为FragmentEmptyViewBinding 避免binding报错 构造xml不用传
     *
     * @return compose生成的view
     */
    open fun initUI(): View? = null

    /**
     * 逻辑
     */
    abstract fun initData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGoBack()
        if (!lazyLoad()) { // 圈子首页接口慢，使用懒加载
            initData()
        }
    }

    /**
     * 设置返回按钮操作 不必子类重复实现
     */
    private fun setGoBack() {
        if (binding.root.rootView.findViewById<View?>(R.id.title_layout) != null) {
            if (binding.root.rootView.findViewById<View>(R.id.title_layout)
                    .findViewById<View?>(R.id.go_back) != null
            ) {
                binding.root.rootView.findViewById<View>(R.id.title_layout)
                    .findViewById<View>(R.id.go_back)
                    .setOnClickListener { activity?.onBackPressed() }
            }
        }
    }

    /**
     * 处理back
     *
     * @return 是否处理
     */
    open fun onFragmentBackPressed(): Boolean {
        return false
    }

    protected open fun lazyLoad(): Boolean {
        return false
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        if (activity is ContainerActivity) {
            (activity as ContainerActivity).setBackgroundColor(color)
        }
    }

    protected fun finish() {
        activity?.finish()
    }

    open fun refreshCurrentItem() {}

    /**
     * 绑定的activity是否被销毁
     */
    protected val isDestroyAct: Boolean
        get() = ImageLoadUtil.instance.isDestroy(activity)

    protected fun onBackPressed() {
        activity?.onBackPressed()
    }

    protected fun getNoMoreDataFooterView(root: ViewGroup): View {
        return LayoutInflater.from(context)
            .inflate(R.layout.footer_load_no_more_data, root, false)
    }

    protected fun finishLoadMoreWithNoMoreData(
        adapter: BaseAdapter<*, *>,
        refreshView: SmartRefreshLayout,
        footerView: View?,
        isNoMoreData: Boolean = false,
    ) {
        refreshView.setEnableLoadMore(!isNoMoreData)
        if (isNoMoreData) {
            refreshView.finishLoadMoreWithNoMoreData()
            if (footerView != null && pIndex > 1 && adapter.footerView2 == null) adapter.footerView2 =
                footerView
        } else if (adapter.footerView2 != null) adapter.footerView2 = null
    }

    protected open fun canUseHiddenChangedReport(): Boolean {
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            startTime = System.currentTimeMillis()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            startTime = System.currentTimeMillis()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mHidden || parentFragment?.isHidden == true) {
            return
        }
        onResumed = true
        startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        if (mHidden || parentFragment?.isHidden == true) {
            return
        }
    }
}