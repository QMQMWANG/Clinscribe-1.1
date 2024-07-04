package com.whispercppdemo.recorder

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test


class RecorderMuteFunctionalityTest {

    @Test
    fun `test mute and unmute`() = runBlocking {
        // Create an instance of the Recorder
        val recorder = Recorder()

        // Initially, the recorder should not be muted
        assertTrue(!recorder.isMuted.get())  // Use .get() to access AtomicBoolean value

        // Mute the recorder
        recorder.setMuted(true)

        // The recorder should now be muted
        assertTrue(recorder.isMuted.get())  // Use .get() to access AtomicBoolean value

        // Unmute the recorder
        recorder.setMuted(false)

        // The recorder should not be muted anymore
        assertTrue(!recorder.isMuted.get())  // Use .get() to access AtomicBoolean value
    }


}

