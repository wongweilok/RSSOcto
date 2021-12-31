package com.weilok.rssocto.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.InvalidParameterException
import javax.inject.Inject
import javax.inject.Singleton

enum class EntriesView { BY_ALL, BY_UNREAD }

@Singleton
class PreferenceHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getThemePref(): Int {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val themePrefValue = sp.getString("theme", "")

        return themePrefValue?.let {
            getThemePrefWithValue(it)
        } ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun getThemePrefWithValue(value: String): Int {
        return when (value) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    fun getFilterPref(): EntriesView {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val filterPrefValue = sp.getString("filter", "all")

        return filterPrefValue?.let {
            getFilterPrefWithValue(it)
        } ?: EntriesView.BY_ALL
    }

    private fun getFilterPrefWithValue(value: String): EntriesView {
        return when (value) {
            "all" -> EntriesView.BY_ALL
            "unread" -> EntriesView.BY_UNREAD
            else -> throw InvalidParameterException("Error loading filter for $value")
        }
    }
}