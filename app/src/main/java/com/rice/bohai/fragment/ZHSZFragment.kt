package com.rice.bohai.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.CashoutActivity
import com.rice.bohai.activity.RechargeActivity
import com.rice.bohai.activity.PintuanMXActivity
import com.rice.bohai.adapter.PHZHAdapter
import com.rice.bohai.adapter.XJSYAdapter
import com.rice.bohai.adapter.ZHSZAdapter
import com.rice.bohai.model.*
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.FragmentHelper
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.fragment_zhsz.*

/**
 * 账户市值
 */
class ZHSZFragment : BaseImmersionFragment() {

    var mode = MODE_XJZH

    lateinit var zhszAdapter: ZHSZAdapter
    var listZHSZ: MutableList<WDCCModel> = ArrayList()
    lateinit var xjsyAdapter: XJSYAdapter
    var listXJSY: MutableList<XJSYModel> = ArrayList()
    lateinit var xsphAdapter: PHZHAdapter
    var listProfitHistory: MutableList<ProfitHistoryModel> = ArrayList()

    var xsphFragment = XSPHFragment()

    var page = 1

    init {
        isContentInvade = true
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = true
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    companion object {

        const val MODE_XJZH = 0
        const val MODE_XSPH = 1
        const val MODE_THZH = 2

        fun newInstance(mode: Int): ZHSZFragment {
            val args = Bundle()
            args.putInt("mode", mode)
            val fragment = ZHSZFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_zhsz

    override fun initView() {
        mode = arguments?.getInt("mode", MODE_XJZH) ?: MODE_XJZH
        recycler.layoutManager = LinearLayoutManager(mContext)
        zhszAdapter = ZHSZAdapter(mContext, listZHSZ)
        xjsyAdapter = XJSYAdapter(mContext, listXJSY)
        xsphAdapter = PHZHAdapter(mContext, listProfitHistory)
        zhszAdapter.bindToRecyclerView(recycler)
        zhszAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        xjsyAdapter.bindToRecyclerView(recycler)
        xjsyAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        xsphAdapter.bindToRecyclerView(recycler)
        xsphAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        textBtn.setOnClickListener {
            when (mode) {
                //                MODE_ZHSZ -> {
                //                    //划转积分
                //                    var b = Bundle()
                //                    b.putInt("mode", MulModeSettingActivity.MODE_ZJHZ)
                //                    ActivityUtils.openActivity(mContext, MulModeSettingActivity::class.java, b)
                //                }
                MODE_XJZH -> {
                    //现金充值
                    ActivityUtils.openActivity(mContext, RechargeActivity::class.java)
                }
                MODE_XSPH -> {
                    //销售配货查看明细
                    ActivityUtils.openActivity(mContext, PintuanMXActivity::class.java)
                }
            }
        }
        textBtn2.setOnClickListener {
            //提现
            ActivityUtils.openActivity(mContext, CashoutActivity::class.java)
        }
        initMode()
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        FragmentHelper.switchFragment(xsphFragment, mActivity as AppCompatActivity, R.id.frameXSPH)
    }

    private fun initData() {
        when (mode) {
            //            MODE_ZHSZ -> {
            //                initZHSZ()
            //            }
            MODE_XJZH -> {
                initXJSY()
            }
            MODE_THZH -> {
                initPHZH()
            }
            MODE_XSPH -> {
                if (xsphFragment.isResumed) {
                    xsphFragment.initData()
                }
            }
        }
    }

    private fun initMode() {
        page = 1
        when (mode) {
            //            MODE_ZHSZ -> {
            //                textTag.text = "账户市值"
            //                textBtn.visibility = View.VISIBLE
            //                textBtn2.visibility = View.GONE
            //                textBtn.text = "划转积分"
            //                recycler.adapter = zhszAdapter
            //            }
            MODE_XJZH -> {
                textTag.text = "现金账户"
                textBtn.text = "充值"
                if (MyApplication.instance.userInfo?.is_start_recharge == 1) {
                    textBtn.visibility = View.VISIBLE
                } else {
                    textBtn.visibility = View.GONE
                }
                textBtn2.visibility = View.VISIBLE
                recycler.adapter = xjsyAdapter
                frameXSPH.visibility = View.GONE
            }
            MODE_THZH -> {
                textTag.text = "提货券"
                textBtn.visibility = View.GONE
                recycler.adapter = xsphAdapter
                frameXSPH.visibility = View.GONE
            }
            MODE_XSPH -> {
                frameXSPH.visibility = View.VISIBLE
            }
        }
        textTag.visibility = View.GONE
        initData()
    }

    override fun onStop() {
        super.onStop()
        Http.getRequestQueue().cancelAll("thje")
        Http.getRequestQueue().cancelAll("xjsy")
        Http.getRequestQueue().cancelAll("zhsz")
    }

    /**
     * 账户市值
     */
    private fun initZHSZ() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_POSITION)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            tag = "zhsz"
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                zhszAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: WDCCListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listZHSZ.clear()
                    }
                    listZHSZ.addAll(model.lists)
                    zhszAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                zhszAdapter.setEmptyView(R.layout.include_fail)
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
            tag = "xjsy"
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
     * 提货券
     */
    fun initPHZH() {
        Http.post {
            url = RiceHttpK.getUrl( Constant.MY_PROFIT_RECORD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            tag = "thje"
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                xsphAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ProfitHistoryListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listProfitHistory.clear()
                    }
                    listProfitHistory.addAll(model.lists)
                    xsphAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                xsphAdapter.setEmptyView(R.layout.include_fail)
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