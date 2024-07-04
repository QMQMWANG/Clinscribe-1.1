package com.whispercppdemo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import fi.iki.elonen.NanoHTTPD
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DatabaseServerTest {

    private lateinit var server: DatabaseServer
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()  // Use a real context with Robolectric
        server = DatabaseServer(context, 8080)
    }

    @After
    fun tearDown() {
        server.stop()
    }

    @Test
    fun `test export database to csv`() {
        // Simulate a request to the "/csv" endpoint
        val session = mock(NanoHTTPD.IHTTPSession::class.java)
        Mockito.`when`(session.uri).thenReturn("/csv")

        // Call the server's serve method
        val response = server.serve(session)

        // Check that the response is not null
        assertNotNull(response)

        // Check that the response status is OK
        assertEquals(NanoHTTPD.Response.Status.OK, response.status)

        // Check the content type is CSV
        assertEquals("text/csv", response.mimeType)

        // Check the content is not empty (assuming your database has some records)
        val outputStream = ByteArrayOutputStream()
        val inputStream: InputStream = response.data
        inputStream.copyTo(outputStream)
        val csvContent = outputStream.toString("UTF-8")

        // Assert that the CSV content has been generated
        assert(csvContent.isNotEmpty())

        // Further checks could include validating the actual CSV format and data if needed
    }

}


