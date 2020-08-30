package com.rice.bohai.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.dialog.SellSuccessDialog
import com.rice.bohai.model.SellPromptModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil

@SuppressLint("Registered")
class SellPromptService : Service() {

    var context: Context? = null
    var isFirst = true
    lateinit var thisBinder: SocketBinder
    lateinit var dialog: SellSuccessDialog

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        dialog = SellSuccessDialog(context!!)
        if (intent != null && isFirst) {
            start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun stopService(name: Intent): Boolean {
        Log.d("---SellPromptService---", "stopService")
        return super.stopService(name)
    }

    override fun onDestroy() {
        Log.d("---SellPromptService---", "onDestroy")
        super.onDestroy()
    }

    @SuppressLint("LongLogTag")
    private fun start() {
        if (isFirst) {
            Thread {
                while (true) {
                    if (MyApplication.instance.userInfo != null && TextUtils.isNotEmpty(MyApplication.instance.userInfo?.access_token)) {
                        initData()
                    }
                    Thread.sleep(3000)
                }
            }.start()
            isFirst = false
        }
    }

    /**
     * 轮询交易结果
     */
    private fun initData() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.LOAD_MESSAGE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { byts ->
//                Log.i("hel->", url)
                val result = RiceHttpK.getResult(applicationContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: SellPromptModel = StringNullAdapter.gson.fromJson(result)
                    if (TextUtils.isNotEmpty(model.model.content)) {
                        dialog.setTitleText(model.model.name)
                                .setBigText(model.model.intro)
                                .setInfo(model.model.content)
                        if (!dialog.isShowing) {
                            dialog.show()
                        }
                    }
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return thisBinder
    }

    interface OnRepackListener {
        fun onRepack(messageJson: String)
    }

    inner class SocketBinder : Binder() {

        val SellPromptService = this@SellPromptService

    }

    companion object {
        val ACTION = "com.rice.bohai.SellPromptService" //TODO:替换为正式项目包名
    }

}
