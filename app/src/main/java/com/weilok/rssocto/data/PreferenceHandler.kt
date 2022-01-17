/*
    Copyright (C) 2021 Wong Wei Lok <wongweilok@disroot.org>

    This file is part of RSSOcto

    RSSOcto is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RSSOcto is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this RSSOcto.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.weilok.rssocto.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.weilok.rssocto.R
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
        val themePrefValue = sp.getString(context.getString(R.string.theme_key), "")

        return themePrefValue?.let {
            getThemePrefWithValue(it)
        } ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun getThemePrefWithValue(value: String): Int {
        return when (value) {
            context.getString(R.string.dark_theme) -> AppCompatDelegate.MODE_NIGHT_YES
            context.getString(R.string.light_theme) -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    fun getFilterPref(): EntriesView {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val filterPrefValue = sp.getString(
            context.getString(R.string.entry_filter_key),
            context.getString(R.string.filter_all)
        )

        return filterPrefValue?.let {
            getFilterPrefWithValue(it)
        } ?: EntriesView.BY_ALL
    }

    private fun getFilterPrefWithValue(value: String): EntriesView {
        return when (value) {
            context.getString(R.string.filter_all) -> EntriesView.BY_ALL
            context.getString(R.string.filter_unread) -> EntriesView.BY_UNREAD
            else -> throw InvalidParameterException("Error loading filter for $value")
        }
    }

    fun getRefreshIntervalPref(): Long {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val value = sp.getString(
            context.getString(R.string.refresh_interval_key),
            context.getString(R.string.duration_30m)
        )

        return value?.let {
            getRefreshIntervalPrefWithValue(value)
        } ?: 30L
    }

    fun getRefreshIntervalPrefWithValue(value: String): Long {
        return when (value) {
            context.getString(R.string.duration_15m) -> 15L
            context.getString(R.string.duration_30m) -> 30L
            context.getString(R.string.duration_1h) -> 60L
            context.getString(R.string.duration_2h) -> 120L
            context.getString(R.string.duration_6h) -> 360L
            context.getString(R.string.duration_12h) -> 720L
            context.getString(R.string.duration_1d) -> 1440L
            else -> throw InvalidParameterException("Error selecting duration for $value")
        }
    }

    fun getRefreshOnStartPref(): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

        return sp.getBoolean(
            context.getString(R.string.refresh_startup_key),
            false
        )
    }
}