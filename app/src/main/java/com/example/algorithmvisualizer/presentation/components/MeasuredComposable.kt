package com.example.algorithmvisualizer.presentation.components

import android.os.Trace
import androidx.compose.runtime.Composable

//@Composable
//fun MeasuredComposable(content: @Composable () -> Unit) {
//    traceSection("MeasuredComposable") {
//        content()
//    }
//}
//
//inline fun <T> traceSection(tag: String, block: () -> T): T {
//    Trace.beginSection(tag)
//    return try {
//        block()
//    } finally {
//        Trace.endSection()
//    }
//}