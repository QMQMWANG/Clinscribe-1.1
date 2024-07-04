package com.whispercppdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.whispercppdemo.ui.main.MainScreenViewModel
import com.whispercppdemo.ui.main.MyApp
import com.whispercppdemo.ui.theme.WhisperCppDemoTheme
import com.google.firebase.auth.FirebaseAuth
import com.whispercppdemo.ui.auth.AuthScreen

class MainActivity : ComponentActivity() {
    private val viewModel: MainScreenViewModel by viewModels { MainScreenViewModel.factory() }
    private lateinit var server: DatabaseServer
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhisperCppDemoTheme {
                val auth = FirebaseAuth.getInstance()
                if (auth.currentUser != null) {
                    MyApp(viewModel = viewModel)
                } else {
                    AuthScreen(onAuthSuccess = { recreate() })
                }
            }
        }

        // Start the HTTP server
        server = DatabaseServer(this, 8080)
        server.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}