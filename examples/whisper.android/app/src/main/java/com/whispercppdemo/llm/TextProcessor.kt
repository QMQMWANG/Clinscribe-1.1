//package com.whispercppdemo.llm
//
//import com.chaquo.python.PyObject
//import com.chaquo.python.Python
//
//object TextProcessor {
//    private val py: Python = Python.getInstance()
//    private val textProcessor: PyObject = py.getModule("llm_processor")
//
//    fun makeTextMoreDetailed(rawText: String): String {
//        val detailedText: PyObject = textProcessor.callAttr("process_text", rawText)
//        return detailedText.toString()
//    }
//}
