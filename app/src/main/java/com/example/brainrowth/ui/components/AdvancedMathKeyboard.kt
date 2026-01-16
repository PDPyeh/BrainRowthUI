package com.example.brainrowth.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MathCategory {
    BASIC,
    TRIG,
    ADVANCED,
    SYMBOLS
}

@Composable
fun AdvancedMathKeyboard(
    onKeyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(MathCategory.BASIC) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Category tabs
        TabRow(selectedTabIndex = selectedCategory.ordinal) {
            Tab(
                selected = selectedCategory == MathCategory.BASIC,
                onClick = { selectedCategory = MathCategory.BASIC },
                text = { Text("Basic") }
            )
            Tab(
                selected = selectedCategory == MathCategory.TRIG,
                onClick = { selectedCategory = MathCategory.TRIG },
                text = { Text("Trig") }
            )
            Tab(
                selected = selectedCategory == MathCategory.ADVANCED,
                onClick = { selectedCategory = MathCategory.ADVANCED },
                text = { Text("Advanced") }
            )
            Tab(
                selected = selectedCategory == MathCategory.SYMBOLS,
                onClick = { selectedCategory = MathCategory.SYMBOLS },
                text = { Text("Symbols") }
            )
        }

        // Keys grid
        val keys = when (selectedCategory) {
            MathCategory.BASIC -> listOf(
                "7", "8", "9", "+",
                "4", "5", "6", "-",
                "1", "2", "3", "×",
                "0", ".", "=", "÷",
                "^", "√", "(", ")",
                "π", "e", "x", "y"
            )
            MathCategory.TRIG -> listOf(
                "sin(", "cos(", "tan(", ")",
                "asin(", "acos(", "atan(", ")",
                "sinh(", "cosh(", "tanh(", ")",
                "sec(", "csc(", "cot(", ")",
                "°", "rad", "π", "2π"
            )
            MathCategory.ADVANCED -> listOf(
                "log(", "ln(", "exp(", ")",
                "∫", "d/dx", "Σ", "Π",
                "lim", "∞", "∂", "∇",
                "√(", "∛(", "ⁿ√", ")",
                "!", "⌊⌋", "⌈⌉", "|x|"
            )
            MathCategory.SYMBOLS -> listOf(
                "≠", "≈", "≤", "≥",
                "∈", "∉", "⊂", "⊃",
                "∪", "∩", "∅", "ℝ",
                "ℕ", "ℤ", "ℚ", "ℂ",
                "∀", "∃", "∧", "∨"
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(keys) { key ->
                Button(
                    onClick = { onKeyClick(key) },
                    modifier = Modifier
                        .padding(2.dp)
                        .aspectRatio(1.2f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            key in listOf("+", "-", "×", "÷", "=") -> MaterialTheme.colorScheme.tertiary
                            key.length > 2 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(
                        key,
                        fontSize = if (key.length > 3) 10.sp else 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom row: Delete and Clear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Delete")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete")
            }

            Button(
                onClick = onClearClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            ) {
                Text("Clear All")
            }
        }
    }
}
