package com.jeahwan.origin.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.jeahwan.origin.App

object CommonUtils {
    const val FIRST_RUN = "FIRST_RUN"

    /**
     * 复制到剪切板
     */
    fun setClipboard(text: String?) {
        try {
            val cmb = App.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(null, text) ?: return
            cmb.setPrimaryClip(clip)
            Toast.makeText(App.instance, "复制成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        val mode = if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            View.MeasureSpec.UNSPECIFIED
        } else {
            View.MeasureSpec.EXACTLY
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
    }

    /**
     * 申请权限
     *
     * @param activity
     * @param onGrantedCallBack
     * @param permissions
     */
    fun checkPermission(
        activity: FragmentActivity?,
        onGrantedCallBack: () -> Unit,
        vararg permissions: String?,
    ) {
        if (activity == null) {
            return
        }
        //todo 替换
//        RxPermissions(activity)
//            .requestEach(*permissions)
//            .subscribe { permission: Permission ->
//                when {
//                    permission.granted -> {
//                        // 用户已经同意该权限
//                        onGrantedCallBack()
//                    }
//                    permission.shouldShowRequestPermissionRationale -> {
//                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                        //                        OnPermissionRefuse(false);
//                    }
//                    else -> {
//                        // 用户拒绝了该权限，并且选中『不再询问』
//                        try {
//                            toastShort("请点击权限，并允许全部权限")
//                            Toast.makeText(activity, "请点击权限，并允许全部权限", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                            val uri = Uri.fromParts("package", activity.packageName, null)
//                            intent.data = uri
//                            activity.startActivity(intent)
//                        } catch (ex: Exception) {
//                            toastShort("请在应用管理中打开app权限")
//                        }
//                    }
//                }
//            }
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    @JvmStatic
    fun getVersionName(context: Context): String {
        // 获取packagemanager的实例
        val packageManager = context.packageManager
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        var packInfo: PackageInfo? = null
        try {
            packInfo = packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_CONFIGURATIONS
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return if (packInfo != null) packInfo.versionName else ""
    }

    /**
     * 获取版本号code
     *
     * @return 版本号
     */
    @JvmStatic
    fun getVersionCode(): Int {
        val packageManager = App.instance.packageManager
        var packInfo: PackageInfo? = null
        try {
            packInfo = packageManager.getPackageInfo(
                App.instance.packageName,
                PackageManager.GET_CONFIGURATIONS
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return packInfo?.versionCode ?: 0
    }
}