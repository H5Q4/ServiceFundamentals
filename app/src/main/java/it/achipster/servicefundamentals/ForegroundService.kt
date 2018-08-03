package it.achipster.servicefundamentals

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.app.NotificationManager
import android.support.v4.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi



class ForegroundService : Service() {

  companion object {
    private const val CHANNEL_ID = "test_channel_id"
    private const val NOTIFICATION_ID = 100

    val TAG: String = ForegroundService::class.java.simpleName
  }

  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createNotificationChannels()
    }
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle("Notification from ForegroundService")
        .setContentText(
            getString(R.string.fake_notification_content)
        )
        .build()
    startForeground(NOTIFICATION_ID, notification)
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate, Thread => " + Thread.currentThread().name)
  }

  override fun onDestroy() {
    Log.d(TAG, "onDestroy")
    stopForeground(true)
    super.onDestroy()
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private fun createAppNotificationChanel(
    chanelId: String,
    chanelName: String,
    chanelDescription: String,
    chanelImportance: Int
  ): NotificationChannel {
    val channel = NotificationChannel(chanelId, chanelName, chanelImportance)
    channel.description = chanelDescription
    return channel
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private fun createNotificationChannels() {
    val channels = ArrayList<NotificationChannel>()
    channels.add(
        createAppNotificationChanel(
            CHANNEL_ID,
            "Cities",
            "Information about cities",
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
    )
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannels(channels)
  }
}
