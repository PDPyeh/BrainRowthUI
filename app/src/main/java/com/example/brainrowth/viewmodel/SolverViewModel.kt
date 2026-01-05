package com.example.brainrowth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainrowth.data.local.AppDatabase
import com.example.brainrowth.data.local.HistoryEntity
import com.example.brainrowth.data.remote.ApiClient
import com.example.brainrowth.data.remote.SolveRequest
import com.example.brainrowth.data.remote.SolveResponse
import com.example.brainrowth.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SolverUiState(
    val question: String = "",
    val isLoading: Boolean = false,
    val steps: List<String> = emptyList(),
    val finalAnswer: String = "",
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class SolverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    val historyList: StateFlow<List<HistoryEntity>>

    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
        historyList = repository.allHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    var uiState = androidx.compose.runtime.mutableStateOf(SolverUiState())
        private set

    fun onQuestionChange(newValue: String) {
        uiState.value = uiState.value.copy(question = newValue, isSaved = false)
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
                finalAnswer = "",
                isSaved = false
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

                // Auto-save to history if solve successful
                if (response.final_answer != null && response.parse_error == null) {
                    saveToHistory()
                }
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal menghubungi server: ${e.message}"
                )
            }
        }
    }

    fun saveToHistory() {
        val current = uiState.value
        if (current.finalAnswer.isBlank()) return

        viewModelScope.launch {
            val history = HistoryEntity(
                question = current.question,
                steps = current.steps,
                finalAnswer = current.finalAnswer
            )
            repository.insert(history)
            uiState.value = current.copy(isSaved = true)
        }
    }

    fun deleteHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.delete(history)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun loadFromHistory(history: HistoryEntity) {
        uiState.value = SolverUiState(
            question = history.question,
            steps = history.steps,
            finalAnswer = history.finalAnswer,
            isSaved = true
        )
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }

    fun clearResult() {
        uiState.value = SolverUiState()
    }
}