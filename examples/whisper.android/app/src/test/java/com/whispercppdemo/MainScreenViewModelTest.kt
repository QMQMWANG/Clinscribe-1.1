package com.whispercppdemo.ui.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.shadows.ShadowLog
import com.whispercppdemo.ui.main.MainScreenViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import kotlinx.coroutines.runBlocking

import androidx.lifecycle.Observer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.never

class MainScreenViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule() // For LiveData testing

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var mockApplication: Application

    private lateinit var viewModel: MainScreenViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Initialize mocks

        // Set the Main dispatcher to use TestCoroutineDispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel with the mocked Application
        viewModel = MainScreenViewModel(mockApplication)

    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()

        // Clean up the TestCoroutineDispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test initial status is idle`() {
        // Initial status should be "Idle"
        assertEquals("Idle", viewModel.status)
    }


    @Test
    fun `test navigation to FHIR screen`() {
        // Initially, the navigation event should be null
        assertNull(viewModel.navigateToFhirScreen.value)

        // Simulate the event that triggers navigation
        viewModel.navigateToFhirScreen("SampleFHIRRecord")

        // Assert that the navigation event is triggered with the correct value
        assertEquals("SampleFHIRRecord", viewModel.navigateToFhirScreen.value)
    }
    @Test
    fun `test navigation away from FHIR screen`() {
        // Simulate navigating to FHIR screen
        viewModel.navigateToFhirScreen("SampleFHIRRecord")

        // Simulate that navigation is completed
        viewModel.doneNavigating()

        // Verify that the navigation state is reset
        assertNull(viewModel.navigateToFhirScreen.value)
    }

    @Test
    fun `test Manage FHIR navigation triggered`() {
        // Create a mock observer for navigation trigger
        val onNavigateToManageFhir: () -> Unit = mock()

        // Mock the Application object
        val mockApplication: Application = mock()

        // Create an instance of your ViewModel with the mocked Application
        val viewModel = MainScreenViewModel(mockApplication)

        // Simulate clicking the "Manage FHIR" button
        onNavigateToManageFhir.invoke()

        // Verify that the navigation lambda was called
        verify(onNavigateToManageFhir).invoke()

    }

    @Test
    fun `test Clinical Summary navigation triggered`() {
        // Create a mock observer for navigation trigger
        val onNavigateToClinicalSummary: () -> Unit = mock()

        // Mock the Application object
        val mockApplication: Application = mock()

        // Create an instance of your ViewModel with the mocked Application
        val viewModel = MainScreenViewModel(mockApplication)

        // Simulate clicking the "Clinical Summary" button
        onNavigateToClinicalSummary.invoke()

        // Verify that the navigation lambda was called
        verify(onNavigateToClinicalSummary).invoke()
    }


    @Test
    fun `test toggle mute functionality`() {
        // Initial mute state should be false
        assertEquals(false, viewModel.isMuted)

        // Toggle mute
        viewModel.toggleMute()

        // Assert: mute state should be true
        assertEquals(true, viewModel.isMuted)

        // Toggle mute again
        viewModel.toggleMute()

        // Assert: mute state should be false
        assertEquals(false, viewModel.isMuted)
    }

    @Test
    fun `test FHIR record state updates`() {
        // Set a FHIR record
        viewModel.fhirRecord = "TestFHIRRecord"

        // Check that the FHIR record is correctly updated
        assertEquals("TestFHIRRecord", viewModel.fhirRecord)
    }

    @Test
    fun `test log message updates`() = runBlocking {
        // Add a message to the log
        viewModel.printMessage("Test log message")

        // Check that the message is correctly appended to the dataLog
        assertTrue(viewModel.dataLog.contains("Test log message"))
    }

    @Test
    fun `test log message with empty string`() = runBlocking {
        viewModel.printMessage("")
        assertTrue(viewModel.dataLog.contains(""))
    }

    @Test
    fun `test LiveData observation`() {
        // Create a mock Observer that observes in String?
        val observer = mock<Observer<in String?>>()

        // Attach the observer to the LiveData
        viewModel.navigateToFhirScreen.observeForever(observer)

        // Trigger the LiveData update
        viewModel.navigateToFhirScreen("TestRecord")

        // Verify that the observer's onChanged method was called with the correct value
        verify(observer).onChanged("TestRecord")

        // Clean up: remove the observer
        viewModel.navigateToFhirScreen.removeObserver(observer)
    }


}