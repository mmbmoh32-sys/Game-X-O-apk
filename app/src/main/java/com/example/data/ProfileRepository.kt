package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("neon_xo_profile", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _appLanguage = MutableStateFlow(loadLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private fun loadLanguage(): AppLanguage {
        val saved = prefs.getString("app_language", "ar") ?: "ar"
        return if (saved == "en") AppLanguage.ENGLISH else AppLanguage.ARABIC
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("app_language", lang.code).apply()
        _appLanguage.value = lang
    }

    private fun loadProfile(): UserProfile {
        val defaultName = if (loadLanguage() == AppLanguage.ARABIC) "لاعب النيون" else "CyberGamer"
        return UserProfile(
            name = prefs.getString("player_name", defaultName) ?: defaultName,
            avatarId = prefs.getString("avatar_id", "cyber_runner") ?: "cyber_runner",
            wins = prefs.getInt("wins", 0),
            losses = prefs.getInt("losses", 0),
            draws = prefs.getInt("draws", 0),
            currentStreak = prefs.getInt("current_streak", 0),
            bestStreak = prefs.getInt("best_streak", 0)
        )
    }

    fun updateName(newName: String) {
        val defaultName = if (_appLanguage.value == AppLanguage.ARABIC) "لاعب النيون" else "CyberGamer"
        val trimmed = newName.trim().ifEmpty { defaultName }
        prefs.edit().putString("player_name", trimmed).apply()
        _userProfile.value = _userProfile.value.copy(name = trimmed)
    }

    fun updateAvatar(avatarId: String) {
        prefs.edit().putString("avatar_id", avatarId).apply()
        _userProfile.value = _userProfile.value.copy(avatarId = avatarId)
    }

    fun recordWin() {
        val current = _userProfile.value
        val newWins = current.wins + 1
        val newStreak = current.currentStreak + 1
        val newBest = maxOf(current.bestStreak, newStreak)
        prefs.edit()
            .putInt("wins", newWins)
            .putInt("current_streak", newStreak)
            .putInt("best_streak", newBest)
            .apply()
        _userProfile.value = current.copy(
            wins = newWins,
            currentStreak = newStreak,
            bestStreak = newBest
        )
    }

    fun recordLoss() {
        val current = _userProfile.value
        val newLosses = current.losses + 1
        prefs.edit()
            .putInt("losses", newLosses)
            .putInt("current_streak", 0)
            .apply()
        _userProfile.value = current.copy(
            losses = newLosses,
            currentStreak = 0
        )
    }

    fun recordDraw() {
        val current = _userProfile.value
        val newDraws = current.draws + 1
        prefs.edit()
            .putInt("draws", newDraws)
            .apply()
        _userProfile.value = current.copy(draws = newDraws)
    }

    fun resetStats() {
        prefs.edit()
            .putInt("wins", 0)
            .putInt("losses", 0)
            .putInt("draws", 0)
            .putInt("current_streak", 0)
            .putInt("best_streak", 0)
            .apply()
        _userProfile.value = _userProfile.value.copy(
            wins = 0,
            losses = 0,
            draws = 0,
            currentStreak = 0,
            bestStreak = 0
        )
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean("sound_enabled", true)
    fun setSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean("sound_enabled", enabled).apply()

    fun isHapticsEnabled(): Boolean = prefs.getBoolean("haptics_enabled", true)
    fun setHapticsEnabled(enabled: Boolean) = prefs.edit().putBoolean("haptics_enabled", enabled).apply()
}
