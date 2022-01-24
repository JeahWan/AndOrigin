package com.jeahwan.origin.utils

import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import android.util.Patterns
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object StringUtils {
    private fun empty(text: String): Boolean =
        if (TextUtils.isEmpty(text)) true
        else text.length == 4 && text.uppercase(Locale.getDefault()) == "NULL"

    fun equal(text1: String, text2: String): Boolean {
        return if (empty(text1) || empty(
                text2
            )
        ) false else text1 == text2
    }

    fun isEmail(text: String): Boolean {
        return if (empty(text)) false else text.matches(Patterns.EMAIL_ADDRESS.pattern().toRegex())
    }

    fun isMobile(text: String): Boolean {
        return if (empty(text)) false else text.matches("^(13|14|15|16|17|18|19)\\d{9}$".toRegex())
    }

    fun isWebSite(text: String): Boolean {
        return if (empty(text)) false else text.matches(Patterns.WEB_URL.pattern().toRegex())
    }

    fun isNumber(text: String): Boolean {
        return if (empty(text)) false else try {
            text.toDouble()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 判断是否有此文件
     *
     * @param path
     * @return
     */
    fun hasFile(path: String): Boolean {
        return getFile(path) != null
    }

    /**
     * 获取是否有此文件
     *
     * @param text
     * @return
     */
    fun getFile(text: String): File? {
        if (empty(text)) return null
        val file = File(text)
        return if (file.exists()) file else null
    }

    fun getFloat(number: Float, index: Int): String {
        return getDouble(number.toDouble(), index)
    }

    private fun getDouble(number: Double, index: Int): String {
        var str = "##0"
        if (index > 0) {
            str += "."
            for (i in 0 until index) {
                str += "0"
            }
        }
        return DecimalFormat(str).format(number)
    }

    fun getDouble(text: String): Double {
        return if (isNumber(text)) {
            text.toDouble()
        } else 0.0
    }

    fun getInt(text: String): Int {
        return getInt(text, 0)
    }

    fun getInt(text: String, def: Int): Int {
        return if (isNumber(text)) {
            text.toInt()
        } else def
    }

    fun getLong(text: String): Long {
        return if (isNumber(text)) {
            text.toLong()
        } else 0
    }

    fun getUri(text: String): Uri? {
        return if (!empty(text)) {
            Uri.parse(text)
        } else null
    }

    /**
     * 加密
     */
    fun encryptPassword(clearText: String, password: String): String {
        try {
            val keySpec =
                DESKeySpec(password.toByteArray(StandardCharsets.UTF_8))
            val keyFactory =
                SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(keySpec)
            val cipher = Cipher.getInstance("DES")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return Base64.encodeToString(
                cipher.doFinal(clearText.toByteArray(StandardCharsets.UTF_8)),
                Base64.DEFAULT
            )
        } catch (e: Exception) {
        }
        return clearText
    }

    /**
     * 解密
     */
    fun decryptPassword(encryptedPwd: String, password: String): String {
        try {
            val keySpec = DESKeySpec(password.toByteArray(StandardCharsets.UTF_8))
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(keySpec)
            val encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT)
            val cipher = Cipher.getInstance("DES")
            cipher.init(Cipher.DECRYPT_MODE, key)
            val plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64)
            return String(plainTextPwdBytes)
        } catch (e: Exception) {
        }
        return encryptedPwd
    }
}