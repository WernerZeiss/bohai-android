package com.rice.bohai.activity

import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.CardListAdapter
import com.rice.bohai.listener.OnCardListListener
import com.rice.bohai.model.CardListModel
import com.rice.bohai.model.CardModel
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_card_manager.*
import java.nio.charset.Charset
import kotlin.math.log

/**
 * @author CWQ
 * @date 2020/8/8
 * 银行卡管理
 */
class CardManagerActivity : RiceBaseActivity() {

    private var mAdapter: CardListAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_card_manager
    }

    override fun initView() {
    }


    override fun onResume() {
        super.onResume()
        getCardList()
    }

    private fun getCardList() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_MY_BANK_CARD_LIST)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { bytes ->
                val result = RiceHttpK.getResult(mContext, bytes)
                if (!TextUtils.isEmpty(result)) {
                    val model: CardListModel = StringNullAdapter.gson.fromJson(result)
                    if (model.lists != null) {
                        if (mAdapter == null) {
                            mAdapter = CardListAdapter(model.lists)
                            mAdapter?.setOnCardListener(object : OnCardListListener {

                                override fun onClickDefault(card: CardModel) {
                                    //设置默认卡片
                                    setDefaultCard(card.id)
                                }

                                override fun onAddCard() {
                                    //添加银行卡
                                    ActivityUtils.openActivity(
                                        this@CardManagerActivity,
                                        AddBankCardActivity::class.java
                                    )
                                }
                            })
                            rv_cards.layoutManager = LinearLayoutManager(this@CardManagerActivity)
                            rv_cards.adapter = mAdapter
                        } else {
                            mAdapter?.update(model.lists)
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

    /**
     * 设置默认银行卡
     */
    private fun setDefaultCard(id: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.DEFAULT_BANK)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "id" - id
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    getCardList()
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