package com.whispercppdemo.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.livedata.observeAsState
import android.os.Environment
import androidx.compose.material.icons.filled.Save
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    onFhirRecordsTapped: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToCompleteParagraph: () -> Unit, //
    onNavigateToManageFhir: () -> Unit
) {
    MainScreen(
        canTranscribe = viewModel.canTranscribe,
        isRecording = viewModel.isRecording,
        isMuted = viewModel.isMuted,
        messageLog = viewModel.dataLog,
        status = viewModel.status,
        recordingTime = viewModel.recordingTime,
        onTranscribeSampleTapped = viewModel::transcribeSample,
        onRecordTapped = viewModel::toggleRecord,
        onMuteTapped = viewModel::toggleMute,
        onFhirRecordsTapped = onFhirRecordsTapped,
        onSignOut = onSignOut,
        onNavigateToCompleteParagraph = onNavigateToCompleteParagraph,
        onNavigateToManageFhir = onNavigateToManageFhir
    )
}

@Composable
private fun MainScreen(
    canTranscribe: Boolean,
    isRecording: Boolean,
    isMuted: Boolean,
    messageLog: String,
    status: String,
    recordingTime: Long,
    onTranscribeSampleTapped: () -> Unit,
    onRecordTapped: () -> Unit,
    onMuteTapped: () -> Unit,
    onFhirRecordsTapped: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToCompleteParagraph: () -> Unit,
    onNavigateToManageFhir: () -> Unit
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = scaffoldState.snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Clinsribe 1.1") },
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
                // Status Bar with Smaller Box
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(horizontal = 30.dp),
                    shape = RoundedCornerShape(50.dp),
                    backgroundColor = Color.White,
                    elevation = 4.dp,
                    border = BorderStroke(2.dp, blue)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        StatusBar(status, recordingTime)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // First row: Start Recording and Mute Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RecordButton(
                        enabled = canTranscribe,
                        isRecording = isRecording,
                        onClick = onRecordTapped,
                        modifier = Modifier.weight(1f) // Equal width
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    MuteButton(
                        isMuted = isMuted,
                        onClick = onMuteTapped,
                        modifier = Modifier.weight(1f) // Equal width
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Second row: FHIR Records and Manage FHIR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onFhirRecordsTapped,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = blue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f) // Equal width
                    ) {
                        Text("FHIR Records")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onNavigateToManageFhir, // Add the onClick handler for Manage FHIR
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = blue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f) // Equal width
                    ) {
                        Text("Manage FHIR")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Third row: Clinical Summary
                Button(
                    onClick = onNavigateToCompleteParagraph, // Add the onClick handler
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = blue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth() // Make this button fill the width of the row
                ) {
                    Text("Clinical Summary")
                }
                Spacer(modifier = Modifier.height(16.dp))

                TranscriptionBox(
                    log = messageLog,
                    onExportClick = {
                        coroutineScope.launch {
                            exportToDoc(context, messageLog, scaffoldState)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    )
}


@Composable
private fun StatusBar(status: String, recordingTime: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Status: $status")
            if (status == "Transcribing..." || status == "Converting...") {
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        if (status == "Recording...") {
            Text(text = "Recording Time: ${recordingTime / 1000} seconds")
        }
    }
}

//@Composable
//private fun MessageLog(log: String) {
//    SelectionContainer {
//        Text(modifier = Modifier.verticalScroll(rememberScrollState()), text = log)
//    }
//}

@Composable
private fun TranscriptionBox(log: String, onExportClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        elevation = 4.dp,
        border = BorderStroke(1.dp, blue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transcribed Text:",
                    style = MaterialTheme.typography.h6,
                    color = blue,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onExportClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Export")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            SelectionContainer(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = log,
                    style = MaterialTheme.typography.body1,
                    color = Color.Black
                )
            }
        }
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
private fun RecordButton(
    enabled: Boolean,
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Ensure modifier is defined with a default value
) {
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
        colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White),
        shape = RoundedCornerShape(50),
        modifier = modifier // Ensure this is correctly passed
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

@Composable
private fun MuteButton(
    isMuted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Ensure modifier is defined with a default value
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = if (isMuted) Color.Red else blue, contentColor = Color.White),
        shape = RoundedCornerShape(50),
        modifier = modifier // Ensure this is correctly passed
    ) {
        Text(
            if (isMuted) {
                "Unmute"
            } else {
                "Mute"
            }
        )
    }
}

 suspend fun exportToDoc(context: Context, text: String, scaffoldState: ScaffoldState) {
    val baseFileName = "ClinicalConsultation"
    val fileExtension = ".doc"
    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

    if (directory != null && !directory.exists()) {
        directory.mkdirs()
    }

    var file = File(directory, "$baseFileName$fileExtension")
    var fileIndex = 1

    // Check if the file already exists and create a new file name if necessary
    while (file.exists()) {
        file = File(directory, "$baseFileName$fileIndex$fileExtension")
        fileIndex++
    }

    try {
        FileOutputStream(file).use { fos ->
            fos.write(text.toByteArray())
            fos.flush()
        }
        scaffoldState.snackbarHostState.showSnackbar("File exported to ${file.absolutePath}")
    } catch (e: Exception) {
        scaffoldState.snackbarHostState.showSnackbar("Export failed: ${e.message}")
    }
}