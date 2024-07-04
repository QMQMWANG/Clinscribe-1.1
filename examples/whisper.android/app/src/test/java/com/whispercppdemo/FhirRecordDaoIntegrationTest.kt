package com.whispercppdemo.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FhirRecordDaoIntegrationTest {

    private lateinit var dbHelper: DBHelper
    private lateinit var fhirRecordDao: FhirRecordDao
    private lateinit var database: SQLiteDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DBHelper(context)
        database = dbHelper.writableDatabase
        fhirRecordDao = FhirRecordDao(context)
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun testInsertAndQueryRecords() {
        // Insert a record using FhirRecordDao
        val content = "Test FHIR Record"
        val rowId = fhirRecordDao.insert(content)
        assertTrue(rowId != -1L)

        // Query the record using the database directly
        val cursor = database.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString()),
            null, null, null
        )
        assertTrue(cursor.moveToFirst())
        assertEquals(content, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)))
        cursor.close()
    }
}
