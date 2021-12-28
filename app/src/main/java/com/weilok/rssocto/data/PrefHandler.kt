package com.weilok.rssocto.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class PrefHandler(private val context: Context) {
    fun getThemePref(): Int {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val themePref = sp.getString("theme", "")

        return themePref?.let {
            getTheme(it)
        } ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun getTheme(themePref: String): Int {
        return when (themePref) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
}