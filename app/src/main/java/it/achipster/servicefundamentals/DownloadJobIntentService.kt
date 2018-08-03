package it.achipster.servicefundamentals

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

class DownloadJobIntentService: JobIntentService() {

  companion object {
    private const val ACTION_FAKE_DOWNLOAD = "it.achipster.servicefundamentals.action.FakeDownload"
    private const val EXTRA_SLEEP_TIME = "it.achipster.servicefundamentals.extra.SleepTime"
    const val ACTION_DOWNLOAD_FINISHED = "it.achipster.servicefundamentals.action.DownloadFinished"
    const val EXTRA_ELAPSED_TIME = "it.achipster.servicefundamentals.extra.ElapsedTime"

    private val TAG: String = DownloadJobIntentService::class.java.simpleName

    @JvmStatic fun startActionFakeDownload(
      context: Context,
      sleepTime: Int
    ) {
      val intent = Intent().apply {
        action = ACTION_FAKE_DOWNLOAD
        putExtra(EXTRA_SLEEP_TIME, sleepTime)
      }
      enqueueWork(context, DownloadJobIntentService::class.java, 100, intent)
    }

  }

  override fun onHandleWork(intent: Intent) {
    Log.d(TAG, "onHandleWork, thread => ${Thread.currentThread().name}")
    when (intent.action) {
      DownloadJobIntentService.ACTION_FAKE_DOWNLOAD -> {
        val sleepTime = intent.getIntExtra(DownloadJobIntentService.EXTRA_SLEEP_TIME, 0)
        handleActionFakeDownload(sleepTime)
      }
    }
  }

  private fun handleActionFakeDownload(sleepTime: Int) {
    var timeElapsed = 0
    while (sleepTime > timeElapsed) {
      Thread.sleep(1000)
      timeElapsed++
      Log.d(TAG, "Time elapsed: $timeElapsed")
    }

    val localIntent = Intent(DownloadJobIntentService.ACTION_DOWNLOAD_FINISHED)
    localIntent.putExtra(DownloadJobIntentService.EXTRA_ELAPSED_TIME, timeElapsed)
    LocalBroadcastManager.getInstance(this)
        .sendBroadcast(localIntent)
  }
}