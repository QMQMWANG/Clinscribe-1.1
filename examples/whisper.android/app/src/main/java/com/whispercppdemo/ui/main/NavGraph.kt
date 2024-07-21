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
@Composable
fun MyApp(viewModel: MainScreenViewModel) {
    val navController = rememberNavController()
    val fhirResponse by viewModel.navigateToFhirScreen.observeAsState()

    fhirResponse?.let { response ->
        Log.d("MyApp", "Navigating to FHIR Records Screen with response: $response")
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
                    Log.d("MyApp", "Navigating to FHIR Records Screen without response")
                    if (viewModel.fhirRecord.isNotEmpty()) {
                        navController.navigate("fhir_records_screen/${Uri.encode(viewModel.fhirRecord)}")
                    } else {
                        navController.navigate("fhir_records_screen/empty")
                    }
                },
                onSignOut = { navController.navigate("auth_screen") }
            )
        }
        composable("fhir_records_screen/{fhirResponse}") { backStackEntry ->
            val fhirResponse = backStackEntry.arguments?.getString("fhirResponse") ?: ""
            Log.d("MyApp", "Opening FHIR Records Screen with response: $fhirResponse")
            FHIRRecordsScreen(fhirResponse, navController)
        }
    }
}

@Composable
fun FHIRRecordsScreen(fhirResponse: String, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FHIR Records") },
                backgroundColor = blue,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Text(
                text = fhirResponse,
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            )
        }
    )
}
