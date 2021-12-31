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

package com.weilok.rssocto

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

import com.weilok.rssocto.data.PreferenceHandler

@HiltAndroidApp
class App : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    lateinit var prefHandler: PreferenceHandler

    override fun onCreate() {
        super.onCreate()

        // Set theme based on preference
        val themePref = prefHandler.getThemePref()
        AppCompatDelegate.setDefaultNightMode(themePref)

        // Initialize preference change listener
        PreferenceManager
            .getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Change theme when theme preference change
        if (key == "theme") {
            val value = sharedPreferences?.getString(key, "")
            val themePref = prefHandler.getThemePrefWithValue(value!!)

            AppCompatDelegate.setDefaultNightMode(themePref)
        }
    }
}