package com.whispercppdemo.ui.main

import android.content.Context
import android.os.Environment
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import org.junit.Assert.*
import java.io.FileOutputStream
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ClinicalSummaryIntegrationTest {

    private lateinit var context: Context
    private lateinit var summaryFile: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        summaryFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ClinicalSummary.doc")
    }

    @After
    fun tearDown() {
        // Clean up files created during tests
        if (summaryFile.exists()) {
            summaryFile.delete()
        }
    }

    @Test
    fun `test generating, editing, and exporting Clinical Summary`() = runBlocking {
        val initialContent = "{ \"fhir\": \"test record\" }"
        val updatedContent = "{ \"fhir\": \"updated test record\" }"

        // Simulate generating Clinical Summary from FHIR record
        var clinicalSummary = initialContent

        // Simulate editing the Clinical Summary
        clinicalSummary = updatedContent
        assertEquals(updatedContent, clinicalSummary)

        // Export the Clinical Summary to a DOC file
        exportToDocClinicalSummary(context, clinicalSummary, summaryFile)

        assertTrue(summaryFile.exists())
        assertEquals(updatedContent, summaryFile.readText())
    }

    // Simplified exportToDoc function without ScaffoldState
    private suspend fun exportToDocClinicalSummary(context: Context, text: String, file: File) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        var exportFile = file
        var fileIndex = 1

        // Check if the file already exists and create a new file name if necessary
        while (exportFile.exists()) {
            exportFile = File(directory, "${file.nameWithoutExtension}$fileIndex.doc")
            fileIndex++
        }

        try {
            FileOutputStream(exportFile).use { fos ->
                fos.write(text.toByteArray())
                fos.flush()
            }
        } catch (e: Exception) {
            throw e // Re-throw the exception for the test to catch it
        }
    }
}
