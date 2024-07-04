package com.whispercppdemo.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whispercppdemo.ui.theme.blue
import com.whispercppdemo.ui.auth.AuthScreen
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import com.whispercppdemo.data.FhirRecordDao
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
@Composable
fun MyApp(viewModel: MainScreenViewModel) {
    val navController = rememberNavController()
    val fhirResponse by viewModel.navigateToFhirScreen.observeAsState()
    val context = LocalContext.current

    fhirResponse?.let { response ->
        try {
            navController.navigate("fhir_records_screen/${Uri.encode(response)}")
            viewModel.doneNavigating()
        } catch (e: Exception) {
            Log.e("MyApp", "Navigation error: ${e.message}")
        }
    }

    NavHost(navController = navController, startDestination = "auth_screen") {
        composable("auth_screen") {
            AuthScreen(onAuthSuccess = { navController.navigate("main_screen") })
        }
        composable("main_screen") {
            MainScreen(
                viewModel = viewModel,
                onFhirRecordsTapped = {
                    if (viewModel.fhirRecord.isNotEmpty()) {
                        navController.navigate("fhir_records_screen/${Uri.encode(viewModel.fhirRecord)}")
                    } else {
                        navController.navigate("fhir_records_screen/empty")
                    }
                },
                onSignOut = { navController.navigate("auth_screen") },
                onNavigateToCompleteParagraph = { navController.navigate("complete_paragraph_screen/empty") },
                onNavigateToManageFhir = { navController.navigate("manage_fhir_screen") } // Add navigation for Manage FHIR
            )
        }
        composable("fhir_records_screen/{fhirResponse}") { backStackEntry ->
            val fhirResponse = backStackEntry.arguments?.getString("fhirResponse") ?: ""
            val fhirViewModel: FhirRecordsViewModel = viewModel()
            FHIRRecordsScreen(fhirResponse, navController, fhirViewModel, viewModel, context)
        }
        composable("complete_paragraph_screen/{paragraphText}") { backStackEntry ->
            val paragraphText = backStackEntry.arguments?.getString("paragraphText") ?: ""
            CompleteParagraphScreen(navController = navController, viewModel = viewModel)
        }
        composable("manage_fhir_screen") {
            ManageFhirScreen(navController, viewModel) // Add the Manage FHIR screen
        }
        composable("record_detail_screen/{record}") { backStackEntry ->
            val record = backStackEntry.arguments?.getString("record") ?: ""
            RecordDetailScreen(navController = navController, record = record)
        }
    }
}


@Composable
fun FHIRRecordsScreen(
    fhirResponse: String,
    navController: NavHostController,
    fhirViewModel: FhirRecordsViewModel = viewModel(),
    mainViewModel: MainScreenViewModel,
    context: Context
) {
    var localText by remember { mutableStateOf(if (fhirResponse != "empty") fhirResponse else "") }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val dao = FhirRecordDao(context)

    val isProcessing by fhirViewModel.isProcessing.observeAsState(false)

    // Observe ViewModel text and update local state when it changes
    val viewModelText by fhirViewModel.text.observeAsState(localText)
    LaunchedEffect(viewModelText) {
        localText = viewModelText
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("FHIR Records") },
                backgroundColor = blue,
                contentColor = Color.White,
                actions = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dao.insert(localText)
                                scaffoldState.snackbarHostState.showSnackbar("Stored in Database")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Store", color = blue)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    value = localText,
                    onValueChange = { newText ->
                        localText = newText
                    },
                    label = { Text("FHIR Record") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            fhirViewModel.sendToOllamaServer(localText, "help me make the json format to be a clinical summary that is in complete paragraph, not include any json format here, Just show the complete paragraph.")
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = blue),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Convert into Clinical Summary", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isProcessing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )


    val updatedFhirText by fhirViewModel.updatedFhirText.observeAsState("")
    if (updatedFhirText.isNotEmpty()) {
        LaunchedEffect(updatedFhirText) {
            mainViewModel.setCompleteParagraphText(updatedFhirText)
            mainViewModel.setNavigateFromFhirScreen(true)
            navController.navigate("complete_paragraph_screen/${Uri.encode(updatedFhirText)}")
            fhirViewModel.clearUpdatedFhirText()
        }
    }
}
