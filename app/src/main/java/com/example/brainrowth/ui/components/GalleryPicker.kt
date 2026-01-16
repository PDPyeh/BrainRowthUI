package com.example.brainrowth.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun GalleryPicker(
    onImagePicked: (Bitmap) -> Unit,
    onError: (Exception) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        try {
            if (uri != null) {
                // Convert URI to Bitmap
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                if (bitmap != null) {
                    onImagePicked(bitmap)
                } else {
                    onError(Exception("Failed to decode image"))
                }
            }
        } catch (e: Exception) {
            onError(e)
        }
    }
    
    Button(
        onClick = { launcher.launch("image/*") },
        modifier = modifier
    ) {
        Text("Pick from Gallery")
    }
}
