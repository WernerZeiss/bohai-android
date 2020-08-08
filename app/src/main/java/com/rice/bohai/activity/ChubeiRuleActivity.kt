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

    override fun getLayoutId(): Int {
        return R.layout.activity_chubei_rule
    }

    override fun initView() {
        Http.post {
            url =
                RiceHttpK.getUrl(Constant.CHUBEI_RULE)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ChubeiRuleListModel = StringNullAdapter.gson.fromJson(result)
                    val models: ArrayList<ChubeiRuleModel> =
                        model.rules_list as ArrayList<ChubeiRuleModel>
                    if (models.size >= 2) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            textview_chubei_01.text =
                                Html.fromHtml(models[0].title, FROM_HTML_MODE_COMPACT)
                        } else {
                            textview_chubei_01.text =
                                Html.fromHtml(models[0].title)
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            textview_chubei_02.text =
                                Html.fromHtml(models[0].rules[0], FROM_HTML_MODE_COMPACT)
                        } else {
                            textview_chubei_02.text =
                                Html.fromHtml(models[0].rules[0])
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            textview_chubei_03.text =
                                Html.fromHtml(models[1].title, FROM_HTML_MODE_COMPACT)
                        } else {
                            textview_chubei_03.text =
                                Html.fromHtml(models[1].title)
                        }
                        val strBuilder = StringBuilder();
                        for (str in models[1].rules) {
                            strBuilder.append("${str}<br/>");
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            textview_chubei_04.text =
                                Html.fromHtml(strBuilder.toString(), FROM_HTML_MODE_COMPACT)
                        } else {
                            textview_chubei_04.text =
                                Html.fromHtml(strBuilder.toString())
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
    }

    override fun clear() {

    }

}