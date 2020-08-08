package com.rice.bohai.fragment

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.IntegralHistoryAdapter
import com.rice.bohai.adapter.THJEAdapter
import com.rice.bohai.adapter.XJSYAdapter
import com.rice.bohai.model.*
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.include_smr_recycler_match.*

/**
 * 数据展示Fragment
 */
class DataFragment : BaseImmersionFragment() {

    var mode = MODE_NULL
    var page = 1

    var listScoreHistory: MutableList<IntegralHistoryModel> = ArrayList()
    lateinit var scoreHistoryAdapter: IntegralHistoryAdapter

    var listXJSY: MutableList<XJSYModel> = ArrayList()
    lateinit var xjsyAdapter: XJSYAdapter

    var listTHJE: MutableList<THJEModel> = ArrayList()
    lateinit var thjeAdapter: THJEAdapter

    companion object {
        const val MODE_NULL = 0
        const val MODE_XJSY = 1 //现金收益
        const val MODE_JFSY = 2 //积分收益
        const val MODE_THJE = 3 //提货金额

        fun newInstance(mode: Int): DataFragment {
            val args = Bundle()
            args.putInt("mode", mode)
            val fragment = DataFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override val contentViewLayoutID: Int
        get() = R.layout.include_smr_recycler_match

    override fun initView() {
        mode = arguments?.getInt("mode", MODE_NULL) ?: MODE_NULL
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        when (mode) {
            MODE_XJSY -> {
                //现金收益
                recycler.layoutManager = LinearLayoutManager(mContext)
                xjsyAdapter = XJSYAdapter(mContext, listXJSY)
                xjsyAdapter.bindToRecyclerView(recycler)
                xjsyAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = xjsyAdapter
            }
            MODE_JFSY -> {
                //积分收益
                recycler.layoutManager = LinearLayoutManager(mContext)
                scoreHistoryAdapter = IntegralHistoryAdapter(mContext, listScoreHistory)
                scoreHistoryAdapter.bindToRecyclerView(recycler)
                scoreHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = scoreHistoryAdapter
            }
            MODE_THJE -> {
                //提货金额
                recycler.layoutManager = LinearLayoutManager(mContext)
                thjeAdapter = THJEAdapter(mContext, listTHJE)
                thjeAdapter.bindToRecyclerView(recycler)
                thjeAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = thjeAdapter
            }
        }
        initData()
    }

    private fun initData() {
        when (mode) {
            MODE_XJSY -> {
                //现金收益
                initXJSY()
            }
            MODE_JFSY -> {
                //积分收益
                initHistory("3")
            }
            MODE_THJE -> {
                //提货金额
                initTHJE()
            }
        }
    }

    /**
     * 提货金额
     */
    private fun initTHJE() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_SETTLEMENT_POSITION)
            params {
                "page" - page.toString()
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                thjeAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: THJEListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listTHJE.clear()
                    }
                    listTHJE.addAll(model.lists)
                    thjeAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                thjeAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 现金收益
     */
    private fun initXJSY() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRICE_CHANGE_RECORD)
            params {
                "page" - page.toString()
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                xjsyAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: XJSYListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listXJSY.clear()
                    }
                    listXJSY.addAll(model.lists)
                    xjsyAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                xjsyAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 积分收益
     * @param type 1兑换记录  2积分记录 3积分收益
     */
    private fun initHistory(type: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_INTEGRAL_RECORD)
            params {
                "page" - page.toString()
                "type " - type
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                scoreHistoryAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: IntegralHistoryListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listScoreHistory.clear()
                    }
                    listScoreHistory.addAll(model.lists)
                    scoreHistoryAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                scoreHistoryAdapter.setEmptyView(R.layout.include_fail)
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