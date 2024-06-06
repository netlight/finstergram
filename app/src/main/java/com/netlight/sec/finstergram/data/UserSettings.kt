package com.netlight.sec.finstergram.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

@Serializable
class UserSettings private constructor(
    val darkMode: Boolean = false,
    val requirePassword: Boolean = true
) {
    init {
        instance = this
    }

    companion object {

        lateinit var instance: UserSettings

        fun load(context: Context) {
            val sharedPrefs = context.getSharedPreferences(FINSTERGRAM_PREFS, Context.MODE_PRIVATE)
            val json = sharedPrefs.getString(FINSTERGRAM_SETTINGS_PREF_KEY, null)
            val settings = json?.let { Json.decodeFromString<UserSettings>(it) }
            if (settings == null) {
                // no settings found on disk -> use default settings
                UserSettings()
            }
        }

        fun store(
            context: Context,
            darkMode: Boolean = instance.darkMode,
            requirePassword: Boolean = instance.requirePassword
        ) {
            val newSettings = UserSettings(darkMode, requirePassword)
            val json = Json.encodeToString(newSettings)
            val sharedPrefs = context.getSharedPreferences(FINSTERGRAM_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString(FINSTERGRAM_SETTINGS_PREF_KEY, json)
            editor.apply()
        }

        fun reset(context: Context) {
            val defaultSettings = UserSettings()
            store(context, defaultSettings.darkMode, defaultSettings.requirePassword)
        }

        private const val FINSTERGRAM_PREFS = "FinstergramPreferences"
        private const val FINSTERGRAM_SETTINGS_PREF_KEY = "UserSettings"
    }
}