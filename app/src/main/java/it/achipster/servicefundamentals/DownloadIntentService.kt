package it.achipster.servicefundamentals

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast

class DownloadIntentService : IntentService("DownloadIntentService") {
  override fun onCreate() {
    super.onCreate()
    Toast.makeText(this, "Task execution started", Toast.LENGTH_SHORT)
        .show()
    Log.d(TAG, "onCreate, thread => ${Thread.currentThread().name}")
  }

  override fun onHandleIntent(intent: Intent?) {
    Log.d(TAG, "onHandleIntent, thread => ${Thread.currentThread().name}")

    val resultReceiver = intent?.getParcelableExtra(EXTRA_RESULT_RECEIVER) as ResultReceiver
    when (intent.action) {
      ACTION_FAKE_DOWNLOAD -> {
        val sleepTime = intent.getIntExtra(EXTRA_SLEEP_TIME, 0)
//        handleActionFakeDownload(sleepTime)
        handleActionFakeDownloadWithResult(sleepTime, resultReceiver)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Toast.makeText(this, "Task execution finished", Toast.LENGTH_SHORT)
        .show()
    Log.d(TAG, "onDestroy, thread => ${Thread.currentThread().name}")
  }

  private fun handleActionFakeDownload(sleepTime: Int) {
    var timeElapsed = 0
    while (sleepTime > timeElapsed) {
      Thread.sleep(1000)
      timeElapsed++
      Log.d(TAG, "Time elapsed: $timeElapsed")
    }

    sendResultByLocalBroadcastReceiver(timeElapsed)
  }

  private fun handleActionFakeDownloadWithResult(sleepTime: Int, resultReceiver: ResultReceiver) {
    var timeElapsed = 0
    while (sleepTime > timeElapsed) {
      Thread.sleep(1000)
      timeElapsed++
      Log.d(TAG, "Time elapsed: $timeElapsed")
    }

    sendResultByResultReceiver(timeElapsed, resultReceiver)
  }

  private fun sendResultByResultReceiver(timeElapsed: Int, receiver: ResultReceiver) {
    val bundle = Bundle()
    bundle.putSerializable(ServiceResultReceiver.PARAM_RESULT, timeElapsed)
    receiver.send(ServiceResultReceiver.RESULT_CODE_OK, bundle)
  }

  private fun sendResultByLocalBroadcastReceiver(timeElapsed: Int) {
    val localIntent = Intent(ACTION_DOWNLOAD_FINISHED)
    localIntent.putExtra(EXTRA_ELAPSED_TIME, timeElapsed)
    LocalBroadcastManager.getInstance(this)
        .sendBroadcast(localIntent)
  }

  companion object {
    private const val ACTION_FAKE_DOWNLOAD = "it.achipster.servicefundamentals.action.FakeDownload"
    private const val EXTRA_SLEEP_TIME = "it.achipster.servicefundamentals.extra.SleepTime"
    const val ACTION_DOWNLOAD_FINISHED = "it.achipster.servicefundamentals.action.DownloadFinished"
    const val EXTRA_ELAPSED_TIME = "it.achipster.servicefundamentals.extra.ElapsedTime"
    const val EXTRA_RESULT_RECEIVER = "it.achipster.servicefundamentals.extra.ResultReceiver"

    private val TAG: String = DownloadIntentService::class.java.simpleName

    /**
     * Starts this service to perform action ACTION_FAKE_DOWNLOAD with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     */
    @JvmStatic fun startActionFakeDownload(
      context: Context,
      sleepTime: Int
    ) {
      val intent = Intent(context, DownloadIntentService::class.java).apply {
        action = ACTION_FAKE_DOWNLOAD
        putExtra(EXTRA_SLEEP_TIME, sleepTime)
      }
      context.startService(intent)
    }

    @JvmStatic fun startActionFakeDownloadWithResult(
      context: Context,
      receiverCallback: ServiceResultReceiver.ResultReceiverCallback<Int>,
      sleepTime: Int
    ) {
      val resultReceiver = ServiceResultReceiver<Int>(Handler(context.mainLooper))
      resultReceiver.setCallback(receiverCallback)
      val intent = Intent(context, DownloadIntentService::class.java).apply {
        action = ACTION_FAKE_DOWNLOAD
        putExtra(EXTRA_SLEEP_TIME, sleepTime)
        putExtra(EXTRA_RESULT_RECEIVER, resultReceiver)
      }
      context.startService(intent)
    }

  }

}
