package com.whispercppdemo.ui.auth

import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.whispercppdemo.ui.main.MainScreenViewModel
import com.whispercppdemo.ui.main.MyApp

@Composable
fun AuthNavigation(viewModel: MainScreenViewModel) {
    val auth = FirebaseAuth.getInstance()
    var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

    DisposableEffect(auth) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            isAuthenticated = firebaseAuth.currentUser != null
        }
        auth.addAuthStateListener(authStateListener)
        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    if (isAuthenticated) {
        MyApp(viewModel = viewModel)
    } else {
        AuthScreen(onAuthSuccess = { isAuthenticated = true })
    }
}