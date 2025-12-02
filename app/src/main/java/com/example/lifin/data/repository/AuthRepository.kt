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
            Result.failure(IllegalArgumentException("Email atau password tidak valid. Silakan coba lagi atau daftar akun baru."))
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
            
            // Dapatkan email user yang sedang login
            val currentUser = SupabaseClient.client.auth.currentUserOrNull()
            val userEmail = currentUser?.email 
                ?: return Result.failure(IllegalArgumentException("User tidak ditemukan. Silakan login kembali."))
            
            // Verifikasi password saat ini dengan mencoba sign in baru
            try {
                // Test authentication dengan kredensial baru di dalam try block
                val testClient = io.github.jan.supabase.createSupabaseClient(
                    supabaseUrl = "https://zddbdwavmasndcymjone.supabase.co",
                    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpkZGJkd2F2bWFzbmRjeW1qb25lIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI5NDc4MDksImV4cCI6MjA0ODUyMzgwOX0.Fk4T15wNdruD5yX-KjHv6_r0uEFNZE4kxnDPOirvPRs"
                ) {
                    install(io.github.jan.supabase.auth.Auth)
                }
                
                // Coba login dengan password yang dimasukkan untuk verifikasi
                testClient.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
                    this.email = userEmail
                    this.password = current
                }
                
                // Jika sampai sini berarti password benar, update password di client utama
                SupabaseClient.client.auth.updateUser {
                    password = new
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                // Password salah atau ada error lain
                val errorMsg = e.message?.lowercase() ?: ""
                return if (errorMsg.contains("invalid") || errorMsg.contains("credentials") || errorMsg.contains("password")) {
                    Result.failure(IllegalArgumentException("Password saat ini tidak sesuai. Silakan periksa kembali atau gunakan fitur lupa password."))
                } else {
                    Result.failure(IllegalArgumentException("Gagal mengubah password: ${e.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Gagal mengubah password. Silakan coba lagi."))
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
