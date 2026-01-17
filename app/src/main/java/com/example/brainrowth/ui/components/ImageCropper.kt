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
import androidx.compose.ui.graphics.*
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
                Offset(100f, 100f),
                Size(300f, 300f)
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

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val handleSize = 40f
                                dragMode = when {
                                    abs(offset.x - cropRect.left) < handleSize &&
                                            abs(offset.y - cropRect.top) < handleSize ->
                                        DragMode.RESIZE_TL

                                    abs(offset.x - cropRect.right) < handleSize &&
                                            abs(offset.y - cropRect.top) < handleSize ->
                                        DragMode.RESIZE_TR

                                    abs(offset.x - cropRect.left) < handleSize &&
                                            abs(offset.y - cropRect.bottom) < handleSize ->
                                        DragMode.RESIZE_BL

                                    abs(offset.x - cropRect.right) < handleSize &&
                                            abs(offset.y - cropRect.bottom) < handleSize ->
                                        DragMode.RESIZE_BR

                                    offset.x in cropRect.left..cropRect.right &&
                                            offset.y in cropRect.top..cropRect.bottom ->
                                        DragMode.MOVE

                                    else -> DragMode.NONE
                                }
                            },

                            onDrag = { change, dragAmount ->
                                change.consume()
                                val minSize = 80f

                                cropRect = when (dragMode) {

                                    DragMode.MOVE -> {
                                        val newLeft = (cropRect.left + dragAmount.x)
                                            .coerceIn(0f, size.width - cropRect.width)
                                        val newTop = (cropRect.top + dragAmount.y)
                                            .coerceIn(0f, size.height - cropRect.height)

                                        Rect(Offset(newLeft, newTop), cropRect.size)
                                    }

                                    DragMode.RESIZE_TL -> {
                                        val left = (cropRect.left + dragAmount.x).coerceAtLeast(0f)
                                        val top = (cropRect.top + dragAmount.y).coerceAtLeast(0f)
                                        Rect(
                                            Offset(left, top),
                                            Size(
                                                (cropRect.right - left).coerceAtLeast(minSize),
                                                (cropRect.bottom - top).coerceAtLeast(minSize)
                                            )
                                        )
                                    }

                                    DragMode.RESIZE_TR -> {
                                        val right = (cropRect.right + dragAmount.x)
                                            .coerceAtMost(size.width.toFloat())
                                        val top = (cropRect.top + dragAmount.y).coerceAtLeast(0f)
                                        Rect(
                                            Offset(cropRect.left, top),
                                            Size(
                                                (right - cropRect.left).coerceAtLeast(minSize),
                                                (cropRect.bottom - top).coerceAtLeast(minSize)
                                            )
                                        )
                                    }

                                    DragMode.RESIZE_BL -> {
                                        val left = (cropRect.left + dragAmount.x).coerceAtLeast(0f)
                                        val bottom = (cropRect.bottom + dragAmount.y)
                                            .coerceAtMost(size.height.toFloat())
                                        Rect(
                                            Offset(left, cropRect.top),
                                            Size(
                                                (cropRect.right - left).coerceAtLeast(minSize),
                                                (bottom - cropRect.top).coerceAtLeast(minSize)
                                            )
                                        )
                                    }

                                    DragMode.RESIZE_BR -> {
                                        val right = (cropRect.right + dragAmount.x)
                                            .coerceAtMost(size.width.toFloat())
                                        val bottom = (cropRect.bottom + dragAmount.y)
                                            .coerceAtMost(size.height.toFloat())
                                        Rect(
                                            cropRect.topLeft,
                                            Size(
                                                (right - cropRect.left).coerceAtLeast(minSize),
                                                (bottom - cropRect.top).coerceAtLeast(minSize)
                                            )
                                        )
                                    }

                                    else -> cropRect
                                }
                            },

                            onDragEnd = { dragMode = DragMode.NONE }
                        )
                    }
            ) {
                canvasSize = size

                drawRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    size = size
                )

                drawRect(
                    color = Color.Transparent,
                    topLeft = cropRect.topLeft,
                    size = cropRect.size,
                    blendMode = BlendMode.Clear
                )

                drawRect(
                    color = Color.Cyan,
                    topLeft = cropRect.topLeft,
                    size = cropRect.size,
                    style = Stroke(3f)
                )

                val r = 14f
                val c = Color.Yellow

                drawCircle(c, r, cropRect.topLeft)
                drawCircle(c, r, Offset(cropRect.right, cropRect.top))
                drawCircle(c, r, Offset(cropRect.left, cropRect.bottom))
                drawCircle(c, r, Offset(cropRect.right, cropRect.bottom))
            }
        }

        Text(
            "Drag corner to resize • Drag inside to move",
            fontSize = 14.sp,
            modifier = Modifier.padding(8.dp)
        )

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
                modifier = Modifier.weight(1f),
                onClick = {
                    val scaleX = bitmap.width / canvasSize.width
                    val scaleY = bitmap.height / canvasSize.height

                    val mappedRect = Rect(
                        Offset(
                            cropRect.left * scaleX,
                            cropRect.top * scaleY
                        ),
                        Size(
                            cropRect.width * scaleX,
                            cropRect.height * scaleY
                        )
                    )

                    onCropConfirmed(cropBitmap(bitmap, mappedRect))
                }
            ) {
                Text("Confirm & Solve")
            }
        }
    }
}

private fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
    val x = rect.left.toInt().coerceIn(0, bitmap.width - 1)
    val y = rect.top.toInt().coerceIn(0, bitmap.height - 1)
    val w = rect.width.toInt().coerceAtMost(bitmap.width - x)
    val h = rect.height.toInt().coerceAtMost(bitmap.height - y)

    return Bitmap.createBitmap(bitmap, x, y, w.coerceAtLeast(1), h.coerceAtLeast(1))
}
