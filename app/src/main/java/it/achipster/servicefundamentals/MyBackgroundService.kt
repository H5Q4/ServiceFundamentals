package it.achipster.servicefundamentals

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log

class MyBackgroundService : Service() {

  companion object {
    val TAG: String = MyBackgroundService::class.java.simpleName
  }

  inner class LocalBinder: Binder() {
    fun getService(): MyBackgroundService {
      return this@MyBackgroundService
    }
  }

  override fun onBind(intent: Intent): IBinder {
    return LocalBinder()
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    Log.d(TAG, "onStartCommand, Thread => " + Thread.currentThread().name)
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate, Thread => " + Thread.currentThread().name)
  }

  override fun onDestroy() {
    Log.d(TAG, "onDestroy")
    super.onDestroy()
  }
}
