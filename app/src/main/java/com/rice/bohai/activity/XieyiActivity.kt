package com.rice.bohai.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.View
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.ChubeiRuleListModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_xieyi.*

class XieyiActivity : RiceBaseActivity() {

    var type: Int = 0
    private val xieYiBaseUrl = "https://xhrdadmin.scxhrd.com"

    override fun getLayoutId(): Int {
        return R.layout.activity_xieyi
    }

    override fun initView() {
        var process = 0
        Thread(Runnable {
            Thread.sleep(30)
            runOnUiThread(Runnable {
                process += 1
                progressbar.progress = process
            })
        }).start()
        initXieyi()
    }

    override fun getIntentData() {
        type = intent.getIntExtra("type", 0)
    }

    override fun clear() {

    }

    private fun initXieyi() {
        if (type == 1){
            val intent = Intent().apply {
                action = "android.intent.action.VIEW"
                data = Uri.parse(xieYiBaseUrl + MyApplication.instance.userInfo?.agreement_doc)
            }
            startActivity(intent)
            finish()
        }else{
            Http.post {
                url =
                    RiceHttpK.getUrl(Constant.CHUBEI_RULE)
                onSuccess { byts ->
                    Log.i("hel->", url)
                    progressbar.visibility = View.GONE
                    val result = RiceHttpK.getResult(mContext, byts)
                    if (TextUtils.isNotEmpty(result)) {
                        val model: ChubeiRuleListModel = StringNullAdapter.gson.fromJson(result)
                        if (Build.VERSION.SDK_INT >= 24) {
                            if (type == 1) {
//                            webview_xieyi.visibility = View.GONE
//                            textview_xieyi.text =
//                                Html.fromHtml(model.agreement, Html.FROM_HTML_MODE_COMPACT)
                            } else if (type == 2) {
                                textview_xieyi.text =
                                    Html.fromHtml(
                                        model.private_agreement,
                                        Html.FROM_HTML_MODE_COMPACT
                                    )
                            }
                        } else {
                            if (type == 1) {
//                            webview_xieyi.visibility = View.GONE
//                            textview_xieyi.text =
//                                Html.fromHtml(model.agreement)
                            } else if (type == 2) {
                                textview_xieyi.text =
                                    Html.fromHtml(model.private_agreement)
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
    }
}