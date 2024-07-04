package com.whispercppdemo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.input.TextFieldValue
import com.whispercppdemo.data.FhirRecordDao
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.whispercppdemo.ui.theme.blue

@Composable
fun RecordDetailScreen(navController: NavHostController, record: String) {
    val context = LocalContext.current
    val dao = FhirRecordDao(context)
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var textFieldValue by remember { mutableStateOf(TextFieldValue(record)) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Record Detail") },
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
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newValue -> textFieldValue = newValue },
                    label = { Text("FHIR Record") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dao.deleteRecord(record)
                                scaffoldState.snackbarHostState.showSnackbar("Record deleted")
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = blue),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dao.updateRecord(record, textFieldValue.text)
                                scaffoldState.snackbarHostState.showSnackbar("Record updated")
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = blue),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    )
}