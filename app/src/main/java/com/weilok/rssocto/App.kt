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

import com.weilok.rssocto.data.PrefHandler

@HiltAndroidApp
class App : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var prefHandler: PrefHandler

    override fun onCreate() {
        super.onCreate()

        // Initialize preference handler
        prefHandler = PrefHandler(this)

        // Set theme based on preference
        val theme = prefHandler.getThemePref()
        AppCompatDelegate.setDefaultNightMode(theme)

        // Initialize preference change listener
        PreferenceManager
            .getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Change theme when theme preference change
        if (key == "theme") {
            val themePref = sharedPreferences?.getString(key, "system")
            val theme = prefHandler.getTheme(themePref!!)

            AppCompatDelegate.setDefaultNightMode(theme)
        }
    }
}