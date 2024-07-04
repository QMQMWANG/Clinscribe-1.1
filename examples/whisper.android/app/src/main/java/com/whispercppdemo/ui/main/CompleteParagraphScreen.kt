package com.whispercppdemo.ui.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.whispercppdemo.data.FhirRecordDao
import com.whispercppdemo.ui.theme.blue
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.livedata.observeAsState
import android.os.Environment
import androidx.compose.material.icons.filled.Save
import java.io.File
import java.io.FileOutputStream

@Composable
fun CompleteParagraphScreen(
    navController: NavHostController,
    viewModel: MainScreenViewModel
) {
    val completeParagraphText by viewModel.completeParagraphText.observeAsState("")
    val navigateFromFhirScreen by viewModel.navigateFromFhirScreen.observeAsState(false)
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Clinical Summary") },
                backgroundColor = blue,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = {
                        if (navigateFromFhirScreen) {
                            navController.navigate("main_screen") {
                                popUpTo("main_screen") { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            exportToDocClinicalSummary(context, completeParagraphText, scaffoldState)
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Export")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                TextField(
                    value = completeParagraphText,
                    onValueChange = { newText ->
                        viewModel.setCompleteParagraphText(newText)
                    },
                    label = { Text("Clinical Summary") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

 suspend fun exportToDocClinicalSummary(context: Context, text: String, scaffoldState: ScaffoldState) {
    val baseFileName = "ClinicalSummary"
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