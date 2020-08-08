package com.rice.bohai.activity

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.BuyHistoryAdapter
import com.rice.bohai.model.PintuanBuyListModel
import com.rice.bohai.model.PintuanBuyModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_history_search.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.text.SimpleDateFormat

class BuyHistoryActivity : RiceBaseActivity() {

    lateinit var buyHistoryAdapter: BuyHistoryAdapter
    var listHistory: MutableList<PintuanBuyModel> = ArrayList()
    var page = 1

    override fun getLayoutId(): Int {
        return R.layout.activity_history_search
    }

    override fun initView() {
        llSelectDate.setOnClickListener {
            TimePickerBuilder(this, OnTimeSelectListener { date, v ->
                initHistory("", SimpleDateFormat("yyyy-MM-dd").format(date))
            })
                .setType(booleanArrayOf(true, true, true, false, false, false))
                .build().show()
        }
        recycler.layoutManager = LinearLayoutManager(mContext)
        buyHistoryAdapter = BuyHistoryAdapter(mContext, listHistory)
        buyHistoryAdapter.bindToRecyclerView(recycler)
        buyHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = buyHistoryAdapter
        refresh.setOnLoadMoreListener {
            page++
            initHistory()
        }
        refresh.setOnRefreshListener {
            page = 1
            initHistory()
        }
        initHistory()
    }

    /**
     * 购买记录
     */
    private fun initHistory(word: String = "", day: String = "") {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_BUY_HISTORY)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                if (TextUtils.isNotEmpty(day)) {
                    "start_time" - day
                }
                "page" - page.toString()
            }
            tag = "history"
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", "${url},${day},${page}")
                buyHistoryAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                Log.i("hel->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: PintuanBuyListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listHistory.clear()
                    }
                    listHistory.addAll(model.lists)
                    buyHistoryAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                buyHistoryAdapter.setEmptyView(R.layout.include_fail)
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