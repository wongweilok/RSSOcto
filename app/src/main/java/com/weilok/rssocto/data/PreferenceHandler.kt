package com.weilok.rssocto.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

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
        .map { pref ->
            val entriesView = EntriesView.valueOf(
                pref[PreferencesKey.ENTRIES_VIEW] ?: EntriesView.BY_ALL.name
            )
            FilterPref(entriesView)
        }

    suspend fun updateEntriesView(entriesView: EntriesView) {
        context.dataStore.edit { pref ->
            pref[PreferencesKey.ENTRIES_VIEW] = entriesView.name
        }
    }
}