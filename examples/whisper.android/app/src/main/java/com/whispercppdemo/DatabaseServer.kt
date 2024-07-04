package com.whispercppdemo

import android.content.Context
import android.database.Cursor
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import com.whispercppdemo.data.DBHelper

class DatabaseServer(private val context: Context, port: Int) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri

        return when {
            uri.equals("/csv", ignoreCase = true) -> {
                exportDatabaseToCsv()
            }
            else -> {
                serveDatabaseFile()
            }
        }
    }

    private fun serveDatabaseFile(): Response {
        val dbFile = context.getDatabasePath("FhirDatabase.db")
        return newFixedLengthResponse(Response.Status.OK, "application/octet-stream", dbFile.inputStream(), dbFile.length()).apply {
            addHeader("Content-Disposition", "attachment; filename=\"FhirDatabase.db\"")
        }
    }

    private fun exportDatabaseToCsv(): Response {
        val dbHelper = DBHelper(context)
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${DBHelper.TABLE_NAME}", null)
        val csvOutput = ByteArrayOutputStream()

        cursor.use {
            val columnNames = cursor.columnNames
            csvOutput.write(columnNames.joinToString(",") { "\"$it\"" }.toByteArray())
            csvOutput.write("\n".toByteArray())

            while (cursor.moveToNext()) {
                val row = Array(cursor.columnCount) { cursor.getString(it).replace("\"", "\"\"") }
                csvOutput.write(row.joinToString(",") { "\"$it\"" }.toByteArray())
                csvOutput.write("\n".toByteArray())
            }
        }

        val csvData = csvOutput.toByteArray()
        val inputStream = ByteArrayInputStream(csvData)

        return newFixedLengthResponse(Response.Status.OK, "text/csv", inputStream, csvData.size.toLong()).apply {
            addHeader("Content-Disposition", "attachment; filename=\"FhirDatabase.csv\"")
        }
    }
}
