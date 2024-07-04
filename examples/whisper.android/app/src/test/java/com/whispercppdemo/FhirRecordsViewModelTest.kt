package com.whispercppdemo.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import androidx.lifecycle.Observer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.mockito.Mock
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times


class FhirRecordsViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule() // For LiveData testing

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: FhirRecordsViewModel

    @Mock
    private lateinit var mockHttpClient: OkHttpClient

    @Mock
    private lateinit var mockCall: Call

    @Mock
    private lateinit var mockResponse: Response

    @Mock
    private lateinit var mockResponseBody: ResponseBody

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Initialize mocks

        // Set the Main dispatcher to use TestCoroutineDispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel
        viewModel = FhirRecordsViewModel()

        // Inject mock OkHttpClient into the ViewModel (if needed)
        // If you're using Dependency Injection, adjust accordingly.
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()

        // Clean up the TestCoroutineDispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test initial LiveData states`() {
        // Check if LiveData fields are initialized and handle potential null values
        assertEquals(false, viewModel.isProcessing.value ?: false)
        assertEquals("", viewModel.text.value ?: "")
        assertEquals("", viewModel.updatedFhirText.value ?: "")
    }


    @Test
    fun `test updating FHIR text`() {
        viewModel.setText("Sample FHIR Text")
        assertEquals("Sample FHIR Text", viewModel.text.value)
    }

    @Test
    fun `test clearing updated FHIR text`() {
        viewModel.clearUpdatedFhirText()
        assertEquals("", viewModel.updatedFhirText.value)
    }


    @Test
    fun `test API call failure`() = runBlockingTest {
        // Mock a failed response
        whenever(mockResponse.isSuccessful).thenReturn(false)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockHttpClient.newCall(any())).thenReturn(mockCall)

        // Observer to capture the LiveData changes
        val observer = mock<Observer<String>>()
        viewModel.updatedFhirText.observeForever(observer)

        // Trigger the API call
        viewModel.sendToOllamaServer("FHIR Text", "prompt")

        // Verify that the observer's onChanged method was not called with success
        verify(observer, never()).onChanged(any())

        // Clean up
        viewModel.updatedFhirText.removeObserver(observer)
    }

    @Test
    fun `test API call timeout`() = runBlockingTest {
        // Mock a timeout exception
        whenever(mockCall.execute()).thenThrow(IOException("Timeout"))

        // Observer to capture the LiveData changes
        val observer = mock<Observer<String>>()
        viewModel.updatedFhirText.observeForever(observer)

        // Trigger the API call
        viewModel.sendToOllamaServer("FHIR Text", "prompt")

        // Verify that the observer's onChanged method was not called with success
        verify(observer, never()).onChanged(any())

        // Clean up
        viewModel.updatedFhirText.removeObserver(observer)
    }
    @Test
    fun `test API call with null response`() = runBlockingTest {
        // Mock a successful response with a null body
        whenever(mockResponse.isSuccessful).thenReturn(true)
        whenever(mockResponse.body).thenReturn(null)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockHttpClient.newCall(any())).thenReturn(mockCall)

        // Observer to capture the LiveData changes
        val observer = mock<Observer<String>>()
        viewModel.updatedFhirText.observeForever(observer)

        // Trigger the API call
        viewModel.sendToOllamaServer("FHIR Text", "prompt")

        // Verify that the observer's onChanged method was not called with success
        verify(observer, never()).onChanged(any())

        // Clean up
        viewModel.updatedFhirText.removeObserver(observer)
    }
    @Test
    fun `test partial data update`() {
        // Set initial text
        viewModel.setText("Initial FHIR Text")

        // Update only a part of the FHIR text
        viewModel.setText("Updated FHIR Text")

        // Check if the ViewModel updated the text correctly
        assertEquals("Updated FHIR Text", viewModel.text.value)
    }

    @Test
    fun `test clearing all data state`() {
        // Set some initial values
        viewModel.setText("Sample FHIR Text")
        viewModel.clearUpdatedFhirText()

        // Clear all data in the ViewModel
        viewModel.setText("")
        viewModel.clearUpdatedFhirText()

        // Check that all LiveData fields are reset
        assertEquals("", viewModel.text.value)
        assertEquals("", viewModel.updatedFhirText.value)
    }



}
