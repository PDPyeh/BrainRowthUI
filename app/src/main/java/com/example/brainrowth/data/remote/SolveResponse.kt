package com.example.brainrowth.data.remote

data class SolveResponse(
    val question: String,
    val steps: List<String> = emptyList(),
    val final_answer: String? = null,
    val raw_answer: String? = null,
    val parse_error: String? = null
)