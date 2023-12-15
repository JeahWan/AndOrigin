package com.jeahwan.origin.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.jeahwan.origin.App.Companion.instance
import com.jeahwan.origin.utils.FileUtils.getCacheFilePath
import com.jeahwan.origin.utils.FileUtils.isFileExists
import java.io.*

/**
 * Decription: https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/src/main/java/com/blankj/utilcode/util/UriUtils.java
 */
object UriUtils {
    const val DOCUMENTS_DIR = "documents"
    val AUTHORITY = instance.packageName + ".provider"

    /**
     * File to uri.
     *
     * @param file The file.
     * @return uri
     */
    fun file2Uri(file: File, context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = context.packageName + ".utilcode.provider"
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * Uri to file.
     *
     * @param uri The uri.
     * @return file
     */
    fun uri2Path(uri: Uri, context: Context): String? {
        return try {
            val authority = uri.authority
            val scheme = uri.scheme
            val path = uri.path
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && path != null && path.startsWith(
                    "/external/"
                )
            ) {
                val file = File(
                    Environment.getExternalStorageDirectory().absolutePath
                            + path.replace("/external", "")
                )
                if (isFileExists(file)) {
                    return file.path
                }
            }
            if (ContentResolver.SCHEME_FILE == scheme) {
                path
            } // end 0
            else if (DocumentsContract.isDocumentUri(context, uri)) {
                if ("com.android.externalstorage.documents" == authority) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + "/" + split[1]
                    } else {
                        // Below logic is how External Storage provider build URI for documents
                        // http://stackoverflow.com/questions/28605278/android-5-sd-card-label
                        val mStorageManager =
                            context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                        try {
                            val storageVolumeClazz =
                                Class.forName("android.os.storage.StorageVolume")
                            val getVolumeList =
                                mStorageManager.javaClass.getMethod("getVolumeList")
                            val getUuid = storageVolumeClazz.getMethod("getUuid")
                            val getState = storageVolumeClazz.getMethod("getState")
                            val getPath = storageVolumeClazz.getMethod("getPath")
                            val isPrimary = storageVolumeClazz.getMethod("isPrimary")
                            val isEmulated = storageVolumeClazz.getMethod("isEmulated")
                            val result = getVolumeList.invoke(mStorageManager)
                            val length = java.lang.reflect.Array.getLength(result)
                            for (i in 0 until length) {
                                val storageVolumeElement =
                                    java.lang.reflect.Array.get(result, i)
                                //String uuid = (String) getUuid.invoke(storageVolumeElement);
                                val mounted = Environment.MEDIA_MOUNTED == getState.invoke(
                                    storageVolumeElement
                                ) || Environment.MEDIA_MOUNTED_READ_ONLY == getState.invoke(
                                    storageVolumeElement
                                )

                                //if the media is not mounted, we need not get the volume details
                                if (!mounted) continue

                                //Primary storage is already handled.
                                if (isPrimary.invoke(storageVolumeElement) as Boolean
                                    && isEmulated.invoke(storageVolumeElement) as Boolean
                                ) {
                                    continue
                                }
                                val uuid = getUuid.invoke(storageVolumeElement) as String
                                if (uuid == type) {
                                    return getPath.invoke(storageVolumeElement)
                                        .toString() + "/" + split[1]
                                }
                            }
                        } catch (ignored: Exception) {
                        }
                    }
                    null
                } // end 1_0
                else if ("com.android.providers.downloads.documents" == authority) {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        try {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), id.toLong()
                            )
                            return getFileFromUri(contentUri, "1_1", context)
                        } catch (e: NumberFormatException) {
                            if (id.startsWith("raw:")) {
                                return id.substring(4)
                            }
                        }
                    }
                    null
                } // end 1_1
                else if ("com.android.providers.media.documents" == authority) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    val contentUri: Uri = when (type) {
                        "image" -> {
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                        else -> {
                            return null
                        }
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    getFileFromUri(contentUri, selection, selectionArgs, "1_2", context)
                } // end 1_2
                else if (ContentResolver.SCHEME_CONTENT == scheme) {
                    getFileFromUri(uri, "1_3", context)
                } // end 1_3
                else {
                    null
                } // end 1_4
            } // end 1
            else if (ContentResolver.SCHEME_CONTENT == scheme) {
                getFileFromUri(uri, "2", context)
            } // end 2
            else {
                null
            } // end 3
        } catch (e: Exception) {
            getLocalPath(context, uri)
        }
    }

    private fun getFileFromUri(uri: Uri, code: String, context: Context): String? {
        return getFileFromUri(uri, null, null, code, context)
    }

    private fun getFileFromUri(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?,
        code: String,
        context: Context
    ): String? {
        val cursor = context.contentResolver.query(
            uri, arrayOf("_data"), selection, selectionArgs, null
        ) ?: return null
        return try {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("_data")
                if (columnIndex > -1) {
                    cursor.getString(columnIndex)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            cursor.close()
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null) {
            val path = uri2Path(uri, context)
            filename = if (path == null) {
                getName(uri.toString())
            } else {
                val file = File(path)
                file.name
            }
        } else {
            val returnCursor = context.contentResolver.query(
                uri, null,
                null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!isFileExists(dir)) {
            dir.mkdirs()
        }
        return dir
    }

    fun generateFileName(name: String?, directory: File?): File? {
        var name = name
        if (name == null) {
            return null
        }
        var file = File(directory, name)
        if (isFileExists(file)) {
            var fileName: String = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (isFileExists(file)) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            return null
        }
        return file
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`!!.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 将文件File转成Uri
     */
    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(filePath),
            null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            cursor.close()
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (isFileExists(imageFile)) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
            } else {
                null
            }
        }
    }

    private fun getLocalPath(context: Context, uri: Uri): String? {
        try {
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // LocalStorageProvider
                if (isLocalStorageDocument(uri)) {
                    // The path is the id
                    return DocumentsContract.getDocumentId(uri)
                } else if ("com.android.externalstorage.documents" == uri.authority) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + "/" + split[1]
                    } else if ("home".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + "/documents/" + split[1]
                    }
                } else if ("com.android.providers.downloads.documents" == uri.authority) {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id != null && id.startsWith("raw:")) {
                        return id.substring(4)
                    }
                    val contentUriPrefixesToTry = arrayOf(
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads"
                    )
                    for (contentUriPrefix in contentUriPrefixesToTry) {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse(contentUriPrefix),
                            id!!.toLong()
                        )
                        try {
                            val path = getDataColumn(context, contentUri, null, null)
                            if (path != null) {
                                return path
                            }
                        } catch (ignored: Exception) {
                        }
                    }

                    // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                    val fileName = getFileName(context, uri)
                    val cacheDir = getDocumentCacheDir(context)
                    val file = generateFileName(fileName, cacheDir)
                    var destinationPath: String? = null
                    if (file != null) {
                        destinationPath = file.absolutePath
                        saveFileFromUri(context, uri, destinationPath)
                    }
                    return destinationPath
                } else if ("com.android.providers.media.documents" == uri.authority) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {

                // Return the remote address
                return if ("com.google.android.apps.photos.content" == uri.authority) {
                    uri.lastPathSegment
                } else getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } catch (e: Exception) {
            return getFilePathFromUri(context, uri)
        }
        return null
    }

    /**
     * 根据Uri获取真实的文件路径
     *
     * @param context 上下文
     * @param uri     图片uri地址
     * @return 图片路径
     */
    private fun getFilePathFromUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val resolver = context.contentResolver
        var input: FileInputStream? = null
        var output: FileOutputStream? = null
        try {
            val pfd = resolver.openFileDescriptor(uri, "r") ?: return null
            val fd = pfd.fileDescriptor
            input = FileInputStream(fd)
            val outputFile = getCacheFilePath("IMG_", ".jpg")
            val tempFilename = outputFile.absolutePath
            output = FileOutputStream(tempFilename)
            var read: Int
            val bytes = ByteArray(4096)
            while (input.read(bytes).also { read = it } != -1) {
                output.write(bytes, 0, read)
            }
            return File(tempFilename).absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                input?.close()
                output?.close()
            } catch (t: Throwable) {
                // Do nothing
                t.printStackTrace()
            }
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (ignored: Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is local.
     */
    fun isLocalStorageDocument(uri: Uri): Boolean {
        return AUTHORITY == uri.authority
    }
}