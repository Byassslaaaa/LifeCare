package com.example.lifecare.data

import android.content.Context
import android.content.SharedPreferences

/**
 * PINSessionManager - Manages PIN verification session
 *
 * Purpose: Menghindari user input PIN berulang kali setiap buka app
 * Session valid selama 30 menit sejak terakhir kali verify PIN
 *
 * Flow:
 * 1. User verify PIN → markPINVerified() → Save timestamp
 * 2. User buka app → isSessionValid() → Check jika < 30 menit
 * 3. Jika valid → Langsung ke HomeScreen
 * 4. Jika expired → Minta PIN lagi
 */
class PINSessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "pin_session_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_LAST_VERIFIED = "last_verified_timestamp"
        private const val SESSION_DURATION = 30 * 60 * 1000L // 30 menit dalam milliseconds
    }

    /**
     * Simpan timestamp saat PIN berhasil diverifikasi
     * Call this after user successfully verifies PIN
     */
    fun markPINVerified() {
        prefs.edit()
            .putLong(KEY_LAST_VERIFIED, System.currentTimeMillis())
            .apply()
    }

    /**
     * Check apakah session masih valid
     * Returns true jika < 30 menit sejak terakhir verify
     */
    fun isSessionValid(): Boolean {
        val lastVerified = prefs.getLong(KEY_LAST_VERIFIED, 0L)

        // Jika belum pernah verify, session tidak valid
        if (lastVerified == 0L) return false

        val now = System.currentTimeMillis()
        val elapsed = now - lastVerified

        return elapsed < SESSION_DURATION
    }

    /**
     * Clear session (saat logout atau force clear)
     * Call this when user logs out
     */
    fun clearSession() {
        prefs.edit().remove(KEY_LAST_VERIFIED).apply()
    }

    /**
     * Get remaining session time dalam menit
     * Useful untuk display ke user
     */
    fun getRemainingSessionMinutes(): Int {
        val lastVerified = prefs.getLong(KEY_LAST_VERIFIED, 0L)
        if (lastVerified == 0L) return 0

        val now = System.currentTimeMillis()
        val elapsed = now - lastVerified
        val remaining = SESSION_DURATION - elapsed

        return if (remaining > 0) {
            (remaining / 60000).toInt()
        } else {
            0
        }
    }

    /**
     * Check berapa lama lagi session akan expired (dalam detik)
     */
    fun getSessionExpiresInSeconds(): Long {
        val lastVerified = prefs.getLong(KEY_LAST_VERIFIED, 0L)
        if (lastVerified == 0L) return 0L

        val now = System.currentTimeMillis()
        val elapsed = now - lastVerified
        val remaining = SESSION_DURATION - elapsed

        return if (remaining > 0) {
            remaining / 1000
        } else {
            0L
        }
    }
}
