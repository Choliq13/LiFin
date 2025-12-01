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

    // === HEALTH DATA STORAGE ===
    // Menyimpan data kesehatan per tanggal untuk grafik dan history

    fun saveHealthData(date: String, data: HealthData) {
        val key = "health_data_$date"
        prefs.edit().apply {
            putString("${key}_beratBadan", data.beratBadan)
            putString("${key}_tinggiBadan", data.tinggiBadan)
            putString("${key}_tekananDarah", data.tekananDarah)
            putString("${key}_gulaDarah", data.gulaDarah)
            putString("${key}_aktivitas", data.aktivitas)
            putString("${key}_nutrisi", data.nutrisi)
            // Nutrisi details
            putString("${key}_menuMakanan", data.menuMakanan)
            putString("${key}_sesiMakan", data.sesiMakan)
            putString("${key}_makananUtama", data.makananUtama)
            putString("${key}_makananPendamping", data.makananPendamping)
            putString("${key}_karbohidrat", data.karbohidrat)
            putString("${key}_protein", data.protein)
            putString("${key}_lemak", data.lemak)
            putString("${key}_kalori", data.kalori)
            // Aktivitas details
            putString("${key}_durasi", data.durasi)
            putString("${key}_jenisAktivitas", data.jenisAktivitas)
            apply()
        }
    }

    fun getHealthData(date: String): HealthData? {
        val key = "health_data_$date"
        val beratBadan = prefs.getString("${key}_beratBadan", null) ?: return null
        return HealthData(
            beratBadan = beratBadan,
            tinggiBadan = prefs.getString("${key}_tinggiBadan", "") ?: "",
            tekananDarah = prefs.getString("${key}_tekananDarah", "") ?: "",
            gulaDarah = prefs.getString("${key}_gulaDarah", "") ?: "",
            aktivitas = prefs.getString("${key}_aktivitas", "") ?: "",
            nutrisi = prefs.getString("${key}_nutrisi", "") ?: "",
            // Nutrisi details
            menuMakanan = prefs.getString("${key}_menuMakanan", "") ?: "",
            sesiMakan = prefs.getString("${key}_sesiMakan", "") ?: "",
            makananUtama = prefs.getString("${key}_makananUtama", "") ?: "",
            makananPendamping = prefs.getString("${key}_makananPendamping", "") ?: "",
            karbohidrat = prefs.getString("${key}_karbohidrat", "") ?: "",
            protein = prefs.getString("${key}_protein", "") ?: "",
            lemak = prefs.getString("${key}_lemak", "") ?: "",
            kalori = prefs.getString("${key}_kalori", "") ?: "",
            // Aktivitas details
            durasi = prefs.getString("${key}_durasi", "") ?: "",
            jenisAktivitas = prefs.getString("${key}_jenisAktivitas", "") ?: ""
        )
    }

    // Mendapatkan semua data kesehatan untuk grafik (7 hari terakhir)
    fun getLastSevenDaysHealthData(): List<Pair<String, HealthData>> {
        val dates = getHealthNoteDates().sortedDescending().take(7)
        return dates.mapNotNull { date ->
            getHealthData(date)?.let { date to it }
        }
    }

    // Mendapatkan history untuk ditampilkan di calendar
    fun getAllHealthHistory(): List<HealthHistoryItem> {
        val dates = getHealthNoteDates().sortedDescending()
        return dates.mapNotNull { date ->
            getHealthData(date)?.let {
                HealthHistoryItem(
                    date = date,
                    data = it
                )
            }
        }
    }
}

// Data classes untuk health data
data class HealthData(
    val beratBadan: String,
    val tinggiBadan: String,
    val tekananDarah: String,
    val gulaDarah: String,
    val aktivitas: String,
    val nutrisi: String,
    // Nutrisi details (optional)
    val menuMakanan: String = "",
    val sesiMakan: String = "",
    val makananUtama: String = "",
    val makananPendamping: String = "",
    val karbohidrat: String = "",
    val protein: String = "",
    val lemak: String = "",
    val kalori: String = "",
    // Aktivitas details (optional)
    val durasi: String = "",
    val jenisAktivitas: String = ""
)

data class HealthHistoryItem(
    val date: String,
    val data: HealthData
)
