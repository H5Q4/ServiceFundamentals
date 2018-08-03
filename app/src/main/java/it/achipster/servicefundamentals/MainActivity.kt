package it.achipster.servicefundamentals

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.bindServiceBtn
import kotlinx.android.synthetic.main.activity_main.resultTv
import kotlinx.android.synthetic.main.activity_main.startBackgroundServiceBtn
import kotlinx.android.synthetic.main.activity_main.startForegroundServiceBtn
import kotlinx.android.synthetic.main.activity_main.startIntentServiceBtn
import kotlinx.android.synthetic.main.activity_main.startJobIntentServiceBtn
import kotlinx.android.synthetic.main.activity_main.startServiceBtn
import kotlinx.android.synthetic.main.activity_main.stopBackgroundServiceBtn
import kotlinx.android.synthetic.main.activity_main.stopServiceBtn
import kotlinx.android.synthetic.main.activity_main.unbindServiceBtn

class MainActivity : AppCompatActivity() {

  companion object {
    private val TAG: String = MainActivity::class.java.simpleName
    const val EXTRA_RESULT_FROM_SERVICE = "result_from_remote"
    const val MSG_RESULT_FROM_SERVICE = 200
  }

  class ClientSideHander(private val mainActivity: MainActivity) : Handler() {
    override fun handleMessage(msg: Message?) {
      when (msg?.what) {
        MSG_RESULT_FROM_SERVICE -> Toast.makeText(
            mainActivity, msg.data.getString(
            EXTRA_RESULT_FROM_SERVICE
        ), Toast.LENGTH_SHORT
        ).show()
      }
    }
  }

  private lateinit var downloadCompletedReceiver: BroadcastReceiver

  private var bound = false

  private val handler = ClientSideHander(this)

  private val messenger = Messenger(handler)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    initData()
    initViews()
  }

  private fun initData() {
    downloadCompletedReceiver = object : BroadcastReceiver() {
      override fun onReceive(
        context: Context?,
        intent: Intent?
      ) {
        intent?.run {
          val elapsedTime = getIntExtra(DownloadIntentService.EXTRA_ELAPSED_TIME, -1)
          resultTv.text = "Task execution finished in $elapsedTime s"
          //region 测试在动态注册的 BroadcastReceiver 中 bind service
//          context?.bindService(Intent(context, MyBackgroundService::class.java),
//              object : ServiceConnection {
//                override fun onServiceDisconnected(name: ComponentName?) {
//
//                }
//
//                override fun onServiceConnected(
//                  name: ComponentName?,
//                  service: IBinder?
//                ) {
//                  if (service is MyBackgroundService.LocalBinder) {
//                    Log.d(TAG, service.getService().toString())
//                  }
//                }
//              }, Service.BIND_AUTO_CREATE)
          //endregion
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    val intentFilter = IntentFilter(DownloadIntentService.ACTION_DOWNLOAD_FINISHED)
    LocalBroadcastManager.getInstance(this)
        .registerReceiver(downloadCompletedReceiver, intentFilter)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(this)
        .unregisterReceiver(downloadCompletedReceiver)
  }

  private fun initViews() {
    startBackgroundServiceBtn.setOnClickListener {
      startService(Intent(this@MainActivity, MyBackgroundService::class.java))
    }

    stopBackgroundServiceBtn.setOnClickListener {
      stopService(Intent(this@MainActivity, MyBackgroundService::class.java))
    }

    startIntentServiceBtn.setOnClickListener {
      //      DownloadIntentService.startActionFakeDownload(this, 10)
//      DownloadIntentService.startActionFakeDownloadWithResult(this, DownloadResultReceiverCallback(resultTv), 10)
    }

    startForegroundServiceBtn.setOnClickListener {
      startService(Intent(this@MainActivity, ForegroundService::class.java))
    }

    startJobIntentServiceBtn.setOnClickListener {
      DownloadJobIntentService.startActionFakeDownload(this, 15)
    }

    val serviceConnection = object : ServiceConnection {
      override fun onServiceConnected(
        name: ComponentName?,
        service: IBinder?
      ) {
//        if (service is MyBoundService.LocalBinder) {
//          val myBoundService = service.getService()
//          myBoundService.doSomething()
//        }
        bound = true
        val messenger = Messenger(service)
        try {
          messenger.send(Message.obtain().apply {
            what = MyBoundService.MSG_DO_SOMETHING
            data = Bundle().apply { putString(MyBoundService.EXTRA_WORK_NAME, "Main work") }
            replyTo = this@MainActivity.messenger
          })
        } catch (e: RemoteException) {
          e.printStackTrace()
        }
      }

      override fun onServiceDisconnected(name: ComponentName?) {
        bound = false
        Log.d(TAG, "${name?.className} disconnected")
      }
    }

    bindServiceBtn.setOnClickListener {
      bindService(
          Intent(this@MainActivity, MyBoundService::class.java), serviceConnection,
          Service.BIND_AUTO_CREATE // 改为 0，测试现象
      )
    }

    unbindServiceBtn.setOnClickListener {
      if (bound) {
        unbindService(serviceConnection)
      }
    }

    startServiceBtn.setOnClickListener {
      startService(Intent(this@MainActivity, MyBoundService::class.java))
    }

    stopServiceBtn.setOnClickListener {
      stopService(Intent(this@MainActivity, MyBoundService::class.java))
    }
  }

}
