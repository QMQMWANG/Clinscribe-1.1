package com.whispercppdemo.data

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.*
import android.content.ContentValues
import android.content.Context
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DBHelperTest {

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
    fun `test database creation`() {
        assertNotNull(database)
        assertTrue(database.isOpen)
    }

    @Test
    fun `test table creation`() {
        val cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(DBHelper.TABLE_NAME))
        assertNotNull(cursor)
        assertTrue(cursor.moveToFirst())
        cursor.close()
    }

    @Test
    fun `test database upgrade`() {
        val oldVersion = 1
        val newVersion = 2

        // Simulate an upgrade by calling onUpgrade manually
        dbHelper.onUpgrade(database, oldVersion, newVersion)

        // Check if the table still exists after the upgrade
        val cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(DBHelper.TABLE_NAME))
        assertNotNull(cursor)
        assertTrue(cursor.moveToFirst())
        cursor.close()
    }

    @Test(expected = SQLiteException::class)
    fun `test database error handling`() {
        // Try to execute an invalid SQL statement
        database.execSQL("SELECT * FROM non_existing_table")
    }

    @Test
    fun `test insert and query`() {
        val contentValues = ContentValues().apply {
            put(DBHelper.COLUMN_CONTENT, "Test Content")
        }
        val rowId = database.insert(DBHelper.TABLE_NAME, null, contentValues)
        assertTrue(rowId != -1L)

        val cursor = database.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_CONTENT),
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(rowId.toString()),
            null, null, null
        )

        assertNotNull(cursor)
        assertTrue(cursor.moveToFirst())
        assertEquals("Test Content", cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)))
        cursor.close()
    }
}
