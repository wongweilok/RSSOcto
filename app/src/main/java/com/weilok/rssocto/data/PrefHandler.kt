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