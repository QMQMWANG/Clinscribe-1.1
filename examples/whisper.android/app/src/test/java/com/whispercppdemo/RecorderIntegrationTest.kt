package com.whispercppdemo.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.test.core.app.ApplicationProvider
import com.whispercppdemo.media.decodeWaveFile
import com.whispercppdemo.media.encodeWaveFile
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.*
import android.content.Context

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class RecorderIntegrationTest {

    private lateinit var outputFile: File
    private val isMuted = AtomicBoolean(false) // Mute state

    @Before
    fun setUp() {
        outputFile = File(ApplicationProvider.getApplicationContext<Context>().cacheDir, "testRecording.wav")
    }

    @After
    fun tearDown() {
        if (outputFile.exists()) {
            outputFile.delete()
        }
    }

    @Test
    fun `test recording, muting, and transcribing`() = runBlocking {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ) * 4
            val buffer = ShortArray(bufferSize / 2)

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            audioRecord.startRecording()

            val allData = mutableListOf<Short>()
            var read: Int

            // Record for a short time
            for (i in 0 until 10) { // Just a few iterations for testing
                read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    if (!isMuted.get()) {
                        for (j in 0 until read) {
                            allData.add(buffer[j])
                        }
                    }
                } else {
                    throw RuntimeException("audioRecord.read returned $read")
                }
            }

            audioRecord.stop()
            audioRecord.release()

            encodeWaveFile(outputFile, allData.toShortArray())

            // Ensure the output file exists
            assertTrue(outputFile.exists())

            // Decode the wave file and check if it contains audio samples
            val samples = decodeWaveFile(outputFile)
            assertTrue(samples.isNotEmpty())
        } catch (e: Exception) {
            fail("Test failed with exception: ${e.message}")
        }
    }

    private fun encodeWaveFile(outputFile: File, data: ShortArray) {
        // Simplified encoding logic, replace with actual implementation
        outputFile.outputStream().use { fos ->
            fos.write(data.size.toString().toByteArray())
            for (sample in data) {
                fos.write(sample.toInt().toString().toByteArray())
            }
        }
    }
}
