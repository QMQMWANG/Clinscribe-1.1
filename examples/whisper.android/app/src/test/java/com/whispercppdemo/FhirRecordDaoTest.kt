package com.whispercppdemo.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Specify the SDK version you want to use for testing
class FhirRecordDaoTest {

    private lateinit var dao: FhirRecordDao
    private lateinit var dbHelper: DBHelper

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DBHelper(context)
        dao = FhirRecordDao(context)
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun `test insert record`() {
        val content = "Test FHIR Record"
        val rowId = dao.insert(content)
        assertTrue(rowId != -1L)
    }

    @Test
    fun `test get record by id`() {
        val content = "Test FHIR Record"
        val rowId = dao.insert(content)
        val retrievedContent = dao.getRecordById(rowId.toInt())
        assertEquals(content, retrievedContent)
    }

    @Test
    fun `test get all records`() {
        dao.insert("Test FHIR Record 1")
        dao.insert("Test FHIR Record 2")

        val records = dao.getAllRecords()
        assertEquals(2, records.size)
    }

    @Test
    fun `test update record`() {
        val oldContent = "Old FHIR Record"
        val newContent = "New FHIR Record"

        dao.insert(oldContent)
        dao.updateRecord(oldContent, newContent)

        val records = dao.getAllRecords()
        assertTrue(records.contains(newContent))
        assertFalse(records.contains(oldContent))
    }

    @Test
    fun `test delete record`() {
        val content = "Test FHIR Record"
        dao.insert(content)

        dao.deleteRecord(content)

        val records = dao.getAllRecords()
        assertFalse(records.contains(content))
    }

    @Test
    fun `test filtered records`() {
        dao.insert("Test FHIR Record 1")
        dao.insert("Another FHIR Record")

        val filteredRecords = dao.getFilteredRecords("Test")
        assertEquals(1, filteredRecords.size)
        assertTrue(filteredRecords.contains("Test FHIR Record 1"))
    }
}
