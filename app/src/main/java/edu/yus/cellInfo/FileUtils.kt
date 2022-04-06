package edu.yus.cellInfo

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

/**
 *
 * @author xrn1997
 * @date 2022/3/31 20:30.
 */
object FileUtils {
    /**
     * 获取文件的Uri，保存在DOWNLOADS文件夹下
     * @param displayName 文件名
     * @param mimeType 文件类型
     */
    fun getFileUri(context: Context, displayName: String, mimeType: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
        } else {
            @Suppress("DEPRECATION")
            values.put(
                MediaStore.MediaColumns.DATA,
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DOWNLOADS }/$displayName"
            )
        }
        return context.contentResolver.insert(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }
}