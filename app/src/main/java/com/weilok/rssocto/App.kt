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
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var oneTimeWork: OneTimeWorkRequest

    override fun onCreate() {
        super.onCreate()

        // Set theme based on preference
        val themePref = prefHandler.getThemePref()
        AppCompatDelegate.setDefaultNightMode(themePref)

        // Set periodic work interval
        val refreshIntervalPref = prefHandler.getRefreshIntervalPref()
        setPeriodicWork(refreshIntervalPref)

        // Set one time work refresh on startup
        val refreshOnStartPref = prefHandler.getRefreshOnStartPref()
        setOneTimeWork(refreshOnStartPref)

        // Initialize preference change listener
        PreferenceManager
            .getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val workManager = WorkManager.getInstance(applicationContext)
        when (key) {
            getString(R.string.theme_key) -> {
                // Change theme when theme preference change
                val value = sharedPreferences?.getString(key, getString(R.string.system_theme))
                val themePref = prefHandler.getThemePrefWithValue(value!!)

                AppCompatDelegate.setDefaultNightMode(themePref)
            }
            getString(R.string.refresh_auto_key) -> {
                // Enable or disable auto feed refresh
                val value = sharedPreferences?.getBoolean(key, false)

                if (value!!) {
                    workManager.enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicWork
                    )
                } else {
                    workManager.cancelUniqueWork(WORK_NAME)
                }
            }
            getString(R.string.refresh_interval_key) -> {
                // Set refresh interval based on preferences
                val value = sharedPreferences?.getString(key, getString(R.string.duration_30m))
                val refreshIntervalPref = prefHandler.getRefreshIntervalPrefWithValue(value!!)

                workManager.cancelUniqueWork(WORK_NAME)
                setPeriodicWork(refreshIntervalPref)
                workManager.enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWork
                )
            }
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun setPeriodicWork(interval: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        periodicWork = PeriodicWorkRequest
            .Builder(AutoRefreshWorker::class.java, interval, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInitialDelay(interval, TimeUnit.MINUTES)
            .build()
    }

    private fun setOneTimeWork(start: Boolean) {
        if (start) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

            oneTimeWork = OneTimeWorkRequest
                .Builder(AutoRefreshWorker::class.java)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(applicationContext)
                .enqueue(oneTimeWork)

            Toast.makeText(applicationContext, "Refreshing feeds...", Toast.LENGTH_LONG).show()
        }
    }
}