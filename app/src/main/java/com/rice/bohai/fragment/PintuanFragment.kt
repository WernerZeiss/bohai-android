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
import com.rice.bohai.listener.OnDoubleSelectListener
import com.rice.bohai.listener.OnSelectPayListener
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
    private var successCount = 0

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
            showPaySelector(MyApplication.instance.userInfo?.is_group_buy_ticket == 1)
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
                    if (MyApplication.instance.userInfo?.is_valid == 0) {
                        idCardAuthDialog()
                    } else {
                        joinGroupDialog(orderId)
                    }
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

    /**
     * 身份认证提醒
     */
    private fun idCardAuthDialog() {
        val dialog = DialogHelper.getDoubleDialog(context!!,
            "温馨提示", "您未实名认证，暂时不能参与拼团",
            "取消",
            "实名认证",
            object : OnDoubleSelectListener {
                override fun onLeft(dialog: Dialog) {
                    dialog.dismiss()
                }

                override fun onRight(dialog: Dialog) {
                    dialog.dismiss()
                    ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
                }
            })
        dialog.show()
    }

    /**
     * 参与拼团提示
     */
    private fun joinGroupDialog(orderId: String) {
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


    /**
     * 选择支付方式
     * @param hasCoupon 是否提供购货券支付
     */
    private fun showPaySelector(hasCoupon: Boolean) {
        val payDialog =
            DialogHelper.getPaySelectorDialog(context!!, hasCoupon, object : OnSelectPayListener {
                override fun onSelected(type: Int) {
                    if (type == 0) {
                        //余额支付
                        showBalanceBuyTipDialog()
                    } else {
                        //购货券支付
                        showCouponBuyTipDialog()
                    }
                }
            })
        payDialog.show()
    }

    /**
     * 余额购买提醒
     */
    private fun showBalanceBuyTipDialog() {
        val msg = "请确认是否花费¥${price}购买怀匠·茅台大汉玉液"
        val dialog = CommonDialog(mContext, msg)
        dialog.onCallback = object : CommonDialog.OnCallback {
            override fun okClick() {
                val userMoney = MyApplication.instance.userInfo?.price
                val productPrice = if (TextUtils.isNotEmpty(price)) price!!.toDouble() else 0.toDouble()
                if (!TextUtils.isEmpty(userMoney) && userMoney!!.toDouble() >= productPrice) {
                    if (enableBuy) {
                        enableBuy = false
                        buy(1)
                    }
                } else {
                    showTipMsg("您当前余额不足，您可以直接在渤海贸易上购买直接参团或重新选择支付方式！")
                }
            }

            override fun xieyiClick() {
                val b = Bundle()
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

    /**
     * 购货券购买提醒
     */
    private fun showCouponBuyTipDialog() {
        val msg = "请确认是否花费${price}购货券购买怀匠·茅台大汉玉液"
        val dialog = CommonDialog(mContext, msg)
        dialog.onCallback = object : CommonDialog.OnCallback {
            override fun okClick() {
                val couponMoney = MyApplication.instance.userInfo?.group_wallet_money
                val productPrice = if (TextUtils.isNotEmpty(price)) price!!.toDouble() else 0.toDouble()
                if (!TextUtils.isEmpty(couponMoney) && couponMoney!!.toDouble() >= productPrice) {
                    if (enableBuy) {
                        enableBuy = false
                        buy(2)
                    }
                } else {
                    showTipMsg("您当前购货券余额不足，您可以直接在渤海贸易上购买直接参团或重新选择支付方式！")
                }
            }

            override fun xieyiClick() {
                dialog.dismiss()
                val b = Bundle()
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

    private fun showTipMsg(content: String) {
        val tipDialog = DialogHelper.getSingleDialog(
            context,
            "温馨提示", content, null
        )
        tipDialog?.show()
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
                    if (!TextUtils.isEmpty(model.total_winning)) {
                        successCount = model.total_winning.toInt()
                    }
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

    /**
     * 余额购买
     * @param type 1现金 2购货券
     */
    private fun buy(type: Int) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PINTUAN_BUY)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "buy_type" - type.toString()
            }
            onSuccess { byts ->
                MyApplication.instance.getUserInfoFromWeb()
                val result = RiceHttpK.getResult(mContext, byts)
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showLong("购买成功")
//                    showTipMsg("尊敬的用户，恭喜您拼团成功，请耐心等待结果")
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
//                Logger.i("add-group-buy->${result}")
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