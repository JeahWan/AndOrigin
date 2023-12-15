package com.jeahwan.origin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Base64
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt


object BitmapUtil {
    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    fun bitmapToBase64(bitmap: Bitmap?): String? {
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                baos.flush()
                baos.close()
                val bitmapBytes = baos.toByteArray()
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    fun base64ToBitmap(base64Data: String?): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    private fun getSmallBitmap(filePath: String?): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options)
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800)
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int,
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    /**
     * 图片压缩-质量压缩
     *
     * @param filePath 源图片路径
     * @return 压缩后的路径
     */
    fun compressImage(filePath: String?): String? {

        //原文件
        val oldFile = File(filePath)


        //压缩文件路径 照片路径/
        val targetPath = oldFile.path
        val quality = 80 //压缩比例0-100
        val bm = getSmallBitmap(filePath) //获取一定尺寸的图片
        //        int degree = getRotateAngle(filePath);//获取相片拍摄角度
//
//        if (degree != 0) {//旋转照片角度，防止头像横着显示
//            bm = setRotateAngle(degree, bm);
//        }
        val outputFile = File(targetPath)
        try {
            if (!outputFile.exists()) {
                outputFile.parentFile.mkdirs()
                //outputFile.createNewFile();
            } else {
                outputFile.delete()
            }
            val out = FileOutputStream(outputFile)
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return filePath
        }
        return outputFile.path
    }

    fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Int): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        //得到画布
        val canvas = Canvas(output)
        //将画布的四角圆化
        val color = Color.RED
        val paint = Paint()
        //得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        //值越大角度越明显
        val roundPx = radius.toFloat()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        //drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    fun getNewBitmap(
        bitmap: Bitmap,
        newWidth: Int,
        newHeight: Int,
        callback: (bitmap: Bitmap) -> Unit
    ) {
        RxTask.executeRxTask({
            val newBitmap = compressImage(bitmap)
            // 获得图片的宽高.
            val width = newBitmap.width
            val height = newBitmap.height
            // 计算缩放比例.
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            // 取得想要缩放的matrix参数.
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            // 得到新的图片.
            Bitmap.createBitmap(newBitmap, 0, 0, width, height, matrix, true)
        }, {
            it?.let { bitmap ->
                callback(bitmap)
            }
        })
    }

    private fun compressImage(image: Bitmap): Bitmap {
        val baos = ByteArrayOutputStream()
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val options = when {
            // 500kb
            baos.toByteArray().size > 1024 * 500 -> 80
            else -> 100
        }
        if (options == 100 || image.isRecycled) {
            return image
        }
        image.compress(Bitmap.CompressFormat.JPEG, options, baos)
        //把压缩后的数据baos存放到ByteArrayInputStream中
        val isBm = ByteArrayInputStream(baos.toByteArray())
        //把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(isBm, null, null) ?: image
    }

    /**
     * 对图片进行缩放
     * @param bgimage
     * @param w
     * @param h
     * @return
     */
    fun zoomImage(bgimage: Bitmap, w: Double, h: Double): Bitmap {
//        //使用方式
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);
//        int paddingLeft = getPaddingLeft();
//        int paddingRight = getPaddingRight();
//        int bmWidth = bitmap.getWidth();//图片高度
//        int bmHeight = bitmap.getHeight();//图片宽度
//        int zoomWidth = getWidth() - (paddingLeft + paddingRight);
//        int zoomHeight = (int) (((float)zoomWidth / (float)bmWidth) * bmHeight);
//        Bitmap newBitmap = zoomImage(bitmap, zoomWidth,zoomHeight);
        // 获取这个图片的宽和高
        var newWidth = w
        var newHeight = h
        val width = bgimage.width.toFloat()
        val height = bgimage.height.toFloat()
        //如果宽度为0 保持原图
        if (newWidth == 0.0) {
            newWidth = width.toDouble()
            newHeight = height.toDouble()
        }
        // 创建操作图片用的matrix对象
        val matrix = Matrix()
        // 计算宽高缩放率
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight)
        val bitmap = Bitmap.createBitmap(
            bgimage, 0, 0,
            width.toInt(),
            height.toInt(), matrix, true
        )
        return compressImage(bitmap) //质量压缩
    }

    fun setSelfAdaptionHeight(url: String?, iv: ImageView, context: Context?) {
        if (context == null) return
        Glide.with(context)
            .load(url)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    with(iv) {
                        post {
                            val imageWidth = resource.minimumWidth
                            val imageHeight = resource.minimumHeight
                            val measuredWidth = measuredWidth
                            updateLayoutParams {
                                //宽度固定,然后根据原始宽高比得到此固定宽度需要的高度
                                height =
                                    measuredWidth * imageHeight / imageWidth
                            }
                            setImageDrawable(resource)
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }
}