package com.whispercppdemo.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Login successful")
                    onSuccess()
                } else {
                    Log.w("AuthViewModel", "Login failed", task.exception)
                    onError("Your password is incorrect")
                }
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf("email" to email)

                    Log.d("AuthViewModel", "User created in FirebaseAuth successfully, now adding to Firestore")

                    db.collection("users").document(user!!.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d("AuthViewModel", "User registered successfully in Firestore")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.w("AuthViewModel", "Error adding user to Firestore", e)
                            onError("Error adding user to Firestore: ${e.message}")
                        }
                } else {
                    Log.w("AuthViewModel", "Registration failed", task.exception)
                    onError("Registration failed: ${task.exception?.message}")
                }
            }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Password reset email sent")
                    onSuccess()
                } else {
                    Log.w("AuthViewModel", "Password reset failed", task.exception)
                    onError("Password reset failed")
                }
            }
    }
}
