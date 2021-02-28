package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.notifications.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var currentOption: DownloadOptions = DownloadOptions.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            val currentSelection = radioGroup.checkedRadioButtonId
            if (currentSelection != DEFAULT_RADIO_SELECTION) {
                when (currentSelection) {
                    radioGlide.id -> {
                        setOption(DownloadOptions.GLIDE)
                    }
                    radioRetrofit.id -> {
                        setOption(DownloadOptions.RETROFIT)
                    }
                    radioUdacity.id -> {
                        setOption(DownloadOptions.UDACITY)
                    }
                }
                download(currentOption)
                custom_button.load()
            } else {
                Toast.makeText(this, getString(R.string.select_option_message), Toast.LENGTH_LONG).show()
            }
        }
        createVersionChannels()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun createVersionChannels() {
        val channelId =  "download_channel"
        val channelName = getString(R.string.notification_channel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setShowBadge(false)
                enableLights(true)
                enableVibration(true)
                lightColor = Color.GREEN
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setOption(option: DownloadOptions) {
        currentOption = option
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val queryDownload = DownloadManager.Query()
                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(queryDownload)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val success = (status == DownloadManager.STATUS_SUCCESSFUL)
                    val titleDownload = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    sendNotification(success, titleDownload)
                }
                custom_button.download()
            }
        }
    }

    private fun download(option: DownloadOptions) {
        if (option == DownloadOptions.NONE) {
            Toast
                .makeText(this, getString(R.string.select_option_message), Toast.LENGTH_LONG)
                .show()

            return
        }

        val request =
            DownloadManager.Request(Uri.parse(option.url))
                .setTitle(option.title)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun sendNotification(success: Boolean, titleDownload: String) {
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()
        notificationManager.sendNotification(this, success, titleDownload)
    }

    companion object {
        private const val DEFAULT_RADIO_SELECTION = -1
    }

}
