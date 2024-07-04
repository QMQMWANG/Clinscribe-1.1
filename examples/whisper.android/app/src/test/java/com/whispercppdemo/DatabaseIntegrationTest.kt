package com.whispercppdemo.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DatabaseIntegrationTest {

    private lateinit var dbHelper: DBHelper
    private lateinit var database: SQLiteDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DBHelper(context)
        database = dbHelper.writableDatabase
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun `test insert, retrieve, update, and delete record`() {
        // Create a new record
        val contentValues = ContentValues().apply {
            put(DBHelper.COLUMN_CONTENT, "Test FHIR Record")
        }
        val rowId = database.insert(DBHelper.TABLE_NAME, null, contentValues)
        assertTrue(rowId != -1L)

        // Retrieve the record
        val cursor = database.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_ID, DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString()),
            null, null, null
        )
        assertTrue(cursor.moveToFirst())
        assertEquals("Test FHIR Record", cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)))

        // Update the record
        val updatedValues = ContentValues().apply {
            put(DBHelper.COLUMN_CONTENT, "Updated FHIR Record")
        }
        val rowsAffected = database.update(
            DBHelper.TABLE_NAME,
            updatedValues,
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString())
        )
        assertEquals(1, rowsAffected)

        // Verify the update
        val updatedCursor = database.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString()),
            null, null, null
        )
        assertTrue(updatedCursor.moveToFirst())
        assertEquals("Updated FHIR Record", updatedCursor.getString(updatedCursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)))

        // Delete the record
        val rowsDeleted = database.delete(
            DBHelper.TABLE_NAME,
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString())
        )
        assertEquals(1, rowsDeleted)

        // Verify the deletion
        val deletedCursor = database.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString()),
            null, null, null
        )
        assertFalse(deletedCursor.moveToFirst())

        cursor.close()
        updatedCursor.close()
        deletedCursor.close()
    }

    @Test
    fun `test data consistency across sessions`() {
        // Insert a record
        val contentValues = ContentValues().apply {
            put(DBHelper.COLUMN_CONTENT, "Consistent FHIR Record")
        }
        val rowId = database.insert(DBHelper.TABLE_NAME, null, contentValues)
        assertTrue(rowId != -1L)

        // Close and reopen the database
        dbHelper.close()
        dbHelper = DBHelper(ApplicationProvider.getApplicationContext())
        database = dbHelper.writableDatabase

        // Retrieve the record again
        val cursor = database.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString()),
            null, null, null
        )
        assertTrue(cursor.moveToFirst())
        assertEquals("Consistent FHIR Record", cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)))

        cursor.close()
    }
}
