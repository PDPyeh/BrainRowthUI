package com.example.brainrowth.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ImageCropper(
    bitmap: Bitmap,
    onCropConfirmed: (Bitmap) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cropRect by remember {
        mutableStateOf(
            Rect(
                offset = Offset(50f, 50f),
                size = Size(
                    (bitmap.width - 100).toFloat().coerceAtLeast(100f),
                    (bitmap.height - 100).toFloat().coerceAtLeast(100f)
                )
            )
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Crop Image",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Original image
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Image to crop",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Crop overlay
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            cropRect = cropRect.translate(dragAmount)
                        }
                    }
            ) {
                // Draw semi-transparent overlay
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    size = size
                )

                // Clear crop area
                drawRect(
                    color = Color.Transparent,
                    topLeft = cropRect.topLeft,
                    size = cropRect.size
                )

                // Draw crop border
                drawRect(
                    color = Color.White,
                    topLeft = cropRect.topLeft,
                    size = cropRect.size,
                    style = Stroke(width = 3f)
                )

                // Draw corner handles
                val handleSize = 30f
                drawCircle(
                    color = Color.White,
                    radius = handleSize / 2,
                    center = cropRect.topLeft
                )
                drawCircle(
                    color = Color.White,
                    radius = handleSize / 2,
                    center = Offset(cropRect.right, cropRect.top)
                )
                drawCircle(
                    color = Color.White,
                    radius = handleSize / 2,
                    center = Offset(cropRect.left, cropRect.bottom)
                )
                drawCircle(
                    color = Color.White,
                    radius = handleSize / 2,
                    center = Offset(cropRect.right, cropRect.bottom)
                )
            }
        }

        // Instructions
        Text(
            "Drag to adjust crop area",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(8.dp)
        )

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    // Crop the bitmap
                    val croppedBitmap = cropBitmap(bitmap, cropRect)
                    onCropConfirmed(croppedBitmap)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirm & Solve")
            }
        }
    }
}

private fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
    val x = rect.left.toInt().coerceIn(0, bitmap.width - 1)
    val y = rect.top.toInt().coerceIn(0, bitmap.height - 1)
    val width = rect.width.toInt().coerceAtMost(bitmap.width - x)
    val height = rect.height.toInt().coerceAtMost(bitmap.height - y)
    
    return Bitmap.createBitmap(
        bitmap,
        x,
        y,
        width.coerceAtLeast(1),
        height.coerceAtLeast(1)
    )
}
