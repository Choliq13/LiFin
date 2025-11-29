package com.example.lifin.data.repository

import android.content.Context
import com.example.lifin.data.local.EncryptedPreferences

class AuthRepository {
    private var prefs: EncryptedPreferences? = null

    constructor()
    constructor(context: Context) { prefs = EncryptedPreferences(context) }

    private fun hasPrefs() = prefs != null

    fun isLoggedIn(): Boolean {
        return prefs?.isLoggedIn() ?: false
    }

    fun login(email: String, password: String): Result<Unit> {
        // Stubbed login: accept email contains '@' and password length >= 6
        return if (email.contains("@") && password.length >= 6) {
            prefs?.setLoggedIn(email)
            Result.success(Unit)
        } else Result.failure(IllegalArgumentException("Email atau password tidak valid"))
    }

    fun logout(): Result<Unit> {
        prefs?.clearLogin()
        return Result.success(Unit)
    }

    fun changePassword(current: String, new: String): Result<Unit> {
        // Stubbed logic: accept any non-empty current, new length >= 6 and different from current
        return if (current.isNotBlank() && new.length >= 6 && new != current) {
            Result.success(Unit)
        } else {
            val reason = when {
                current.isBlank() -> "Password saat ini tidak boleh kosong"
                new.length < 6 -> "Password baru minimal 6 karakter"
                new == current -> "Password baru tidak boleh sama dengan password saat ini"
                else -> "Gagal mengubah password"
            }
            Result.failure(IllegalArgumentException(reason))
        }
    }

    fun requestPasswordReset(email: String): Result<Unit> {
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Email tidak valid"))
        // In a real app, send code via email or OTP service. We just succeed for UI flow.
        return Result.success(Unit)
    }

    fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return if (email.contains("@") && code.length == 6 && newPassword.length >= 6) {
            Result.success(Unit)
        } else {
            val reason = when {
                !email.contains("@") -> "Email tidak valid"
                code.length != 6 -> "Kode verifikasi salah"
                newPassword.length < 6 -> "Password baru minimal 6 karakter"
                else -> "Gagal reset password"
            }
            Result.failure(IllegalArgumentException(reason))
        }
    }

    fun register(email: String, password: String): Result<Unit> {
        return if (email.contains("@") && password.length >= 6) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Email atau password tidak valid"))
        }
    }
}
