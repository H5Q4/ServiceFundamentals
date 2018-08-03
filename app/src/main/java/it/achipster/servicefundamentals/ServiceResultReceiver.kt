package it.achipster.servicefundamentals

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import java.lang.Exception

open class ServiceResultReceiver<T>(handler: Handler?) : ResultReceiver(handler) {

  companion object {
    const val RESULT_CODE_OK = 200
    const val RESULT_CODE_ERROR = 500
    const val PARAM_EXCEPTION = "exception"
    const val PARAM_RESULT = "result"
  }

  private var callback: ResultReceiverCallback<T>? = null

  fun setCallback(receiverCallback: ResultReceiverCallback<T>) {
    callback = receiverCallback
  }

  override fun onReceiveResult(
    resultCode: Int,
    resultData: Bundle?
  ) {
    callback?.let {
      if (resultCode == RESULT_CODE_OK) {
        it.onSuccess(resultData?.getSerializable(PARAM_RESULT) as T)
      } else {
        it.onError(resultData?.getSerializable(PARAM_EXCEPTION) as Exception)
      }

    }
  }

  interface ResultReceiverCallback<T> {
    fun onSuccess(data: T)
    fun onError(error: Exception)
  }
}