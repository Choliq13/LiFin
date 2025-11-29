package com.example.lifin.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifin.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Email dan password harus diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            val result = repository.login(email, password)

            result.onSuccess {
                Log.d("AuthViewModel", "✅ Login berhasil")
                _uiState.value = AuthUiState(isSuccess = true)
                onSuccess()
            }.onFailure { error ->
                Log.e("AuthViewModel", "❌ Login gagal", error)
                _uiState.value = AuthUiState(
                    error = "Login gagal: ${error.message}"
                )
            }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Email dan password harus diisi")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthUiState(error = "Password minimal 6 karakter")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            val result = repository.register(email, password)

            result.onSuccess {
                Log.d("AuthViewModel", "✅ Register berhasil")
                _uiState.value = AuthUiState(isSuccess = true)
                onSuccess()
            }.onFailure { error ->
                Log.e("AuthViewModel", "❌ Register gagal", error)
                _uiState.value = AuthUiState(
                    error = "Pendaftaran gagal: ${error.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
