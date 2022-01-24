package com.jeahwan.origin.utils.expandfun

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.jeahwan.origin.ui.ContainerActivity
import com.jeahwan.origin.utils.RxTask
import com.jeahwan.origin.utils.expandfun.ToastKt.toastSingle
import java.util.*
import java.util.concurrent.TimeUnit

object NavKt {

    /**
     * 跳转页面
     *
     * @param clz       所跳转的目的Activity类
     * @param bundle    跳转所携带的信息
     */
    fun Activity.toActivity(
        clz: Class<*>?,
        bundle: Bundle = Bundle()
    ) {
        val intent = Intent(this, clz)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    fun Activity.startContainerActivity(
        canonicalName: String?,
        bundle: Bundle = Bundle(),
        schemeData: Uri? = null,
    ) {
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        intent.putExtra(ContainerActivity.BUNDLE, bundle)
        //携带scheme数据 解决闪屏页跳登录后再到首页的数据传递
        schemeData?.run { intent.data = this }
//        if (canonicalName == LoginFragment::class.java.canonicalName) {
//            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
        startActivity(intent)
    }

    fun Activity.startContainerActivity(canonicalName: String?, vararg pairs: Pair<String, Any?>) {
        startContainerActivity(canonicalName, bundleOf(*pairs))
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    fun Activity.startContainerActivityForResult(
        canonicalName: String?,
        requestCode: Int,
        bundle: Bundle = Bundle(),
    ) {
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        intent.putExtra(ContainerActivity.BUNDLE, bundle)
        startActivityForResult(intent, requestCode)
    }

    /**
     * 下边是fragment重载activity的方法
     */
    fun Fragment.toActivity(
        clz: Class<*>?,
        vararg pairs: Pair<String, Any?>,
    ) {
        activity?.toActivity(clz, bundleOf(*pairs))
    }

    fun Fragment.startContainerActivity(canonicalName: String?, bundle: Bundle = Bundle()) {
        activity?.startContainerActivity(canonicalName, bundle)
    }

    fun Fragment.startContainerActivity(canonicalName: String?, vararg pairs: Pair<String, Any?>) {
        activity?.startContainerActivity(canonicalName, bundleOf(*pairs))
    }

    /**
     * 共享元素动画跳转
     * 需要提前给B页面对应的view设置transitionName
     */
    fun Fragment.startContainerActivityForTransAnim(
        canonicalName: String?,
        bundle: Bundle = Bundle(),
        vararg viewPairs: androidx.core.util.Pair<View, String>
    ) {
        viewPairs.forEach {
            //给A页面的view设置
            it.first.transitionName = it.second
        }
        //Fragment的startActivity()方法无法传入ActivityOptionsCompat，所以需要使用
        //ActivityCompat.startActivity()来进行跳转
        ActivityCompat.startActivity(
            requireActivity(),
            Intent(activity, ContainerActivity::class.java).apply {
                putExtra(ContainerActivity.FRAGMENT, canonicalName)
                putExtra(ContainerActivity.BUNDLE, bundle)
            },
            //传递给B页面，生成动画bundle
            ActivityOptionsCompat.makeSceneTransitionAnimation(activity as Activity, *viewPairs)
                .toBundle()
        )
    }

    //不要直接调用activity的方法 会接收不到回调
    fun Fragment.startContainerActivityForResult(
        canonicalName: String?,
        requestCode: Int,
        vararg pairs: Pair<String, Any?>
    ) {
        activity?.let {
            val bundle = bundleOf(*pairs)
            val intent = Intent(it, ContainerActivity::class.java)
            intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
            startActivityForResult(intent, requestCode)
        }
    }

    private fun parseParams(param: String): HashMap<String, String> {
        val map = hashMapOf<String, String>()
        try {
            if (param.isEmpty()) return map
            val paramUri = Uri.parse("http://host/path$param")
            val queryParameterNames = paramUri.queryParameterNames
            for (key in queryParameterNames) {
                if (key.isEmpty()) continue
                val value = paramUri.getQueryParameter(key)
                if (value.isNullOrEmpty()) continue
                map[key] = value
            }
        } catch (e: Exception) {
        }
        return map
    }
}