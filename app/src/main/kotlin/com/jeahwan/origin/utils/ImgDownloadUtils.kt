package com.jeahwan.origin.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.jeahwan.origin.utils.RxTask.executeRxTask
import com.jeahwan.origin.utils.expandfun.ToastKt.toastShort
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by juemuren on 2018/2/10.
 */
object ImgDownloadUtils {

    /**
     * 保存二维码到本地相册
     */
    fun saveImageToPhotos(context: Context?, url: String?, callback: () -> Unit) {
        if (context == null) return
        executeRxTask({ returnBitMap(url) {} }, {
            if (it == null) {
                toastShort("图片获取失败")
                return@executeRxTask
            }
            saveImageToPhotos(context, it, callback) {}
        })
    }

    /**
     * 保存二维码到本地相册
     */
    fun saveImageToPhotos(
        context: Context?,
        url: String?,
        callback: () -> Unit,
        failCallback: () -> Unit
    ) {
        if (context == null) return
        executeRxTask({ returnBitMap(url, failCallback) }, {
            if (it == null) {
                failCallback()
                return@executeRxTask
            }
            saveImageToPhotos(context, it, callback, failCallback)
        })
    }

    /**
     * 保存二维码到本地相册
     */
    fun saveImageToPhotos(
        context: Context?,
        bmp: Bitmap?,
        callback: () -> Unit,
        failCallback: () -> Unit
    ) {
        if (context == null) return
        if (bmp == null) {
            toastShort("bmp不能为空")
            failCallback()
            return
        }
        val fileName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
        if (Build.VERSION.SDK_INT >= 29) {
            saveImageAndroidO(context, bmp, fileName, callback, failCallback)
            return
        }
        // 首先保存图片
        val appDir =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + File.separator + FileUtils.EXTERNAL_DATA_FOLDER + File.separator)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            toastShort("文件写入失败")
            failCallback()
        }
        //插入图库
        if (file == null || !file.exists()) {
            toastShort("保存失败")
            failCallback()
            return
        }
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.parse("file://" + file.absolutePath)
        context.sendBroadcast(intent)
        callback()
        toastShort("保存成功")
    }

    private fun saveImageAndroidO(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        callback: () -> Unit,
        failCallback: () -> Unit
    ) {
        try {
            //设置保存参数到ContentValues中
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            //兼容Android Q和以下版本

            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_DCIM + File.separator + FileUtils.EXTERNAL_DATA_FOLDER
            )
            //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/signImage");

            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
                callback()
                toastShort("保存成功")
            } else {
                failCallback()
            }
        } catch (ignored: Exception) {
            failCallback()
        }
    }

    /**
     * 将URL转化成bitmap形式
     *
     * @param url
     * @return bitmap type
     */
    fun returnBitMap(url: String?, failCallback: () -> Unit): Bitmap? {
        val myFileUrl: URL
        var bitmap: Bitmap? = null
        try {
            myFileUrl = URL(url)
            val conn: HttpURLConnection
            conn = myFileUrl.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            val `is` = conn.inputStream
            bitmap = BitmapFactory.decodeStream(`is`)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            failCallback()
        } catch (e: IOException) {
            e.printStackTrace()
            failCallback()
        }
        return bitmap
    }

    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }
}