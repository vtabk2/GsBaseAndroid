package com.core.gsbaseandroid.utils.preferences

import android.content.Context

class GsDefaultConfig(val context: Context) {
    private val prefs = context.getSharedPreferences(GS_DEFAULT_CONFIG_NAME, Context.MODE_PRIVATE)

    var isGSFirstStartApp: Boolean
        get() = prefs.getBoolean(GS_KEY_FIRST_START_APP, true)
        set(value) = prefs.edit().putBoolean(GS_KEY_FIRST_START_APP, value).apply()

    companion object {
        fun newInstance(context: Context) = GsDefaultConfig(context)

        private const val GS_DEFAULT_CONFIG_NAME = "GS_DEFAULT_CONFIG_NAME"
        private const val GS_KEY_FIRST_START_APP = "GS_KEY_FIRST_START_APP"
    }
}