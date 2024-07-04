package com.whispercppdemo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class FhirRecordsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    private val _updatedFhirText = MutableLiveData<String>()
    val updatedFhirText: LiveData<String> = _updatedFhirText


    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> = _isProcessing

    fun setText(newText: String) {
        _text.value = newText
    }

    fun clearUpdatedFhirText() {
        _updatedFhirText.value = ""
    }

    fun sendToOllamaServer(fhirText: String, prompt: String) {
        _isProcessing.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder().build()
                val json = JSONObject()
                json.put("text", fhirText)
                json.put("prompt", prompt)
                val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/convert") // Replace with your actual server URL
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    _updatedFhirText.postValue(responseBody)
                } else {
                    println("Failed to call Ollama server: ${response.message}")
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            } finally {
                _isProcessing.postValue(false)  // End processing
            }
        }
    }
}