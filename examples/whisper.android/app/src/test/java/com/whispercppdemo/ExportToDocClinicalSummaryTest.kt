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


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ExportToDocClinicalSummaryTest {

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
    fun `test export clinical summary success`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()

        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        // Simulate the environment and file creation
        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val mockFile = File(mockDirectory, "ClinicalSummary.doc")

        // Ensure the directory and file are clean before the test
        if (mockFile.exists()) {
            mockFile.delete()
        }

        // Act
        exportToDocClinicalSummary(mockContext, "Clinical Summary Content", mockScaffoldState)

        // Assert
        assert(mockFile.exists()) // Check if the file was created
        val content = mockFile.readText() // Check the content of the file
        assert(content == "Clinical Summary Content")

        // Verify Snackbar is shown with success message
        verify(mockSnackbarHostState).showSnackbar("File exported to ${mockFile.absolutePath}")
    }
    @Test
    fun `test export clinical summary empty content`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()

        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        // Simulate the environment and file creation
        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val mockFile = File(mockDirectory, "ClinicalSummary.doc")

        // Ensure the directory and file are clean before the test
        if (mockFile.exists()) {
            mockFile.delete()
        }

        // Act
        exportToDocClinicalSummary(mockContext, "", mockScaffoldState)

        // Assert
        assert(mockFile.exists()) // Check if the file was created
        val content = mockFile.readText() // Check the content of the file
        assert(content.isEmpty()) // Content should be empty

        // Verify Snackbar is shown with success message
        verify(mockSnackbarHostState).showSnackbar("File exported to ${mockFile.absolutePath}")
    }


    @Test
    fun `test export clinical summary large content`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()

        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        // Simulate the environment and file creation
        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val mockFile = File(mockDirectory, "ClinicalSummary.doc")

        // Generate a large string
        val largeContent = "A".repeat(1000000) // 1 million characters

        // Act
        exportToDocClinicalSummary(mockContext, largeContent, mockScaffoldState)

        // Assert
        assert(mockFile.exists()) // Check if the file was created
        val content = mockFile.readText() // Check the content of the file
        assert(content == largeContent) // Content should match the large input

        // Verify Snackbar is shown with success message
        verify(mockSnackbarHostState).showSnackbar("File exported to ${mockFile.absolutePath}")
    }
    @Test
    fun `test exported summary file name conflict`() = runBlockingTest {
        // Arrange
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val mockScaffoldState = mock<ScaffoldState>()
        val mockSnackbarHostState = mock<SnackbarHostState>()
        `when`(mockScaffoldState.snackbarHostState).thenReturn(mockSnackbarHostState)

        val mockDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val existingFile = File(mockDirectory, "ClinicalSummary.doc")

        // Simulate existing file with the same name
        existingFile.writeText("Existing Content")

        // Act
        exportToDocClinicalSummary(mockContext, "New Content", mockScaffoldState)

        // Assert
        // Check if the original file still exists and wasn't overwritten
        assert(existingFile.exists())
        assert(existingFile.readText() == "Existing Content")

        // Check if a new file was created with a different name
        val newFile = File(mockDirectory, "ClinicalSummary1.doc")
        assert(newFile.exists())
        assert(newFile.readText() == "New Content")

        // Verify Snackbar is shown with the correct path of the new file
        verify(mockSnackbarHostState).showSnackbar("File exported to ${newFile.absolutePath}")
    }

}
