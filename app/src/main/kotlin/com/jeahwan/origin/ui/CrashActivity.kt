/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions edu
 * limitations under the License.
 */
package com.jeahwan.origin.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.gyf.immersionbar.ktx.immersionBar
import com.jeahwan.origin.BuildConfig
import com.jeahwan.origin.Constants
import com.jeahwan.origin.R
import com.jeahwan.origin.utils.DensityUtil.dp2px
import com.jeahwan.origin.utils.ImageLoadUtil
import com.jeahwan.origin.utils.StringUtils

/**
 * 全局异常捕获
 * Created by Jeah on 2017/2/4.
 */
class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_crash)

        immersionBar {
            statusBarDarkFont(true)
        }

        //Close/restart button logic:
        //If a class if set, use restart.
        //Else, use close edu just finish the app.
        //It is recommended that you follow this logic if implementing a custom error activity.
        val restartButton =
            findViewById<Button>(R.id.customactivityoncrash_error_activity_restart_button)
        CustomActivityOnCrash.getConfigFromIntent(intent)?.run {
            if (isShowRestartButton && restartActivityClass != null) {
                restartButton.text = "重新启动"
                restartButton.setOnClickListener {
                    CustomActivityOnCrash.restartApplication(
                        this@CrashActivity,
                        this
                    )
                }
            } else {
                restartButton.setOnClickListener {
                    CustomActivityOnCrash.closeApplication(
                        this@CrashActivity,
                        this
                    )
                }
            }
            val moreInfoButton =
                findViewById<Button>(R.id.customactivityoncrash_error_activity_more_info_button)
            if (isShowErrorDetails) {
                findViewById<TextView>(R.id.tv_error_tips).visibility = View.VISIBLE
                moreInfoButton.setOnClickListener {
                    //We retrieve all the error data edu show it
                    var errorInformation =
                        CustomActivityOnCrash.getAllErrorDetailsFromIntent(
                            this@CrashActivity,
                            intent
                        )
                    if (!BuildConfig.DEBUG) {
                        errorInformation =
                            StringUtils.encryptPassword(
                                errorInformation,
                                Constants.Id.WECHAT_SECRET
                            )
                    }
                    val dialog = AlertDialog.Builder(this@CrashActivity)
                        .setTitle("错误详情")
                        .setMessage(errorInformation)
                        .setPositiveButton("关闭", null)
                        .setNeutralButton(
                            "复制日志"
                        ) { _, _ ->
                            copyErrorToClipboard(errorInformation)
                            Toast.makeText(this@CrashActivity, "复制成功", Toast.LENGTH_SHORT).show()
                        }
                        .show()
                    val textView = dialog.findViewById<TextView>(android.R.id.message)
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(12f).toFloat())
                }
            } else {
                moreInfoButton.visibility = View.GONE
            }
            val errorImageView =
                findViewById<ImageView>(R.id.customactivityoncrash_error_activity_image)
            errorDrawable?.let {
                errorImageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        errorDrawable!!,
                        theme
                    )
                )
            } ?: let {
                ImageLoadUtil.instance.loadImage(
                    this@CrashActivity,
                    "",
                    errorImageView
                )
            }
        }
    }

    private fun copyErrorToClipboard(msg: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            "错误信息", msg
        )
        clipboard.setPrimaryClip(clip)
    }
}