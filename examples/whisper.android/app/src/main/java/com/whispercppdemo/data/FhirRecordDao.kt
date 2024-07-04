package com.whispercppdemo.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class FhirRecordDao(context: Context) {
    private val dbHelper = DBHelper(context)

    fun insert(content: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_CONTENT, content)
        }
        return db.insert(DBHelper.TABLE_NAME, null, values)
    }

    fun getRecordById(id: Int): String? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )
        cursor?.moveToFirst()
        val content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT))
        cursor.close()
        return content
    }
    fun getAllRecords(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            null, null, null, null, null
        )
        val records = mutableListOf<String>()
        while (cursor.moveToNext()) {
            records.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)))
        }
        cursor.close()
        return records
    }

    fun getFilteredRecords(query: String): List<String> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_CONTENT} LIKE ?",
            arrayOf("%$query%"),
            null, null, null
        )
        val records = mutableListOf<String>()
        while (cursor.moveToNext()) {
            records.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)))
        }
        cursor.close()
        return records
    }
    fun deleteRecord(content: String) {
        val db = dbHelper.writableDatabase
        db.delete(DBHelper.TABLE_NAME, "${DBHelper.COLUMN_CONTENT}=?", arrayOf(content))
    }

    fun updateRecord(oldContent: String, newContent: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_CONTENT, newContent)
        }
        db.update(DBHelper.TABLE_NAME, values, "${DBHelper.COLUMN_CONTENT}=?", arrayOf(oldContent))
    }

}
