package com.whispercppdemo.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.whispercppdemo.ui.theme.blue
import androidx.compose.material.ButtonDefaults
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material.TextButton
import com.whispercppdemo.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.*
@Composable
fun AuthScreen(viewModel: AuthViewModel = viewModel(), onAuthSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var registrationSuccessful by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.nhslogo),
            contentDescription = "NHS Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isBlank()) {
                    dialogMessage = "Wrong or Invalid e-mail address. Please correct and try again."
                    showDialog = true
                } else if (isLogin && password.isBlank()) {
                    dialogMessage = "Please enter a password"
                    showDialog = true
                } else {
                    if (isLogin) {
                        viewModel.login(email, password, {
                            dialogMessage = "Login successful"
                            showDialog = true
                            onAuthSuccess()
                        }, {
                            dialogMessage = it
                            showDialog = true
                        })
                    } else {
                        viewModel.register(email, password, {
                            dialogMessage = "Registration successful. Please log in with your new account."
                            showDialog = true
                            registrationSuccessful = true
                        }, {
                            dialogMessage = it
                            showDialog = true
                        })
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White)
        ) {
            Text(if (isLogin) "Login" else "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = { isLogin = !isLogin },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (isLogin) "Don't have an account? Register" else "Already have an account? Login", style = TextStyle(color = blue))
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isLogin) {
            TextButton(
                onClick = {
                    if (email.isBlank()) {
                        dialogMessage = "Please enter an email"
                        showDialog = true
                    } else {
                        viewModel.resetPassword(email, {
                            dialogMessage = "Password reset email sent"
                            showDialog = true
                        }, {
                            dialogMessage = "Password reset failed"
                            showDialog = true
                        })
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Forgot Password?", style = TextStyle(color = blue))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = if (registrationSuccessful) "Success" else "Warning") },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    if (registrationSuccessful) {
                        registrationSuccessful = false
                        isLogin = true
                    }
                }) {
                    Text("OK")
                }
            }
        )
    }
}