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
class FhirManagementIntegrationTest {

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
    fun `test save, retrieve, and filter FHIR record`() {
        val fhirRecord1 = "{ \"fhir\": \"test record 1\" }"
        val fhirRecord2 = "{ \"fhir\": \"test record 2\" }"

        // Save the first FHIR record // Save (Create/Insert)
        val rowId1 = dao.insert(fhirRecord1)
        assertTrue(rowId1 != -1L)

        // Retrieve the first FHIR record by ID
        val retrievedRecord1 = dao.getRecordById(rowId1.toInt())
        assertEquals(fhirRecord1, retrievedRecord1)

        // Save the second FHIR record
        val rowId2 = dao.insert(fhirRecord2)
        assertTrue(rowId2 != -1L)

        // Retrieve the second FHIR record by ID
        val retrievedRecord2 = dao.getRecordById(rowId2.toInt())
        assertEquals(fhirRecord2, retrievedRecord2)

        // Filter records that contain "test record 1"
        val filteredRecords = dao.getFilteredRecords("test record 1")
        assertEquals(1, filteredRecords.size)
        assertTrue(filteredRecords.contains(fhirRecord1))
    }
}
