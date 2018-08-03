package it.achipster.servicefundamentals

import android.widget.TextView
import java.lang.Exception
import java.lang.ref.WeakReference

class DownloadResultReceiverCallback(resultTv: TextView) : ServiceResultReceiver.ResultReceiverCallback<Int> {

  private val resultTvReference: WeakReference<TextView> = WeakReference(resultTv)

  override fun onSuccess(data: Int) {
    resultTvReference.get()
        ?.text = "Task execution finished in $data s"
  }

  override fun onError(error: Exception) {
    resultTvReference.get()
        ?.text = "Task execution failed"
  }
}