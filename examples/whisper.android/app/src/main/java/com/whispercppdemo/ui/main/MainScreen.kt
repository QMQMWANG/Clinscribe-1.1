package com.whispercppdemo.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.whispercppdemo.R
import com.whispercppdemo.ui.theme.blue
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    onFhirRecordsTapped: () -> Unit,
    onSignOut: () -> Unit
) {
    MainScreen(
        canTranscribe = viewModel.canTranscribe,
        isRecording = viewModel.isRecording,
        messageLog = viewModel.dataLog,
        status = viewModel.status,
        recordingTime = viewModel.recordingTime,
        onTranscribeSampleTapped = viewModel::transcribeSample,
        onRecordTapped = viewModel::toggleRecord,
        onFhirRecordsTapped = onFhirRecordsTapped,
        onSignOut = onSignOut
    )
}

@Composable
private fun MainScreen(
    canTranscribe: Boolean,
    isRecording: Boolean,
    messageLog: String,
    status: String,
    recordingTime: Long,
    onTranscribeSampleTapped: () -> Unit,
    onRecordTapped: () -> Unit,
    onFhirRecordsTapped: () -> Unit,
    onSignOut: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Whisper-offline") },
                backgroundColor = blue,
                contentColor = Color.White,
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onSignOut() // Call the sign-out callback
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nhslogo),
                    contentDescription = "NHS Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                StatusBar(status, recordingTime)
                Spacer(modifier = Modifier.height(16.dp))
//                TranscribeSampleButton(enabled = canTranscribe, onClick = onTranscribeSampleTapped)
                Spacer(modifier = Modifier.height(16.dp))
                RecordButton(
                    enabled = canTranscribe,
                    isRecording = isRecording,
                    onClick = onRecordTapped
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onFhirRecordsTapped,
                    colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White)
                ) {
                    Text("FHIR records")
                }
                Spacer(modifier = Modifier.height(16.dp))
                MessageLog(messageLog)
            }
        }
    )
}

@Composable
private fun StatusBar(status: String, recordingTime: Long) {
    Text(text = "Status: $status")
    if (status == "Recording...") {
        Text(text = "Recording Time: ${recordingTime / 1000} seconds")
    }
}

@Composable
private fun MessageLog(log: String) {
    SelectionContainer {
        Text(modifier = Modifier.verticalScroll(rememberScrollState()), text = log)
    }
}

//@Composable
//private fun TranscribeSampleButton(enabled: Boolean, onClick: () -> Unit) {
//    Button(
//        onClick = onClick,
//        enabled = enabled,
//        colors = ButtonDefaults.buttonColors(backgroundColor = blue,contentColor = Color.White)
//    ) {
//        Text("Transcribe sample")
//    }
//}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RecordButton(enabled: Boolean, isRecording: Boolean, onClick: () -> Unit) {
    val micPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.RECORD_AUDIO,
        onPermissionResult = { granted ->
            if (granted) {
                onClick()
            }
        }
    )
    Button(
        onClick = {
            if (micPermissionState.status.isGranted) {
                onClick()
            } else {
                micPermissionState.launchPermissionRequest()
            }
        },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(backgroundColor = blue,contentColor = Color.White)
    ) {
        Text(
            if (isRecording) {
                "Stop recording"
            } else {
                "Start recording"
            }
        )
    }
}
