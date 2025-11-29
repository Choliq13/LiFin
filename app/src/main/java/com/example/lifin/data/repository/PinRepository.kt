package com.example.lifin.data.repository

import android.content.Context
import com.example.lifin.data.local.EncryptedPreferences

class PinRepository(context: Context) {
    private val prefs = EncryptedPreferences(context)

    fun isPinSet(): Boolean {
        val pin = prefs.getPin()
        return prefs.isPinEnabled() && pin != null && pin.length == 6
    }

    fun clearPin() {
        prefs.clearPin()
    }

    fun verifyPin(pin: String): Boolean {
        val stored = prefs.getPin()
        return stored != null && pin.length == 6 && pin == stored
    }

    fun setupPin(pin: String): Result<Unit> {
        return if (pin.length == 6 && pin.all { it.isDigit() }) {
            prefs.savePin(pin)
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("PIN harus 6 digit"))
        }
    }

    // Optional biometric helpers if needed by UI later
    fun setBiometricEnabled(enabled: Boolean) = prefs.setBiometricEnabled(enabled)
    fun isBiometricEnabled(): Boolean = prefs.isBiometricEnabled()
}
