package com.example.lifin.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "lifin_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_PIN = "user_pin"
        private const val KEY_PIN_ENABLED = "pin_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_AUTH_EMAIL = "auth_email"
        private const val KEY_NOTIF_ENABLED = "notif_enabled"
        private const val KEY_NOTIF_HOUR = "notif_hour"
        private const val KEY_NOTIF_MINUTE = "notif_minute"

        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_PROFILE_DOB = "profile_dob" // ISO-8601 yyyy-MM-dd
        private const val KEY_PROFILE_GENDER = "profile_gender"
        private const val KEY_PROFILE_HEIGHT_CM = "profile_height_cm"
        private const val KEY_PROFILE_WEIGHT_GOAL = "profile_weight_goal"

        private const val KEY_PROFILE_FIRST_NAME = "profile_first_name"
        private const val KEY_PROFILE_LAST_NAME = "profile_last_name"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
        private const val KEY_PROFILE_AVATAR_INDEX = "profile_avatar_index" // -1 if none

        private const val KEY_LAST_LOGIN_DATE = "last_login_date" // ISO yyyy-MM-dd
        private const val KEY_HEALTH_NOTE_DATES = "health_note_dates" // CSV ISO dates
    }

    // PIN Management
    fun savePin(pin: String) {
        prefs.edit().putString(KEY_PIN, pin).apply()
        prefs.edit().putBoolean(KEY_PIN_ENABLED, true).apply()
    }

    fun getPin(): String? = prefs.getString(KEY_PIN, null)

    fun isPinEnabled(): Boolean = prefs.getBoolean(KEY_PIN_ENABLED, false)

    fun clearPin() {
        prefs.edit().remove(KEY_PIN).apply()
        prefs.edit().putBoolean(KEY_PIN_ENABLED, false).apply()
    }

    // Biometric
    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean =
        prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    // Auth session (login state)
    fun setLoggedIn(email: String) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
        prefs.edit().putString(KEY_AUTH_EMAIL, email).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getEmail(): String? = prefs.getString(KEY_AUTH_EMAIL, null)

    fun clearLogin() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
        prefs.edit().remove(KEY_AUTH_EMAIL).apply()
    }

    // Notification preferences
    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()
    }

    fun isNotificationEnabled(): Boolean = prefs.getBoolean(KEY_NOTIF_ENABLED, false)

    fun setNotificationTime(hour: Int, minute: Int) {
        prefs.edit().putInt(KEY_NOTIF_HOUR, hour).apply()
        prefs.edit().putInt(KEY_NOTIF_MINUTE, minute).apply()
    }

    fun getNotificationHour(default: Int = 8): Int = prefs.getInt(KEY_NOTIF_HOUR, default)

    fun getNotificationMinute(default: Int = 0): Int = prefs.getInt(KEY_NOTIF_MINUTE, default)

    // Profile data
    fun setProfileName(name: String) { prefs.edit().putString(KEY_PROFILE_NAME, name).apply() }
    fun getProfileName(): String = prefs.getString(KEY_PROFILE_NAME, "") ?: ""

    fun setProfileDob(isoDate: String) { prefs.edit().putString(KEY_PROFILE_DOB, isoDate).apply() }
    fun getProfileDob(): String? = prefs.getString(KEY_PROFILE_DOB, null)

    fun setProfileGender(gender: String) { prefs.edit().putString(KEY_PROFILE_GENDER, gender).apply() }
    fun getProfileGender(): String = prefs.getString(KEY_PROFILE_GENDER, "Unspecified") ?: "Unspecified"

    fun setProfileHeightCm(height: Int) { prefs.edit().putInt(KEY_PROFILE_HEIGHT_CM, height).apply() }
    fun getProfileHeightCm(): Int = prefs.getInt(KEY_PROFILE_HEIGHT_CM, 0)

    fun setProfileWeightGoal(weight: Float) { prefs.edit().putFloat(KEY_PROFILE_WEIGHT_GOAL, weight).apply() }
    fun getProfileWeightGoal(): Float = prefs.getFloat(KEY_PROFILE_WEIGHT_GOAL, 0f)

    fun setProfileFirstName(name: String) { prefs.edit().putString(KEY_PROFILE_FIRST_NAME, name).apply() }
    fun getProfileFirstName(): String = prefs.getString(KEY_PROFILE_FIRST_NAME, "") ?: ""

    fun setProfileLastName(name: String) { prefs.edit().putString(KEY_PROFILE_LAST_NAME, name).apply() }
    fun getProfileLastName(): String = prefs.getString(KEY_PROFILE_LAST_NAME, "") ?: ""

    fun setProfileImageUri(uri: String) { prefs.edit().putString(KEY_PROFILE_IMAGE_URI, uri).apply() }
    fun getProfileImageUri(): String? = prefs.getString(KEY_PROFILE_IMAGE_URI, null)

    fun setProfileAvatarIndex(index: Int) { prefs.edit().putInt(KEY_PROFILE_AVATAR_INDEX, index).apply() }
    fun getProfileAvatarIndex(default: Int = -1): Int = prefs.getInt(KEY_PROFILE_AVATAR_INDEX, default)

    // Last login date
    fun setLastLoginDateIso(iso: String) { prefs.edit().putString(KEY_LAST_LOGIN_DATE, iso).apply() }
    fun getLastLoginDateIso(): String? = prefs.getString(KEY_LAST_LOGIN_DATE, null)

    // Health note dates
    fun addHealthNoteDate(iso: String) {
        val current = prefs.getString(KEY_HEALTH_NOTE_DATES, "") ?: ""
        val set = current.split(',').filter { it.isNotBlank() }.toMutableSet()
        set.add(iso)
        prefs.edit().putString(KEY_HEALTH_NOTE_DATES, set.joinToString(",")).apply()
    }
    fun getHealthNoteDates(): Set<String> {
        val current = prefs.getString(KEY_HEALTH_NOTE_DATES, "") ?: ""
        return current.split(',').filter { it.isNotBlank() }.toSet()
    }
}
