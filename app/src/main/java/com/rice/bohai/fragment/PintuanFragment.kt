package com.rice.bohai.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.*
import com.rice.bohai.adapter.PintuanBannerAdapter
import com.rice.bohai.adapter.ProducePintuanAdapter
import com.rice.bohai.anim.ScalePageTransformer
import com.rice.bohai.dialog.CommonDialog
import com.rice.bohai.dialog.DialogHelper
import com.rice.bohai.model.PintuanOrderListModel
import com.rice.bohai.model.PintuanOrderModel
import com.rice.bohai.model.PintuanProcessModel
import com.rice.bohai.model.PintuanProduceDeModel
import com.rice.bohai.tools.ClickUtils
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.fragment_guamai.*
import kotlinx.android.synthetic.main.fragment_pintuan.*

class PintuanFragment : BaseImmersionFragment() {

    lateinit var producePintuanAdapter: ProducePintuanAdapter
    var listBanner = ArrayList<String>()
    var listProduce: MutableList<PintuanOrderModel> = ArrayList()
    var page = 1
    var isloaded = false
    var price = ""
    var isAutoEnable = true
    var enableBuy = true
    lateinit var loadingDialog: RLoadingDialog
    var authDialog: Dialog? = null

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_pintuan

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        textview_chaxun.setOnClickListener() {
            ActivityUtils.openActivity(mContext, BuyHistoryActivity::class.java)
        }
        textview_ptmx.setOnClickListener() {
            ActivityUtils.openActivity(mContext, PintuanMXActivity::class.java)
        }
        textBuyNow.setOnClickListener() {
            if (!enableBuy || !ClickUtils.enableClick()) {
                return@setOnClickListener
            }
            var msg = "请确认是否花费${price}购买怀匠·茅台大汉玉液"
            var dialog = CommonDialog(mContext, msg)
            dialog.onCallback = object : CommonDialog.OnCallback {
                override fun okClick() {
                    val price = MyApplication.instance.userInfo?.price
                    if (!TextUtils.isEmpty(price) && price!!.toDouble() > 0) {
                        if (enableBuy) {
                            enableBuy = false
                            buy()
                        }
                    } else {
                        ToastUtil.showShort("账户余额不足~")
                    }
                }

                override fun xieyiClick() {
                    var b = Bundle()
                    b.putInt("type", 1)
                    ActivityUtils.openActivity(
                        mContext,
                        XieyiActivity::class.java,
                        b
                    )
                }
            }
            dialog.show()
        }
        tv_auth_btn.setOnClickListener {
            ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
        }

        recyclerProducts.layoutManager = LinearLayoutManager(mContext)
        producePintuanAdapter = ProducePintuanAdapter(mContext, listProduce)
        producePintuanAdapter.bindToRecyclerView(recyclerProducts)
        producePintuanAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        producePintuanAdapter.onPintuanClickListener =
            object : ProducePintuanAdapter.OnPintuanClickListener {
                override fun onPintuanClick(orderId: String) {
                    var msg = "请确认该订单是否参与拼团活动"
                    var dialog = CommonDialog(mContext, msg)
                    dialog.onCallback = object : CommonDialog.OnCallback {
                        override fun okClick() {
                            addPintuan(orderId)
                        }

                        override fun xieyiClick() {
                            var b = Bundle()
                            b.putInt("type", 1)
                            ActivityUtils.openActivity(
                                mContext,
                                XieyiActivity::class.java,
                                b
                            )
                        }
                    }
                    dialog.show()
                }

            }
        recyclerProducts.adapter = producePintuanAdapter

        smr.setEnableRefresh(true)
        smr.setOnRefreshListener {
            page = 1
            initPintuanList()
        }
        smr.setOnLoadMoreListener {
            page++
            initPintuanList()
        }

        initProduce()
//        initPintuanList()
    }


    private fun initBanner(imageList: List<String>) {
        if (imageList != null && imageList.isNotEmpty()) {
            bannerViewPager.offscreenPageLimit = imageList.size
            bannerViewPager.pageMargin =
                0 - mContext.resources.getDimensionPixelOffset(R.dimen.dp_26) //处理两边碎片显示
            bannerViewPager.setPageTransformer(true, ScalePageTransformer(true))
            listBanner.clear()
            listBanner.addAll(imageList)
            bannerViewPager.adapter = PintuanBannerAdapter(mContext, listBanner)
            bannerViewPager.startAutoScroll()
        }
    }

    private fun initProduce() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_PRODUCT)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    var model: PintuanProduceDeModel = StringNullAdapter.gson.fromJson(result)
                    var pintuanProduceModel = model.model
                    price = pintuanProduceModel.price
                    textview_price.text = "￥$price"
                    textview_name.text = pintuanProduceModel.name
                    initBanner(pintuanProduceModel.imageList)
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


    fun initData() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        if (MyApplication.instance.userInfo?.is_valid == 0) {
            ll_auth.visibility = View.VISIBLE
            return
        }
        ll_auth.visibility = View.GONE
        if (!isloaded) {
            isloaded = true

            var isauto = false
            if (MyApplication.instance.userInfo !== null && MyApplication.instance.userInfo!!.p_open_auto_group == 1) {
                isauto = true
            }
            checkbox.isChecked = isauto
            checkbox.setOnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->
//                Log.i("hel->", "checkbox onchecked to ${b}")
                autoAddPintuan()
            }
        }
        initPintuanProcess()
        page = 1
        initPintuanList()
    }


    private fun initPintuanProcess() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_PRODUCE_PROCESS)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
//                Log.i("get-group-order-info->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: PintuanProcessModel = StringNullAdapter.gson.fromJson(result)
                    textview_count.text = model.total_winning
                    textview_process.text = "${model.percent}%"
                    progressbar.progress = model.percent
                    if (model.percent <= 30) {
                        progressbar.progressDrawable =
                            mContext.resources.getDrawable(R.drawable.process_orange)
                    } else if (model.percent <= 70) {
                        progressbar.progressDrawable =
                            mContext.resources.getDrawable(R.drawable.process_yellow)
                    } else {
                        progressbar.progressDrawable =
                            mContext.resources.getDrawable(R.drawable.process_green)
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

    private fun initPintuanList() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_ORDER_LIST)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onSuccess { byts ->
                producePintuanAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: PintuanOrderListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listProduce.clear()
                    }
                    listProduce.addAll(model.lists)
                    producePintuanAdapter.notifyDataSetChanged()
                }
            }
            onFinish {
                smr.finishLoadMore()
                smr.finishRefresh()
            }
            onFail { error ->
                producePintuanAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    private fun buy() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_BUY)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { byts ->
                MyApplication.instance.getUserInfoFromWeb()
                val result = RiceHttpK.getResult(mContext, byts)
                if (!TextUtils.isEmpty(result)){
                    ToastUtil.showLong("购买成功")
                    page = 1
                    initPintuanList()
                }
                enableBuy = true
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
                enableBuy = true
            }
            onStart {
                if (!loadingDialog.isShowing) {
                    loadingDialog.show()
                }
            }
            onFinish {
                loadingDialog.dismiss()
            }
        }
    }

    private fun autoAddPintuan() {
        if (!isAutoEnable) {
            isAutoEnable = true
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_AUTO_ADD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                if (result == "") {
                    isAutoEnable = false
                    checkbox.isChecked = !checkbox.isChecked
                }
                // 重启获取get-user接口，initPintuanList中Adapter会用到
                MyApplication.instance.onUserInfoUpdateCompleteListener =
                    object : MyApplication.OnUserInfoUpdateCompleteListener {
                        override fun onUserInfoUpdateComplete() {
                            page = 1
                            initPintuanList()
                        }
                    }
                MyApplication.instance.getUserInfoFromWeb()
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

    private fun addPintuan(order_id: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_ADD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "order_id" - order_id
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                Logger.i("add-group-buy->${result}")
                if (TextUtils.isNotEmpty(result)) {
                    page = 1
                    initPintuanList()
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