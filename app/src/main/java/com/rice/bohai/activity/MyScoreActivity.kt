package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.CommodityAdapter
import com.rice.bohai.adapter.ExchangeHistoryAdapter
import com.rice.bohai.adapter.IntegralHistoryAdapter
import com.rice.bohai.model.*
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_myjf.*
import kotlinx.android.synthetic.main.activity_myjf.frameNoLogin
import kotlinx.android.synthetic.main.include_no_login.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*

@SuppressLint("Registered")
class MyScoreActivity : RiceBaseActivity() {

    var tag = TAG_JFSC
    lateinit var exchangeHistoryAdapter: ExchangeHistoryAdapter
    lateinit var scoreHistoryAdapter: IntegralHistoryAdapter
    lateinit var commodityAdapter: CommodityAdapter
    var listExchangeHistory: MutableList<ExchangeHistoryModel> = ArrayList()
    var listScoreHistory: MutableList<IntegralHistoryModel> = ArrayList()
    var listCommodity: MutableList<CommodityModel> = ArrayList()
    var page = 1
    var word = ""

    companion object {
        const val TAG_JFSC = 0
        const val TAG_DHJL = 1
        const val TAG_JFMX = 2
        const val TAG_HZJF = 3
    }

    init {
        isContentInvade = true
        navigationBarColorId = R.color.white
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = true
        viewTopId = R.id.viewTop
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_myjf
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance.onUserInfoUpdateCompleteListener =
            object : MyApplication.OnUserInfoUpdateCompleteListener {
                override fun onUserInfoUpdateComplete() {
                    initPriceData()
                }
            }
        MyApplication.instance.getUserInfoFromWeb()
    }

    /**
     * 加载账户数据
     */
    private fun initPriceData() {
        textScore.text = MyApplication.instance.userInfo?.integral_num ?: "0"
    }

    override fun initView() {
        toolbar.imgOk.setImageDrawable(resources.getDrawable(R.drawable.shopcar))
        toolbar.setOnOkClickListener() {
            ActivityUtils.openActivity(this, ShopcarActivity::class.java)
        }

        recycler.layoutManager = LinearLayoutManager(mContext)
        exchangeHistoryAdapter = ExchangeHistoryAdapter(mContext, listExchangeHistory)
        exchangeHistoryAdapter.setOnItemClickListener { adapter, view, position ->
            var b = Bundle()
            b.putString("order_number", listExchangeHistory[position].order_number)
            ActivityUtils.openActivity(mContext, OrderInfoActivity::class.java, b)
        }
        exchangeHistoryAdapter.bindToRecyclerView(recycler)
        exchangeHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        scoreHistoryAdapter = IntegralHistoryAdapter(mContext, listScoreHistory)
        scoreHistoryAdapter.bindToRecyclerView(recycler)
        scoreHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        commodityAdapter = CommodityAdapter(mContext, listCommodity)
        commodityAdapter.bindToRecyclerView(recycler)
        commodityAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        commodityAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.img -> {
                    //                    var b = Bundle()
                    //                    b.putSerializable("model", listCommodity[position])
                    //                    ActivityUtils.openActivity(mContext, ConfirmExchangeActivity::class.java, b)
                    var b = Bundle()
                    b.putString("id", listCommodity[position].id.toString())
                    ActivityUtils.openActivity(mContext, CommodityDeActivity::class.java, b)
                }
                R.id.textBtnExchange -> {
                    var b = Bundle()
                    b.putSerializable("model", listCommodity[position])
                    ActivityUtils.openActivity(mContext, ConfirmExchangeActivity::class.java, b)
                }
            }
        }
        llJFSC.setOnClickListener {
            tag = TAG_JFSC
            initTags()
        }
        llDHJL.setOnClickListener {
            tag = TAG_DHJL
            initTags()
        }
        llJFMX.setOnClickListener {
            tag = TAG_JFMX
            initTags()
        }
        initTags()
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        llHZJF.setOnClickListener {
            //划转积分
            //            var b = Bundle()
            //            b.putInt("mode", MulModeSettingActivity.MODE_ZJHZ)
            //            ActivityUtils.openActivity(mContext, MulModeSettingActivity::class.java, b)
            var b = Bundle()
            b.putInt("mode", XJ2JFActivity.MODE_XJ2JF)
            ActivityUtils.openActivity(mContext, XJ2JFActivity::class.java, b)
        }
//        editSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {   // 按下完成按钮，这里和上面imeOptions对应
//                word = editSearch.text.toString()
//                initCommodity()
//                return@setOnEditorActionListener false
//            }
//            return@setOnEditorActionListener true //返回true，保留软键盘。false，隐藏软键盘
//        }
        textSearch.setOnClickListener {
            word = editSearch.text.toString()
            initCommodity()
        }
        textLogin.setOnClickListener {
            ActivityUtils.openActivity(
                mContext,
                LoginActivity::class.java
            )
        }
        initData()
    }

    fun initData() {
        initCommodity()
        exchangeHistory()
        initHistory("2")
    }

    private fun initTags() {
        imgJFSC.setImageResource(R.drawable.btn_market_jf)
        imgDHJL.setImageResource(R.drawable.btn_history_jf)
        imgJFMX.setImageResource(R.drawable.btn_jf)
        textJFSC.setTextColor(mContext.resources.getColor(R.color.black))
        textDHJL.setTextColor(mContext.resources.getColor(R.color.black))
        textJFMX.setTextColor(mContext.resources.getColor(R.color.black))
        when (tag) {
            TAG_JFSC -> {
                editSearch.visibility = View.VISIBLE
                imgJFSC.setImageResource(R.drawable.btn_market_jf_focus)
                textJFSC.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
                recycler.layoutManager = GridLayoutManager(mContext, 2)
                recycler.adapter = commodityAdapter
                runOnUiThread { frameNoLogin.visibility = View.GONE }
            }
            TAG_DHJL -> {
                editSearch.visibility = View.GONE
                imgDHJL.setImageResource(R.drawable.btn_history_jf_focus)
                textDHJL.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
                recycler.layoutManager = LinearLayoutManager(mContext)
                recycler.adapter = exchangeHistoryAdapter
                if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                    runOnUiThread { frameNoLogin.visibility = View.VISIBLE }
                }
            }
            TAG_JFMX -> {
                editSearch.visibility = View.GONE
                imgJFMX.setImageResource(R.drawable.btn_jf_focus)
                textJFMX.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
                recycler.layoutManager = LinearLayoutManager(mContext)
                recycler.adapter = scoreHistoryAdapter
                if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                    runOnUiThread { frameNoLogin.visibility = View.VISIBLE }
                }
            }
        }
    }

    /**
     * 获取积分商城数据
     */
    private fun initCommodity() {
        if (TextUtils.isNotEmpty(editMin.text.toString())) {
            if (TextUtils.isInt(editMin.text.toString()) && editMin.text.toString().toInt() == 0) {
                ToastUtil.showShort("最小积分不能为0")
                return
            }
        }
        if (TextUtils.isNotEmpty(editMax.text.toString())) {
            if (TextUtils.isInt(editMax.text.toString()) && editMax.text.toString().toInt() == 0) {
                ToastUtil.showShort("最大积分不能为0")
                return
            }
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.INTEGRAL_PRODUCT_LIST)
            params {
                "page" - page.toString()
                "word" - word
                if (TextUtils.isNotEmpty(editMin.text.toString()) && TextUtils.isInt(editMin.text.toString()) && editMin.text.toString()
                        .toInt() > 0
                    && TextUtils.isNotEmpty(editMax.text.toString()) && TextUtils.isInt(editMax.text.toString()) && editMax.text.toString()
                        .toInt() > 0
                ) {
                    "small_integral" - editMin.text.toString()
                    "big_integral" - editMax.text.toString()
                }
            }
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                commodityAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: IntegralListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listCommodity.clear()
                    }
                    listCommodity.addAll(model.lists)
                    commodityAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                commodityAdapter.setEmptyView(R.layout.include_fail)
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
     * 获取积分记录
     */
    private fun initHistory(type: String) {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            return
        }
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
                exchangeHistoryAdapter.setEmptyView(R.layout.include_no_data)
                scoreHistoryAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: IntegralHistoryListModel = StringNullAdapter.gson.fromJson(result)
                    when (type) {
                        "2" -> {
                            //积分记录
                            if (page == 1) {
                                listScoreHistory.clear()
                            }
                            listScoreHistory.addAll(model.lists)
                            scoreHistoryAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            onFail { error ->
                exchangeHistoryAdapter.setEmptyView(R.layout.include_fail)
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

    /**
     * 兑换记录
     */
    private fun exchangeHistory() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.EXCHANGE_INTEGRAL_PRODUCT_LIST)
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
                exchangeHistoryAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ExchangeHistoryListModel = StringNullAdapter.gson.fromJson(result)
                    //兑换记录
                    if (page == 1) {
                        listExchangeHistory.clear()
                    }
                    listExchangeHistory.addAll(model.lists)
                    exchangeHistoryAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                exchangeHistoryAdapter.setEmptyView(R.layout.include_fail)
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
        tag = intent.extras?.getInt("tag", TAG_JFSC) ?: TAG_JFSC
    }

    override fun clear() {

    }

}