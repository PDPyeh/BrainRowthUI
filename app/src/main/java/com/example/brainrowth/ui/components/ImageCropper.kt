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
import kotlin.math.abs

enum class DragMode {
    NONE, MOVE, RESIZE_TL, RESIZE_TR, RESIZE_BL, RESIZE_BR
}

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
    
    var dragMode by remember { mutableStateOf(DragMode.NONE) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

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

            // Crop overlay with resize handles
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val handleSize = 40f
                                dragMode = when {
                                    // Top-left corner
                                    abs(offset.x - cropRect.left) < handleSize && 
                                    abs(offset.y - cropRect.top) < handleSize -> DragMode.RESIZE_TL
                                    // Top-right corner
                                    abs(offset.x - cropRect.right) < handleSize && 
                                    abs(offset.y - cropRect.top) < handleSize -> DragMode.RESIZE_TR
                                    // Bottom-left corner
                                    abs(offset.x - cropRect.left) < handleSize && 
                                    abs(offset.y - cropRect.bottom) < handleSize -> DragMode.RESIZE_BL
                                    // Bottom-right corner
                                    abs(offset.x - cropRect.right) < handleSize && 
                                    abs(offset.y - cropRect.bottom) < handleSize -> DragMode.RESIZE_BR
                                    // Inside rect - move
                                    offset.x >= cropRect.left && offset.x <= cropRect.right &&
                                    offset.y >= cropRect.top && offset.y <= cropRect.bottom -> DragMode.MOVE
                                    else -> DragMode.NONE
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                
                                if (dragMode == DragMode.NONE) return@detectDragGestures
                                
                                val minSize = 50f
                                val newRect = when (dragMode) {
                                    DragMode.MOVE -> {
                                        cropRect.translate(dragAmount)
                                    }
                                    DragMode.RESIZE_TL -> {
                                        val newLeft = (cropRect.left + dragAmount.x).coerceAtLeast(0f)
                                        val newTop = (cropRect.top + dragAmount.y).coerceAtLeast(0f)
                                        val newWidth = (cropRect.right - newLeft).coerceAtLeast(minSize)
                                        val newHeight = (cropRect.bottom - newTop).coerceAtLeast(minSize)
                                        Rect(
                                            offset = Offset(newLeft, newTop),
                                            size = Size(newWidth, newHeight)
                                        )
                                    }
                                    DragMode.RESIZE_TR -> {
                                        val newRight = (cropRect.right + dragAmount.x).coerceAtMost(size.width.toFloat())
                                        val newTop = (cropRect.top + dragAmount.y).coerceAtLeast(0f)
                                        val newWidth = (newRight - cropRect.left).coerceAtLeast(minSize)
                                        val newHeight = (cropRect.bottom - newTop).coerceAtLeast(minSize)
                                        Rect(
                                            offset = Offset(cropRect.left, newTop),
                                            size = Size(newWidth, newHeight)
                                        )
                                    }
                                    DragMode.RESIZE_BL -> {
                                        val newLeft = (cropRect.left + dragAmount.x).coerceAtLeast(0f)
                                        val newBottom = (cropRect.bottom + dragAmount.y).coerceAtMost(size.height.toFloat())
                                        val newWidth = (cropRect.right - newLeft).coerceAtLeast(minSize)
                                        val newHeight = (newBottom - cropRect.top).coerceAtLeast(minSize)
                                        Rect(
                                            offset = Offset(newLeft, cropRect.top),
                                            size = Size(newWidth, newHeight)
                                        )
                                    }
                                    DragMode.RESIZE_BR -> {
                                        val newRight = (cropRect.right + dragAmount.x).coerceAtMost(size.width.toFloat())
                                        val newBottom = (cropRect.bottom + dragAmount.y).coerceAtMost(size.height.toFloat())
                                        val newWidth = (newRight - cropRect.left).coerceAtLeast(minSize)
                                        val newHeight = (newBottom - cropRect.top).coerceAtLeast(minSize)
                                        Rect(
                                            offset = cropRect.topLeft,
                                            size = Size(newWidth, newHeight)
                                        )
                                    }
                                    else -> cropRect
                                }
                                
                                cropRect = newRect
                            },
                            onDragEnd = {
                                dragMode = DragMode.NONE
                            }
                        )
                    }
            ) {
                canvasSize = size
                
                // Draw semi-transparent overlay
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    size = size
                )

                // Clear crop area (transparent)
                drawRect(
                    color = Color.Transparent,
                    topLeft = cropRect.topLeft,
                    size = cropRect.size
                )

                // Draw crop border
                drawRect(
                    color = Color.Cyan,
                    topLeft = cropRect.topLeft,
                    size = cropRect.size,
                    style = Stroke(width = 3f)
                )

                // Draw corner handles
                val handleRadius = 15f
                val handleColor = Color.Yellow
                
                // Top-left
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = cropRect.topLeft
                )
                // Top-right
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(cropRect.right, cropRect.top)
                )
                // Bottom-left
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(cropRect.left, cropRect.bottom)
                )
                // Bottom-right
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(cropRect.right, cropRect.bottom)
                )
            }
        }

        // Instructions
        Text(
            "Drag corners to resize • Drag inside to move",
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
