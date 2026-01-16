package com.example.brainrowth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MathKey(
    val display: String,
    val value: String,
    val type: KeyType = KeyType.NORMAL
)

enum class KeyType {
    NORMAL,
    OPERATOR,
    FUNCTION,
    DELETE,
    CLEAR
}

@Composable
fun MathKeyboard(
    onKeyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mathKeys = listOf(
        MathKey("7", "7"),
        MathKey("8", "8"),
        MathKey("9", "9"),
        MathKey("÷", "/", KeyType.OPERATOR),
        MathKey("×", "*", KeyType.OPERATOR),
        
        MathKey("4", "4"),
        MathKey("5", "5"),
        MathKey("6", "6"),
        MathKey("+", "+", KeyType.OPERATOR),
        MathKey("-", "-", KeyType.OPERATOR),
        
        MathKey("1", "1"),
        MathKey("2", "2"),
        MathKey("3", "3"),
        MathKey("(", "("),
        MathKey(")", ")"),
        
        MathKey("0", "0"),
        MathKey(".", "."),
        MathKey("^", "^", KeyType.OPERATOR),
        MathKey("√", "sqrt(", KeyType.FUNCTION),
        MathKey("π", "pi", KeyType.FUNCTION),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        // Math symbol keys grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(240.dp)
        ) {
            items(mathKeys) { key ->
                MathKeyButton(
                    key = key,
                    onClick = { onKeyClick(key.value) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom row: Clear and Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onClearClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("AC", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun MathKeyButton(
    key: MathKey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (key.type) {
        KeyType.OPERATOR -> MaterialTheme.colorScheme.primaryContainer
        KeyType.FUNCTION -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when (key.type) {
        KeyType.OPERATOR -> MaterialTheme.colorScheme.onPrimaryContainer
        KeyType.FUNCTION -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = key.display,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}
