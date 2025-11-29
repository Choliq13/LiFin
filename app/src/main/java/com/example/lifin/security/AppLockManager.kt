package com.example.lifin.security

import android.content.Context
import com.example.lifin.data.repository.AuthRepository
import com.example.lifin.data.repository.PinRepository

/**
 * Simple in-memory app lock manager. Determines whether the app should be locked
 * when entering foreground if the user is logged in and has a PIN configured.
 */
class AppLockManager private constructor(private val context: Context) {
    @Volatile
    var isLocked: Boolean = false
        private set

    private var lastUnlockAt: Long = 0L
    var lockTimeoutMs: Long = 0L // 0 = lock immediately on foreground

    private val auth by lazy { AuthRepository(context) }
    private val pinRepo by lazy { PinRepository(context) }

    fun evaluateLockOnForeground() {
        val loggedIn = auth.isLoggedIn()
        val hasPin = pinRepo.isPinSet()
        val now = System.currentTimeMillis()
        val timedOut = (now - lastUnlockAt) >= lockTimeoutMs
        isLocked = loggedIn && hasPin && timedOut
    }

    fun unlock() {
        isLocked = false
        lastUnlockAt = System.currentTimeMillis()
    }

    companion object {
        @Volatile
        @Suppress("StaticFieldLeak") // We store only applicationContext to avoid leaking an Activity
        private var INSTANCE: AppLockManager? = null

        fun getInstance(context: Context): AppLockManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppLockManager(context.applicationContext).also { INSTANCE = it }
            }
    }
}
