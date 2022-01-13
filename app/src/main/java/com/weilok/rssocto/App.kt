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
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import java.util.concurrent.TimeUnit

import com.weilok.rssocto.data.PreferenceHandler
import com.weilok.rssocto.workers.AutoRefreshWorker

private const val WORK_NAME = "refreshFeedsWork"

@HiltAndroidApp
class App : Application(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    Configuration.Provider {
    @Inject
    lateinit var prefHandler: PreferenceHandler
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    private lateinit var periodicWork: PeriodicWorkRequest

    override fun onCreate() {
        super.onCreate()

        // Set theme based on preference
        val themePref = prefHandler.getThemePref()
        AppCompatDelegate.setDefaultNightMode(themePref)

        // Initialize preference change listener
        PreferenceManager
            .getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

        setPeriodicWork()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Change theme when theme preference change
        if (key == getString(R.string.theme_key)) {
            val value = sharedPreferences?.getString(key, getString(R.string.system_theme))
            val themePref = prefHandler.getThemePrefWithValue(value!!)

            AppCompatDelegate.setDefaultNightMode(themePref)
        } else if (key == getString(R.string.refresh_auto_key)) {
            val value = sharedPreferences?.getBoolean(key, false)

            if (value!!) {
                WorkManager.getInstance(applicationContext)
                    .enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicWork
                    )
            } else {
                WorkManager.getInstance(applicationContext)
                    .cancelUniqueWork(WORK_NAME)
            }
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun setPeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        periodicWork = PeriodicWorkRequest
            .Builder(AutoRefreshWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
    }
}