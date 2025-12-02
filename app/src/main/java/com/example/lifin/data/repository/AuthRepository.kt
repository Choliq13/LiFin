package com.example.lifin.data.repository

import android.content.Context
import com.example.lifin.data.local.EncryptedPreferences
import com.example.lifin.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {
    private var prefs: EncryptedPreferences? = null

    constructor()
    constructor(context: Context) { prefs = EncryptedPreferences(context) }

    private fun hasPrefs() = prefs != null

    fun isLoggedIn(): Boolean {
        return prefs?.isLoggedIn() ?: false
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            // Login menggunakan Supabase Auth
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            // Simpan status login di preferences
            prefs?.setLoggedIn(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Email atau password salah: ${e.message}"))
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            // Logout dari Supabase
            SupabaseClient.client.auth.signOut()
            prefs?.clearLogin()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Gagal logout: ${e.message}"))
        }
    }

    suspend fun changePassword(current: String, new: String): Result<Unit> {
        return try {
            if (current.isBlank()) {
                return Result.failure(IllegalArgumentException("Password saat ini tidak boleh kosong"))
            }
            if (new.length < 6) {
                return Result.failure(IllegalArgumentException("Password baru minimal 6 karakter"))
            }
            if (new == current) {
                return Result.failure(IllegalArgumentException("Password baru tidak boleh sama dengan password saat ini"))
            }
            
            // Update password di Supabase
            SupabaseClient.client.auth.updateUser {
                password = new
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Gagal mengubah password: ${e.message}"))
        }
    }

    suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            if (!email.contains("@")) {
                return Result.failure(IllegalArgumentException("Email tidak valid"))
            }
            
            // Kirim email reset password menggunakan Supabase
            SupabaseClient.client.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Gagal mengirim email reset: ${e.message}"))
        }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            if (!email.contains("@")) {
                return Result.failure(IllegalArgumentException("Email tidak valid"))
            }
            if (newPassword.length < 6) {
                return Result.failure(IllegalArgumentException("Password baru minimal 6 karakter"))
            }
            
            // Note: Supabase menggunakan token dari email untuk reset password
            // Implementasi ini perlu disesuaikan dengan flow reset password Supabase
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Gagal reset password: ${e.message}"))
        }
    }

    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            if (!email.contains("@")) {
                return Result.failure(IllegalArgumentException("Email tidak valid"))
            }
            if (password.length < 6) {
                return Result.failure(IllegalArgumentException("Password minimal 6 karakter"))
            }
            
            // Register menggunakan Supabase Auth
            SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Gagal mendaftar: ${e.message}"))
        }
    }
}
