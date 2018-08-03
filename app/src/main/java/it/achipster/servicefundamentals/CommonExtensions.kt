package it.achipster.servicefundamentals

import android.app.ActivityManager
import android.content.Context
import android.os.Process

fun Context.getProcessName(pid: Int = Process.myPid()): String? {
  val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
  val runningApps = am.runningAppProcesses ?: return null
  for (procInfo in runningApps) {
    if (procInfo.pid == pid) {
      return procInfo.processName
    }
  }
  return null
}