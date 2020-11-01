package com.rice.bohai.activity

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.util.Log
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.ChubeiRuleListModel
import com.rice.bohai.model.ChubeiRuleModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_chubei_rule.*

class ChubeiRuleActivity : RiceBaseActivity() {

    //-1储备规则 0仓储券规则 1购货券规则
    private var ruleType = -1

    override fun getLayoutId(): Int {
        return R.layout.activity_chubei_rule
    }

    override fun initView() {
        val ruleUrl = when(ruleType){
            0 -> RiceHttpK.getUrl(Constant.STORAGE_TICKET_RULE)
            1 -> RiceHttpK.getUrl(Constant.GROUP_BUY_TICKET_RULE)
            else -> RiceHttpK.getUrl(Constant.CHUBEI_RULE)
        }
        Http.get {
            url = ruleUrl
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
//                Log.i("$url:->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ChubeiRuleListModel = StringNullAdapter.gson.fromJson(result)
                    val models: ArrayList<ChubeiRuleModel> =
                        model.rules_list as ArrayList<ChubeiRuleModel>
                    if (models.size >= 1) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            textview_chubei_01.text =
                                Html.fromHtml(models[0].title, FROM_HTML_MODE_COMPACT)
                        } else {
                            textview_chubei_01.text =
                                Html.fromHtml(models[0].title)
                        }
                        val strBuilder0 = StringBuilder()
                        for (str in models[0].rules) {
                            strBuilder0.append("${str}<br/>");
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            textview_chubei_02.text =
                                Html.fromHtml(strBuilder0.toString(), FROM_HTML_MODE_COMPACT)
                        } else {
                            textview_chubei_02.text =
                                Html.fromHtml(strBuilder0.toString())
                        }

                        if (models.size > 1){
                            if (Build.VERSION.SDK_INT >= 24) {
                                textview_chubei_03.text =
                                    Html.fromHtml(models[1].title, FROM_HTML_MODE_COMPACT)
                            } else {
                                textview_chubei_03.text =
                                    Html.fromHtml(models[1].title)
                            }
                            val strBuilder1 = StringBuilder();
                            for (str in models[1].rules) {
                                strBuilder1.append("${str}<br/>");
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                textview_chubei_04.text =
                                    Html.fromHtml(strBuilder1.toString(), FROM_HTML_MODE_COMPACT)
                            } else {
                                textview_chubei_04.text =
                                    Html.fromHtml(strBuilder1.toString())
                            }
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

    override fun getIntentData() {
        ruleType = intent.getIntExtra("ruleType",-1)
    }

    override fun clear() {

    }

}