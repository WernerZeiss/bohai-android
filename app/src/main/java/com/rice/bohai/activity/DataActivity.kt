package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.activity.BannerDetailActivity
import com.rice.activity.WebViewActivity
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.*
import com.rice.bohai.custom.SpaceItemDecoration2
import com.rice.bohai.model.*
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.activity_smr_recycler_match_line.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*

@SuppressLint("Registered")
class DataActivity : RiceBaseActivity() {

    var mode = MODE_SHARE_BG
    var page = 1

    //邀请好友背景图
    var listShare: MutableList<ShareBGModel> = ArrayList()
    lateinit var shareBGAdapter: ShareBGAdapter

    //销售配货详情
    lateinit var phDeAdapter: PHDeAdapter
    var listPHDe: MutableList<PHDeModel> = ArrayList()

    //充值记录
    lateinit var rechargeHistoryAdapter: RechargeHistoryAdapter
    var listRechargeHistory: MutableList<RechargeHistoryModel> = ArrayList()

    //提现记录
    var listCashout: MutableList<CashoutModel> = ArrayList()
    lateinit var cashoutHistoryAdapter: CashoutHistoryAdapter

    //帮助列表
    var helpList: MutableList<HelpModel> = ArrayList()
    lateinit var helpAdapter: HelpAdapter

    companion object {
        const val MODE_SHARE_BG = 1 //邀请好友背景图
        const val MODE_XSPHDE = 2 //销售配货明细
        const val MODE_RECHARGE_HISTORY = 3 //充值记录
        const val MODE_CASHOUT_HISTORY = 4 //提现记录
        const val MODE_HELP_LIST = 5 //帮助列表
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_smr_recycler_match_line
    }

    override fun initView() {
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        initMode()
    }

    private fun initMode() {
        when (mode) {
            MODE_SHARE_BG -> {
                //邀请好友背景图
                toolbar.setTitle("邀请好友")
                var lp = recycler.layoutParams as SmartRefreshLayout.LayoutParams
                lp.marginEnd = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.topMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.bottomMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.marginStart = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                recycler.layoutParams = lp
                recycler.layoutManager = GridLayoutManager(mContext, 3)
                shareBGAdapter = ShareBGAdapter(mContext, listShare)
                shareBGAdapter.bindToRecyclerView(recycler)
                shareBGAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = shareBGAdapter
                recycler.addItemDecoration(SpaceItemDecoration2(mContext.resources.getDimensionPixelOffset(R.dimen.dp_4)))
            }
            MODE_XSPHDE -> {
                //销售配货明细
                toolbar.setTitle("配货券明细")
                var lp = recycler.layoutParams as SmartRefreshLayout.LayoutParams
                lp.marginEnd = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.topMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.bottomMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.marginStart = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                recycler.layoutParams = lp
                recycler.layoutManager = LinearLayoutManager(mContext)
                phDeAdapter = PHDeAdapter(mContext, listPHDe)
                phDeAdapter.bindToRecyclerView(recycler)
                phDeAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = phDeAdapter
                //                recycler.addItemDecoration(SpaceItemDecoration2(mContext.resources.getDimensionPixelOffset(R.dimen.dp_4)))
            }
            MODE_RECHARGE_HISTORY -> {
                //充值记录
                toolbar.setTitle("充值记录")
                var lp = recycler.layoutParams as SmartRefreshLayout.LayoutParams
                lp.marginEnd = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.topMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.bottomMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.marginStart = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                recycler.layoutParams = lp
                recycler.layoutManager = LinearLayoutManager(mContext)
                rechargeHistoryAdapter = RechargeHistoryAdapter(mContext, listRechargeHistory)
                rechargeHistoryAdapter.bindToRecyclerView(recycler)
                rechargeHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = rechargeHistoryAdapter
            }
            MODE_CASHOUT_HISTORY -> {
                //提现记录
                toolbar.setTitle("提现记录")
                var lp = recycler.layoutParams as SmartRefreshLayout.LayoutParams
                lp.marginEnd = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.topMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.bottomMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.marginStart = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                recycler.layoutParams = lp
                recycler.layoutManager = LinearLayoutManager(mContext)
                cashoutHistoryAdapter = CashoutHistoryAdapter(mContext, listCashout)
                cashoutHistoryAdapter.bindToRecyclerView(recycler)
                cashoutHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = cashoutHistoryAdapter
            }
            MODE_HELP_LIST -> {
                //帮助列表
                toolbar.setTitle("新手帮助")
                var lp = recycler.layoutParams as SmartRefreshLayout.LayoutParams
                lp.marginEnd = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.topMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.bottomMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                lp.marginStart = mContext.resources.getDimensionPixelOffset(R.dimen.dp_8)
                recycler.layoutParams = lp
                recycler.layoutManager = LinearLayoutManager(mContext)
                helpAdapter = HelpAdapter(mContext, helpList)
                helpAdapter.setOnItemClickListener { adapter, view, position ->
                    var b = Bundle()
                    b.putBoolean("textX", true)
                    b.putString("url", helpList[position].detail)
                    b.putString("title", helpList[position].title)
                    ActivityUtils.openActivity(mContext, BannerDetailActivity::class.java, b)
                }
                helpAdapter.bindToRecyclerView(recycler)
                helpAdapter.setEmptyView(R.layout.loading_dialog_gray2)
                recycler.adapter = helpAdapter
            }
        }
        initData()
    }

    private fun initData() {
        when (mode) {
            MODE_SHARE_BG -> {
                //邀请好友背景图
                getShareBG()
            }
            MODE_XSPHDE -> {
                //销售配货明细
                getXPSHDe()
            }
            MODE_RECHARGE_HISTORY -> {
                //充值记录
                getRechargeHistory()
            }
            MODE_CASHOUT_HISTORY -> {
                //提现记录
                getCashoutHistory()
            }
            MODE_HELP_LIST -> {
                //帮助列表
                getHelpList()
            }
        }
    }

    /**
     * 帮助列表
     */
    private fun getHelpList() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.HELP_CENTER_LIST)
            params {
                "page" - page.toString()
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                helpAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: HelpListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        helpList.clear()
                    }
                    helpList.addAll(model.lists)
                    helpAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                helpAdapter.setEmptyView(R.layout.include_fail)
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
     * 提现记录
     */
    private fun getCashoutHistory() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRESENT_RECORD_LIST)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                cashoutHistoryAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: CashoutListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listCashout.clear()
                    }
                    listCashout.addAll(model.lists)
                    cashoutHistoryAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                cashoutHistoryAdapter.setEmptyView(R.layout.include_fail)
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
     * 销售配货明细
     */
    private fun getXPSHDe() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PROFIT_RECORD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                phDeAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: PHDeListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listPHDe.clear()
                    }
                    listPHDe.addAll(model.lists)
                    phDeAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                phDeAdapter.setEmptyView(R.layout.include_fail)
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
     * 邀请好友背景图
     */
    private fun getShareBG() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.EXTEND_LIST)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                shareBGAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ShareBGListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listShare.clear()
                    }
                    listShare.addAll(model.lists)
                    shareBGAdapter.setOnItemClickListener { adapter, view, position ->
                        var b = Bundle()
                        b.putString("qrcode_src", model.qrcode_src)
                        b.putString("bg_src", listShare[position].image)
                        ActivityUtils.openActivity(mContext, ShareActivity::class.java, b)
                    }
                    shareBGAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                shareBGAdapter.setEmptyView(R.layout.include_fail)
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
     * 充值记录
     */
    private fun getRechargeHistory() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.RECHARGE_RECORD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                rechargeHistoryAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: RechargeHistoryListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listRechargeHistory.clear()
                    }
                    listRechargeHistory.addAll(model.lists)
                    rechargeHistoryAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                rechargeHistoryAdapter.setEmptyView(R.layout.include_fail)
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
        mode = intent.extras?.getInt("mode", MODE_SHARE_BG) ?: MODE_SHARE_BG
    }

    override fun clear() {

    }

}