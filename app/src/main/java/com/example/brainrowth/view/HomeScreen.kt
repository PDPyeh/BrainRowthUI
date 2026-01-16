package com.example.brainrowth.view

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainrowth.recognizeTextFromBitmap
import com.example.brainrowth.ui.components.CameraCapture
import com.example.brainrowth.ui.components.MathKeyboard
import com.example.brainrowth.ui.components.MathExpressionDisplay
import com.example.brainrowth.ui.components.GalleryPicker
import com.example.brainrowth.viewmodel.SolverViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

enum class InputMode {
    MANUAL,
    CAMERA,
    HISTORY
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SolverScreen(
    viewModel: SolverViewModel = viewModel()
) {
    val state = viewModel.uiState.value
    var inputMode by remember { mutableStateOf(InputMode.MANUAL) }
    var showKeyboard by remember { mutableStateOf(false) }
    
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "BrainRowth",
                    fontWeight = FontWeight.Bold
                ) 
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        // Tab selector
        TabRow(
            selectedTabIndex = inputMode.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = inputMode == InputMode.MANUAL,
                onClick = { inputMode = InputMode.MANUAL },
                text = { Text("Manual") },
                icon = { Icon(Icons.Default.Edit, contentDescription = "Manual") }
            )
            Tab(
                selected = inputMode == InputMode.CAMERA,
                onClick = { 
                    if (cameraPermissionState.status.isGranted) {
                        inputMode = InputMode.CAMERA
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                },
                text = { Text("Camera") },
                icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Camera") }
            )
            Tab(
                selected = inputMode == InputMode.HISTORY,
                onClick = { inputMode = InputMode.HISTORY },
                text = { Text("History") },
                icon = { Icon(Icons.Default.History, contentDescription = "History") }
            )
        }

        // Content based on selected mode
        when (inputMode) {
            InputMode.MANUAL -> ManualInputScreen(
                viewModel = viewModel,
                showKeyboard = showKeyboard,
                onShowKeyboardChange = { showKeyboard = it }
            )
            InputMode.CAMERA -> {
                if (cameraPermissionState.status.isGranted) {
                    CameraScreen(
                        viewModel = viewModel,
                        onBackToManual = { inputMode = InputMode.MANUAL }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Camera permission is required to use this feature")
                        Button(
                            onClick = { cameraPermissionState.launchPermissionRequest() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            }
            InputMode.HISTORY -> HistoryScreen(
                viewModel = viewModel,
                onHistoryClick = { history ->
                    viewModel.loadFromHistory(history)
                    inputMode = InputMode.MANUAL
                }
            )
        }
    }
}

@Composable
fun ManualInputScreen(
    viewModel: SolverViewModel,
    showKeyboard: Boolean,
    onShowKeyboardChange: (Boolean) -> Unit
) {
    val state = viewModel.uiState.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Enter Math Problem",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Math Expression Preview
            if (state.question.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Mathematical Expression:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        MathExpressionDisplay(
                            expression = state.question,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.question,
                onValueChange = viewModel::onQuestionChange,
                label = { Text("Type or use math keyboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                minLines = 1,
                maxLines = 8,
                readOnly = false,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onShowKeyboardChange(!showKeyboard) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showKeyboard) 
                            MaterialTheme.colorScheme.secondary 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (showKeyboard) "Hide Keyboard" else "Math Keyboard", maxLines = 1)
                }

                Button(
                    onClick = { viewModel.solve() },
                    enabled = !state.isLoading && state.question.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Solve")
                    }
                }
            }

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            if (state.finalAnswer.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Final Answer:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (state.isSaved) {
                                Text(
                                    "Saved ✓",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            state.finalAnswer,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (state.steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Solution Steps:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        state.steps.forEachIndexed { index, step ->
                            Text("${index + 1}. $step")
                            if (index < state.steps.size - 1) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }

        // Math keyboard at bottom
        if (showKeyboard) {
            MathKeyboard(
                onKeyClick = { key ->
                    viewModel.onQuestionChange(state.question + key)
                },
                onDeleteClick = {
                    if (state.question.isNotEmpty()) {
                        viewModel.onQuestionChange(state.question.dropLast(1))
                    }
                },
                onClearClick = {
                    viewModel.onQuestionChange("")
                }
            )
        }
    }
}

@Composable
fun CameraScreen(
    viewModel: SolverViewModel,
    onBackToManual: () -> Unit
) {
    var mode by remember { mutableStateOf<String?>(null) } // null = choice, "camera" = camera, "gallery" = gallery
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    when (mode) {
        null -> {
            // Show choice screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Choose Image Source",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { mode = "camera" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("📷 Take Photo from Camera", fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { mode = "gallery" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("🖼️ Pick from Gallery", fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onBackToManual,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Manual Input")
                }
            }
        }
        "camera" -> {
            if (!isProcessing) {
                CameraCapture(
                    onImageCaptured = { bitmap ->
                        isProcessing = true
                        recognizeTextFromBitmap(
                            bitmap = bitmap,
                            onResult = { text ->
                                viewModel.onQuestionChange(text)
                                viewModel.solve()
                                onBackToManual()
                            },
                            onError = { exception ->
                                errorMessage = "OCR Error: ${exception.message}"
                                isProcessing = false
                            }
                        )
                    },
                    onError = { exception ->
                        errorMessage = "Camera Error: ${exception.message}"
                        mode = null
                    }
                )
            } else {
                ProcessingScreen(
                    onTryAgain = { 
                        isProcessing = false
                        errorMessage = null
                    },
                    onBack = { mode = null },
                    errorMessage = errorMessage
                )
            }
        }
        "gallery" -> {
            if (!isProcessing) {
                GalleryPicker(
                    onImagePicked = { bitmap ->
                        isProcessing = true
                        recognizeTextFromBitmap(
                            bitmap = bitmap,
                            onResult = { text ->
                                viewModel.onQuestionChange(text)
                                viewModel.solve()
                                onBackToManual()
                            },
                            onError = { exception ->
                                errorMessage = "OCR Error: ${exception.message}"
                                isProcessing = false
                            }
                        )
                    },
                    onError = { exception ->
                        errorMessage = "Gallery Error: ${exception.message}"
                        mode = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { mode = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Back")
                }
            } else {
                ProcessingScreen(
                    onTryAgain = { 
                        isProcessing = false
                        errorMessage = null
                        mode = "gallery"
                    },
                    onBack = { mode = null },
                    errorMessage = errorMessage
                )
            }
        }
    }
}

@Composable
fun ProcessingScreen(
    onTryAgain: () -> Unit,
    onBack: () -> Unit,
    errorMessage: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Processing image...")
        }
        
        Button(
            onClick = onTryAgain,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Try Again")
        }
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
