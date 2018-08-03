package it.achipster.servicefundamentals

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log

class MyBoundService : Service() {

  private val handler = ServerSideHandler(this)
  private val messenger = Messenger(handler)

  companion object {
    val TAG: String = MyBoundService::class.java.simpleName

    const val MSG_DO_SOMETHING = 1
    const val EXTRA_WORK_NAME = "work_name"
  }

  class ServerSideHandler(private val myBoundService: MyBoundService) : Handler() {
    override fun handleMessage(msg: Message?) {
      when (msg?.what) {
        MSG_DO_SOMETHING -> {
          val workName = msg.data.getString(EXTRA_WORK_NAME)
          myBoundService.doSomething(workName)
          msg.replyTo.send(Message.obtain().apply {
            what = MainActivity.MSG_RESULT_FROM_SERVICE
            data = Bundle().apply {
              putString(MainActivity.EXTRA_RESULT_FROM_SERVICE, "Result from Service")
            }
          })
        }
      }
    }
  }

  inner class LocalBinder : Binder() {
    fun getService(): MyBoundService {
      return this@MyBoundService
    }
  }

  fun doSomething(workName: String = "default") {
    Log.d(
        TAG,
        "do $workName in MyBoundService => Process: ${getProcessName()}, Thread: ${Thread.currentThread().name}"
    )
  }

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate => Process: ${getProcessName()}, Thread: ${Thread.currentThread().name}")
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    Log.d(TAG, "onStartCommand => Process: ${getProcessName()}, Thread: ${Thread.currentThread().name}")
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onBind(intent: Intent): IBinder {
    Log.d(TAG, "onBind => Process: ${getProcessName()}, Thread: ${Thread.currentThread().name}")
//    return LocalBinder()
    return messenger.binder
  }

  override fun onUnbind(intent: Intent?): Boolean {
    Log.d(TAG, "onUnbind => Process: ${getProcessName()}, Thread: ${Thread.currentThread().name}")
    return super.onUnbind(intent)
  }

  override fun onDestroy() {
    Log.d(TAG, "onDestroy => Process: ${getProcessName()}, Thread: ${Thread.currentThread().name}")
    super.onDestroy()
  }

}
