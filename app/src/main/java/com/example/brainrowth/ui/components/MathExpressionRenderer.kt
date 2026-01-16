package com.example.brainrowth.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Parse dan render mathematical expressions dengan proper symbols
 * sqrt(x) → √x
 * x^2 → x²
 * pi → π
 * dll
 */
fun parseMathExpression(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            when {
                // Handle sqrt(...)
                text.startsWith("sqrt(", i) -> {
                    append("√")
                    i += 5 // skip "sqrt("
                    var parenCount = 1
                    val content = StringBuilder()
                    while (i < text.length && parenCount > 0) {
                        when (text[i]) {
                            '(' -> parenCount++
                            ')' -> parenCount--
                        }
                        if (parenCount > 0) {
                            content.append(text[i])
                        }
                        i++
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(content.toString())
                    }
                }
                
                // Handle pi
                text.startsWith("pi", i) -> {
                    append("π")
                    i += 2
                }
                
                // Handle power (x^n) - convert to superscript
                i + 1 < text.length && text[i + 1] == '^' && text[i].isLetterOrDigit() -> {
                    append(text[i])
                    i++
                    append("^")
                    i++ // skip ^
                    
                    // Get the exponent
                    val exponent = StringBuilder()
                    while (i < text.length && (text[i].isDigit() || text[i] == '.')) {
                        exponent.append(text[i])
                        i++
                    }
                    
                    // Apply superscript styling
                    withStyle(SpanStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )) {
                        append(convertToSuperscript(exponent.toString()))
                    }
                }
                
                // Handle multiplication symbols
                text[i] == '*' -> {
                    append("×")
                    i++
                }
                
                // Handle division
                text[i] == '/' -> {
                    append("÷")
                    i++
                }
                
                // Default
                else -> {
                    append(text[i])
                    i++
                }
            }
        }
    }
}

/**
 * Convert regular numbers to superscript Unicode characters
 */
private fun convertToSuperscript(text: String): String {
    val superscriptMap = mapOf(
        '0' to '⁰',
        '1' to '¹',
        '2' to '²',
        '3' to '³',
        '4' to '⁴',
        '5' to '⁵',
        '6' to '⁶',
        '7' to '⁷',
        '8' to '⁸',
        '9' to '⁹',
        '+' to '⁺',
        '-' to '⁻',
        '=' to '⁼',
        '(' to '⁽',
        ')' to '⁾'
    )
    
    return text.map { superscriptMap[it] ?: it }.joinToString("")
}

@Composable
fun MathExpressionDisplay(
    expression: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal
) {
    if (expression.isBlank()) return
    
    Text(
        text = parseMathExpression(expression),
        modifier = modifier,
        fontSize = fontSize,
        color = color,
        fontWeight = fontWeight
    )
}
