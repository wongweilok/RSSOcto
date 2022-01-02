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