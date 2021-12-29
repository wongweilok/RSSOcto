package com.weilok.rssocto.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import java.security.InvalidParameterException

enum class EntriesView { BY_ALL, BY_UNREAD }

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

    fun getEntryFilterPref(): EntriesView {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val entryPref = sp.getString("filter", "all")

        return entryPref?.let {
            getEntryFilter(entryPref)
        } ?: EntriesView.BY_ALL
    }

    private fun getEntryFilter(entryFilterPref: String): EntriesView {
        return when (entryFilterPref) {
            "all" -> EntriesView.BY_ALL
            "unread" -> EntriesView.BY_UNREAD
            else -> throw InvalidParameterException("Error loading filter for $entryFilterPref")
        }
    }
}