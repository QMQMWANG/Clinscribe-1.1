package com.whispercppdemo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.whispercppdemo.data.FhirRecordDao
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.whispercppdemo.ui.theme.blue
import androidx.compose.foundation.clickable
import android.net.Uri
import androidx.compose.material.icons.filled.Search


@Composable
fun ManageFhirScreen(navController: NavHostController, viewModel: MainScreenViewModel) {
    val context = LocalContext.current
    val dao = FhirRecordDao(context)
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var records by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        records = dao.getAllRecords()
    }

    LaunchedEffect(searchText) {
        records = if (searchText.isEmpty()) {
            dao.getAllRecords()
        } else {
            dao.getFilteredRecords(searchText)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Manage FHIR Records") },
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { newText -> searchText = newText },
                    label = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                    trailingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = blue, thickness = 2.dp)
                Text(
                    "Search Results:",
                    style = TextStyle(fontSize = 18.sp, color = Color.Gray),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                records.forEachIndexed { index, record ->
                    val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFdee8fa)
                    val truncatedRecord = record.split(".").firstOrNull() ?: record
                    Card(
                        backgroundColor = backgroundColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate("record_detail_screen/${Uri.encode(record)}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(truncatedRecord, style = TextStyle(fontSize = 16.sp))
                        }
                    }
                }
            }
        }
    )
}