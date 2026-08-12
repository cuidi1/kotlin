package com.example.sdkstudydemo.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.sdkstudydemo.sdk.MySdk

class DemoContentProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.example.sdkstudydemo.demo.provider"
        val SDK_INFO_URI: Uri = Uri.parse("content://$AUTHORITY/sdk_info")
        const val COLUMN_KEY = "key"
        const val COLUMN_VALUE = "value"
    }
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun onCreate(): Boolean {
        return true;
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        //Cursor 查询结果的游标


        //临时手工创建一个查询结果表格，现在只是为了学习，还没有真正接数据库
        val cursor = MatrixCursor(
            arrayOf(
                COLUMN_KEY,
                COLUMN_VALUE)
        )

        cursor.addRow(
            arrayOf(
                "sdk_initialized",
                MySdk.isInitialized().toString()
            )
        )

        cursor.addRow(
            arrayOf(
                "sdk_version",
                MySdk.getVersion()
            )
        )
        return cursor
    }

    override fun update(
        p0: Uri,
        p1: ContentValues?,
        p2: String?,
        p3: Array<out String?>?
    ): Int {
        return 0
    }
}