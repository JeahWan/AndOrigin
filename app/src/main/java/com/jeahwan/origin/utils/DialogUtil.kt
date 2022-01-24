package com.jeahwan.origin.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object DialogUtil {
    /**
     * 提示弹框
     *
     * @param context
     * @param tips
     * @param positiveText
     * @param positiveClickListener
     */
    fun showTipDialog(
        context: Context?,
        tips: String?,
        positiveText: String?,
        positiveClickListener: DialogInterface.OnClickListener?
    ) {
        if (context == null) {
            return
        }
        AlertDialog.Builder(context)
            .setTitle("提示")
            .setMessage(tips)
            .setPositiveButton(positiveText, positiveClickListener)
            .setNegativeButton("取消") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .create().show()
    }

    /**
     * 显示加载框
     *
     * @param context
     * @param content
     */
    fun showProgressDialog(
        context: Activity?,
        content: String?,
        dialog: ProgressDialog?
    ): ProgressDialog? {
        var dialog = dialog
        try {
            if (context == null || context.isFinishing) {
                return null
            }
            if (dialog == null) {
                dialog = ProgressDialog(context)
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                dialog.setMessage(content)
                dialog.setCancelable(false)
            }
            if (!dialog.isShowing) {
                dialog.show()
            }
        } catch (ignored: Exception) {
        }
        return dialog
    }
}