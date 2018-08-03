package it.achipster.servicefundamentals

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

class BootCompletedReceiver : BroadcastReceiver() {

  override fun onReceive(
    context: Context,
    intent: Intent
  ) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
      // 测试在后台启动 Background Service
//      DownloadIntentService.startActionFakeDownload(context, 12)

      // 测试在后台启动 Foreground Service
      if (VERSION.SDK_INT >= VERSION_CODES.O) {
        context.startForegroundService(Intent(context, ForegroundService::class.java))
      } else {
        context.startService(Intent(context, ForegroundService::class.java))
      }

      //region 测试在静态注册的 broadcast receiver 中 bind service
//      context.bindService(Intent(context, MyBackgroundService::class.java),
//          object : ServiceConnection {
//            override fun onServiceDisconnected(name: ComponentName?) {
//
//            }
//
//            override fun onServiceConnected(
//              name: ComponentName?,
//              service: IBinder?
//            ) {
//              Toast.makeText(
//                  context, "Bind service from static broadcast receiver", Toast.LENGTH_SHORT
//              )
//                  .show()
//            }
//          }, Service.BIND_AUTO_CREATE
//      )
      //endregion
    }
  }
}
