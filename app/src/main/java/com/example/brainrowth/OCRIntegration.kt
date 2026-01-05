package com.example.brainrowth

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

fun recognizeTextFromBitmap(
    bitmap: Bitmap,
    onResult: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val resultText = visionText.text  // ini teks soal dari gambar
            onResult(resultText)
        }
        .addOnFailureListener { e ->
            onError(e)
        }
}
