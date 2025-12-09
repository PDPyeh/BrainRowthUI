package com.example.brainrowth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainrowth.data.remote.ApiClient
import com.example.brainrowth.data.remote.SolveRequest
import com.example.brainrowth.data.remote.SolveResponse
import kotlinx.coroutines.launch

data class SolverUiState(
    val question: String = "",
    val isLoading: Boolean = false,
    val steps: List<String> = emptyList(),
    val finalAnswer: String = "",
    val errorMessage: String? = null
)

class SolverViewModel : ViewModel() {

    var uiState = androidx.compose.runtime.mutableStateOf(SolverUiState())
        private set

    fun onQuestionChange(newValue: String) {
        uiState.value = uiState.value.copy(question = newValue)
    }

    fun solve() {
        val current = uiState.value
        if (current.question.isBlank()) {
            uiState.value = current.copy(
                errorMessage = "Soal tidak boleh kosong"
            )
            return
        }

        viewModelScope.launch {
            uiState.value = current.copy(
                isLoading = true,
                errorMessage = null,
                steps = emptyList(),
                finalAnswer = ""
            )

            try {
                val response: SolveResponse = ApiClient.api.solveText(
                    SolveRequest(question = current.question)
                )

                uiState.value = uiState.value.copy(
                    isLoading = false,
                    steps = response.steps,
                    finalAnswer = response.final_answer ?: "",
                    errorMessage = response.parse_error
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal menghubungi server: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }
}