package com.jeahwan.origin.utils

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import com.jeahwan.origin.App.Companion.instance
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

    const val EXTERNAL_DATA_FOLDER = "qiniu"

    fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }

    /**
     * 获取一个文件路径
     *
     * @param filePrefix 前缀
     * @param fileSuffix 后缀
     * @return 文件
     */
    fun getFilePath(filePrefix: String, fileSuffix: String): File {
        val folderDir: File
        // 外部没有自定义拍照存储路径使用默认
        val rootDir: File
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            folderDir =
                File(rootDir.absolutePath + File.separator + EXTERNAL_DATA_FOLDER + File.separator)
        } else {
            rootDir = instance.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            folderDir = File(rootDir.absolutePath + File.separator)
        }
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
        if (!folderDir.exists()) {
            folderDir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault()).format(Date())
        val fileName = filePrefix + timeStamp + fileSuffix
        return File(folderDir, fileName)
    }

    /**
     * 获取一个缓存路径
     *
     * @param filePrefix 前缀
     * @param fileSuffix 后缀
     * @return 文件
     */
    fun getCacheFilePath(filePrefix: String, fileSuffix: String): File {
        val folderDir = instance.cacheDir
        if (!folderDir.exists()) {
            folderDir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault()).format(Date())
        val fileName = filePrefix + timeStamp + fileSuffix
        return File(folderDir, fileName)
    }

    /**
     * Notify system to scan the file.
     *
     * @param file The file.
     */
    fun notifySystemToScan(file: File?) {
        if (file == null || !file.exists()) return
        instance.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
    }

    fun notifySystemToScan(uri: Uri?) {
        if (uri == null) return
        instance.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }
}