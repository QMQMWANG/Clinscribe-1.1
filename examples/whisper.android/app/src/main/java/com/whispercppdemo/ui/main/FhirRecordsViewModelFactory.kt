//package com.whispercppdemo.ui.main
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.whispercppdemo.data.FhirRecordDao
//
//class FhirRecordsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(FhirRecordsViewModel::class.java)) {
//            val dao = FhirRecordDao(context)
//            @Suppress("UNCHECKED_CAST")
//            return FhirRecordsViewModel(dao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
