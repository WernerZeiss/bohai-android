package com.rice.bohai.activity

import android.annotation.SuppressLint
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
import com.rice.bohai.adapter.CashoutHistoryAdapter
import com.rice.bohai.adapter.RechargeHistoryAdapter
import com.rice.bohai.model.CashoutListModel
import com.rice.bohai.model.CashoutModel
import com.rice.bohai.model.RechargeHistoryListModel
import com.rice.bohai.model.RechargeHistoryModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_recharge_history.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.text.SimpleDateFormat

@SuppressLint("Registered")
class RechargeHistoryActivity : RiceBaseActivity() {

    //充值记录
    lateinit var rechargeHistoryAdapter: RechargeHistoryAdapter
    var listRechargeHistory: MutableList<RechargeHistoryModel> = ArrayList()

    //提现记录
    var listCashout: MutableList<CashoutModel> = ArrayList()
    lateinit var cashoutHistoryAdapter: CashoutHistoryAdapter

    var page = 1
    var start_time = ""
    var end_time = ""
    var mode = MODE_RECHARGE

    companion object {
        const val MODE_RECHARGE = 0
        const val MODE_CASHOUT = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_recharge_history
    }

    override fun initView() {
        recycler.layoutManager = LinearLayoutManager(mContext)
        initMode()
        refresh.setOnLoadMoreListener {
            page++
            getData()
        }
        refresh.setOnRefreshListener {
            page = 1
            getData()
        }
        imgStart.setOnClickListener {
            showTimerPicker(true)
        }
        textStartDate.setOnClickListener {
            showTimerPicker(true)
        }
        imgEnd.setOnClickListener {
            showTimerPicker(false)
        }
        textEndDate.setOnClickListener {
            showTimerPicker(false)
        }
        textClear.setOnClickListener {
            start_time = ""
            end_time = ""
            textStartDate.text = "开始日期"
            textEndDate.text = "结束日期"
            page = 1
            getData()
        }
        textSearch.setOnClickListener {
            page = 1
            getData()
        }
        getData()
    }

    private fun initMode() {
        when (mode) {
            MODE_RECHARGE -> {
                //充值记录
                toolbar.setTitle("充值记录")
                rechargeHistoryAdapter = RechargeHistoryAdapter(mContext, listRechargeHistory)
                rechargeHistoryAdapter.bindToRecyclerView(recycler)
                rechargeHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = rechargeHistoryAdapter
            }
            MODE_CASHOUT -> {
                //提现记录
                toolbar.setTitle("提现记录")
                cashoutHistoryAdapter = CashoutHistoryAdapter(mContext, listCashout)
                cashoutHistoryAdapter.bindToRecyclerView(recycler)
                cashoutHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = cashoutHistoryAdapter
            }
        }
    }

    /**
     * 显示日期选择器
     */
    @SuppressLint("SimpleDateFormat")
    private fun showTimerPicker(isStart: Boolean) {
        var title = ""
        if (isStart) {
            title = "请选择开始日期"
        } else {
            title = "请选择结束日期"
        }
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            if (isStart) {
                start_time = SimpleDateFormat("yyyy-MM-dd").format(date)
                textStartDate.text = start_time
                //                showTimerPicker(false)
            } else {
                end_time = SimpleDateFormat("yyyy-MM-dd").format(date)
                textEndDate.text = end_time
            }
        })
                .setTitleText(title)
                .setType(booleanArrayOf(true, true, true, false, false, false)) // 只显示时分
                .build().show()
    }

    /**
     * 充值记录
     */
    @SuppressLint("SetTextI18n")
    private fun getData() {
        if (TextUtils.isNotEmpty(start_time) && TextUtils.isEmpty(end_time)) {
            ToastUtil.showShort("请选择结束日期")
        }
        if (TextUtils.isEmpty(start_time) && TextUtils.isNotEmpty(end_time)) {
            ToastUtil.showShort("请选择开始日期")
        }
        Http.post {
            when (mode) {
                MODE_RECHARGE -> {
                    url = RiceHttpK.getUrl(Constant.RECHARGE_RECORD)
                    //充值记录
                }
                MODE_CASHOUT -> {
                    //提现记录
                    url = RiceHttpK.getUrl(Constant.PRESENT_RECORD_LIST)
                }
            }
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
                if (TextUtils.isNotEmpty(start_time)) {
                    "start_time" - start_time
                }
                if (TextUtils.isNotEmpty(end_time)) {
                    "end_time" - end_time
                }
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                if (::rechargeHistoryAdapter.isInitialized) {
                    rechargeHistoryAdapter.setEmptyView(R.layout.include_no_data)
                }
                if (::cashoutHistoryAdapter.isInitialized) {
                    cashoutHistoryAdapter.setEmptyView(R.layout.include_no_data)
                }
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    when (mode) {
                        MODE_RECHARGE -> {
                            //充值记录
                            val model: RechargeHistoryListModel = StringNullAdapter.gson.fromJson(result)
                            if (page == 1) {
                                listRechargeHistory.clear()
                            }
                            listRechargeHistory.addAll(model.lists)
                            rechargeHistoryAdapter.notifyDataSetChanged()
                            if (TextUtils.isNotEmpty(start_time) && TextUtils.isNotEmpty(end_time)) {
                                textTotal.text = "${start_time}至${end_time}期间累计充值：${model.total_price}元"
                            } else {
                                textTotal.text = "累计充值：${model.total_price}元"
                            }
                        }
                        MODE_CASHOUT -> {
                            //提现记录
                            val model: CashoutListModel = StringNullAdapter.gson.fromJson(result)
                            if (page == 1) {
                                listCashout.clear()
                            }
                            listCashout.addAll(model.lists)
                            cashoutHistoryAdapter.notifyDataSetChanged()
                            if (TextUtils.isNotEmpty(start_time) && TextUtils.isNotEmpty(end_time)) {
                                textTotal.text = "${start_time}至${end_time}期间累计提现：${model.total_price}元"
                            } else {
                                textTotal.text = "累计提现：${model.total_price}元"
                            }
                        }
                    }
                }
            }
            onFail { error ->
                if (::rechargeHistoryAdapter.isInitialized) {
                    rechargeHistoryAdapter.setEmptyView(R.layout.include_fail)
                }
                if (::cashoutHistoryAdapter.isInitialized) {
                    cashoutHistoryAdapter.setEmptyView(R.layout.include_fail)
                }
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
        mode = intent.extras?.getInt("mode", MODE_RECHARGE) ?: MODE_RECHARGE
    }

    override fun clear() {

    }

}