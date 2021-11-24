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
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferenceHandler"

enum class EntriesView { BY_ALL, BY_UNREAD }

data class FilterPref(val entriesView: EntriesView)

@Singleton
class PreferenceHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "user_preferences"
    )

    private object PreferencesKey {
        val ENTRIES_VIEW = stringPreferencesKey("entries_view")
    }

    val preferencesFlow = context.dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e(TAG, "Error loading user preferences", e)
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { pref ->
            val entriesView = EntriesView.valueOf(
                pref[PreferencesKey.ENTRIES_VIEW] ?: EntriesView.BY_ALL.name
            )
            FilterPref(entriesView)
        }

    // Set and update preferences in datastore
    suspend fun updateEntriesView(entriesView: EntriesView) {
        context.dataStore.edit { pref ->
            pref[PreferencesKey.ENTRIES_VIEW] = entriesView.name
        }
    }
}