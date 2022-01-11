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

package com.weilok.rssocto.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.weilok.rssocto.R

const val CHANNEL_NAME = "Feeds Refresh Notification"
const val CHANNEL_DESC = "Show feed(s) refresh status"
const val CHANNEL_ID = "FEED_REFRESH_NOTIFICATION"
const val NOTIFICATION_TITLE = "Feeds updated"
const val NOTIFICATION_ID = 21

fun createNotification(context: Context, message: String) {
    // Create notification channel (Android API 26 and above)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESC
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    // Create notification
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_rss_feed)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    // Show notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder.build())
}