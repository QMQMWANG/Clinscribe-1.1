package com.whispercppdemo.ui.main

import android.content.Context
import android.os.Environment
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.compose.material.SnackbarDuration


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ExportToDocClinicalConsultationTest {

    @get:Rule
    var rule = InstantTaskExecutorRule() // For LiveData testing

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        // Set the Main dispatcher to use TestCoroutineDispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()

        // Clean up the TestCoroutineDispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test export to doc consultation success`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()
        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val mockFile = File(mockDirectory, "ClinicalConsultation.doc")

        if (mockFile.exists()) {
            mockFile.delete()
        }

        // Act
        exportToDoc(mockContext, "Transcribed Content", mockScaffoldState)

        // Assert
        assert(mockFile.exists())
        val content = mockFile.readText()
        assert(content == "Transcribed Content")

        verify(mockSnackbarHostState).showSnackbar("File exported to ${mockFile.absolutePath}")
    }

    @Test
    fun `test consultation file name conflict`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()
        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val existingFile = File(mockDirectory, "ClinicalConsultation.doc")
        existingFile.writeText("Existing Content")

        // Act
        exportToDoc(mockContext, "New Transcribed Content", mockScaffoldState)

        // Assert
        assert(existingFile.exists())
        assert(existingFile.readText() == "Existing Content")

        val newFile = File(mockDirectory, "ClinicalConsultation1.doc")
        assert(newFile.exists())
        assert(newFile.readText() == "New Transcribed Content")

        verify(mockSnackbarHostState).showSnackbar("File exported to ${newFile.absolutePath}")
    }

    @Test
    fun `test consultation empty content`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()
        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val mockFile = File(mockDirectory, "ClinicalConsultation.doc")

        if (mockFile.exists()) {
            mockFile.delete()
        }

        // Act
        exportToDoc(mockContext, "", mockScaffoldState)

        // Assert
        assert(mockFile.exists())
        val content = mockFile.readText()
        assert(content.isEmpty()) // Check that the file is created but is empty

        verify(mockSnackbarHostState).showSnackbar("File exported to ${mockFile.absolutePath}")
    }

    @Test
    fun `test consultation large content`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()
        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val mockFile = File(mockDirectory, "ClinicalConsultation.doc")

        if (mockFile.exists()) {
            mockFile.delete()
        }

        // Create a large string content
        val largeContent = "This is a large content. ".repeat(10_000) // 10,000 repetitions

        // Act
        exportToDoc(mockContext, largeContent, mockScaffoldState)

        // Assert
        assert(mockFile.exists())
        val content = mockFile.readText()
        assert(content == largeContent)

        verify(mockSnackbarHostState).showSnackbar("File exported to ${mockFile.absolutePath}")
    }
}