package com.udacity.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0


fun NotificationManager.sendNotification(applicationContext: Context, success: Boolean, titleDownload: String) {


    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(DetailActivity.STATUS, success)
    contentIntent.putExtra(DetailActivity.FILE_NAME, titleDownload)


    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val buttonPendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            contentIntent,
            FLAGS)

    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.channel_id)
    )
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(applicationContext
                    .getString(R.string.notification_description))
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                    R.drawable.ic_assistant_black_24dp,
                    applicationContext.getString(R.string.notification_button),
                    buttonPendingIntent
            ).setSmallIcon(R.drawable.ic_assistant_black_24dp)

            .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}