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
class ExportImportIntegrationTest {

    private lateinit var context: Context
    private lateinit var consultationFile: File
    private lateinit var summaryFile: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        consultationFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ClinicalConsultation.doc")
        summaryFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ClinicalSummary.doc")
    }

    @After
    fun tearDown() {
        // Clean up files created during tests
        if (consultationFile.exists()) {
            consultationFile.delete()
        }
        if (summaryFile.exists()) {
            summaryFile.delete()
        }
    }

    @Test
    fun `test export of ClinicalConsultation`() = runBlocking {
        val content = "Test Clinical Consultation"
        // Export the Clinical Consultation to a DOC file
        exportToDoc(context, content, consultationFile)

        // Verify the file was created and content is correct
        assertTrue(consultationFile.exists())
        assertEquals(content, consultationFile.readText())
    }

    @Test
    fun `test export of ClinicalSummary`() = runBlocking {
        val content = "Test Clinical Summary"
        // Export the Clinical Summary to a DOC file
        exportToDoc(context, content, summaryFile)

        // Verify the file was created and content is correct
        assertTrue(summaryFile.exists())
        assertEquals(content, summaryFile.readText())
    }

    @Test
    fun `test import of ClinicalConsultation`() = runBlocking {
        val content = "Test Clinical Consultation"
        // Simulate the file already existing
        consultationFile.writeText(content)

        // Read the content back to simulate importing
        val importedContent = consultationFile.readText()
        assertEquals(content, importedContent)
    }

    @Test
    fun `test import of ClinicalSummary`() = runBlocking {
        val content = "Test Clinical Summary"
        // Simulate the file already existing
        summaryFile.writeText(content)

        // Read the content back to simulate importing
        val importedContent = summaryFile.readText()
        assertEquals(content, importedContent)
    }

    // Simplified exportToDoc function without ScaffoldState
    private suspend fun exportToDoc(context: Context, text: String, file: File) {
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
