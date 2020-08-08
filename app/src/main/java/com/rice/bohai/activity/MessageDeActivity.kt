package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.util.Log
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.MessageDeModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_message_de.*

@SuppressLint("Registered")
class MessageDeActivity : RiceBaseActivity() {

    var id = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_message_de
    }

    override fun initView() {
        getMessageDe()
    }

    override fun getIntentData() {
        id = intent.extras?.getString("id", "") ?: ""
    }

    private fun getMessageDe() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MESSAGE_DETAIL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "id" - id
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val de: MessageDeModel = StringNullAdapter.gson.fromJson(result)
                    val model = de.model
                    toolbar.setTitle(model.name)
                    textMessageTitle.text = model.name
                    textContent.text = model.content
                    textTime.text = model.created_at
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

    override fun clear() {

    }

}