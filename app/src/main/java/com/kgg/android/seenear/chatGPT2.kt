package com.kgg.android.seenear
//
//import android.app.Application
//import android.renderscript.Element
//import android.widget.MultiAutoCompleteTextView
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.channels.FileChannel
//import org.tensorflow.lite.Interpreter
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
//import java.io.FileInputStream
//import java.nio.channels.Channels

//class chatGPT2  : Application() {
//
//
//    // Prepare input data
//    val input = prepareInputData("Hello, how are you?")
//
//    // Allocate output buffer
//    val outputSize = 1024 // Set the maximum length of the output
//    val outputBuffer = ByteBuffer.allocateDirect(outputSize * 4).order(ByteOrder.nativeOrder()) // 4 bytes per float
//
//    // Run inference
//    // Load the TensorFlow Lite model
//    val model = Interpreter(loadModelFile()).run(input, outputBuffer)
//
//    // Convert output to text
//    val output = readOutputData(outputBuffer)
//
//    // Helper function to load the TensorFlow Lite model file
//    fun loadModelFile(): ByteBuffer {
//        val fileDescriptor = assets.openFd("model.tflite")
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//
//    // Helper function to prepare input data
//    fun prepareInputData(text: String, tokenizer: MultiAutoCompleteTextView.Tokenizer): ByteBuffer {
//        val inputIds = tokenizer.encode(text).toTypedArray()
//        val inputBuffer = ByteBuffer.allocateDirect(inputIds.size * 4) // 4 bytes per int
//        inputBuffer.order(ByteOrder.nativeOrder())
//        inputIds.forEach { inputBuffer.putInt(it) }
//        return inputBuffer
//    }
//
//    // Helper function to read output data
//    fun readOutputData(outputBuffer: ByteBuffer): String {
//        outputBuffer.rewind()
//        val floatArray = FloatArray(outputBuffer.remaining() / 4)
//        outputBuffer.asFloatBuffer().get(floatArray)
//        val sb = StringBuilder()
//        for (value in floatArray) {
//            sb.append(value)
//            sb.append(" ")
//        }
//        return sb.toString()
//    }
//
//
//}