package com.rice.bohai.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.activity.WebViewInAppActivity
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.BindBankCardActivity
import com.rice.bohai.activity.ExtractActivity
import com.rice.bohai.activity.LoginActivity
import com.rice.bohai.adapter.WDCCAdapter
import com.rice.bohai.dialog.SignDialog
import com.rice.bohai.dialog.XieyiDialog
import com.rice.bohai.model.CommodityDeModel
import com.rice.bohai.model.SignModel
import com.rice.bohai.model.WDCCListModel
import com.rice.bohai.model.WDCCModel
import com.rice.dialog.OkCancelDialog
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.include_my_order.*
import kotlinx.android.synthetic.main.include_no_login.*
import kotlinx.android.synthetic.main.include_no_login.frameNoLogin
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.nio.charset.Charset

/**
 * 交易→订单
 */
class OrderFragment : BaseImmersionFragment() {

    lateinit var wdccAdapter: WDCCAdapter
    var list: MutableList<WDCCModel> = ArrayList()
    var onGuamaiClickListener: OnGuamaiClickListener? = null
    var page = 1
    lateinit var signDialog: SignDialog
    var id = ""
    var position_id = ""
    lateinit var okCancelDialog: OkCancelDialog
    var isResume = false
    lateinit var dd2jfDialog: OkCancelDialog
    lateinit var sellDialog: OkCancelDialog
    lateinit var xieyiDialog: XieyiDialog
    lateinit var loadingDialog: RLoadingDialog

    interface OnGuamaiClickListener {
        fun onGuamaiClickListener(id: String, position_id: String)
    }

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = false
        isWhiteNavigationBarIcon = false
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    override val contentViewLayoutID: Int
        get() = R.layout.include_my_order

    @SuppressLint("SetTextI18n")
    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        recycler.layoutManager = LinearLayoutManager(mContext)
        okCancelDialog = OkCancelDialog(mContext)
        sellDialog = OkCancelDialog(mContext)
        okCancelDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
            override fun onOkClick() {
                cancel()
            }
        }
        dd2jfDialog = OkCancelDialog(mContext)
        signDialog = SignDialog(mContext)
        signDialog.xieyi = MyApplication.instance.systemModel?.buy_sign_agreement
        wdccAdapter = WDCCAdapter(mContext, list)
        wdccAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.textBtnSell -> {
                    //转让
                    //                    onGuamaiClickListener?.onGuamaiClickListener(
                    //                        list[position].product_id.toString(),
                    //                        list[position].id.toString()
                    //                    )
                    id = list[position].product_id.toString()
                    position_id = list[position].id.toString()
                    if (list[position].status == 2) {
                        //转让中
                        okCancelDialog.setInfo("是否确定取消转让${list[position].product_name}？")
                        if (!okCancelDialog.isShowing) {
                            okCancelDialog.show()
                        }
                    } else {
                        if (MyApplication.instance.userInfo?.is_valid == 0) {
                            //先实名认证
                            ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
                            ToastUtil.showShort("请先实名认证")
                            return@setOnItemChildClickListener
                        }
                        if (MyApplication.instance.userInfo?.is_sign_task == 0) {
                            //签署数字签名
                            xieyiDialog = XieyiDialog(mContext)
                            xieyiDialog.onOkClickListener = object : XieyiDialog.OnOkClickListener {
                                override fun onOkClick() {
                                    registerSignTask()
                                }
                            }
                            xieyiDialog.show()
                            ToastUtil.showShort("首次转让请先签署协议")
                            return@setOnItemChildClickListener
                        }
                        //                        if (MyApplication.instance.userInfo?.is_signature == 2) {
                        //                            ToastUtil.showShort("签名正在审核中，请稍后再试")
                        //                            MyApplication.instance.getUserInfoFromWeb()
                        //                            return@setOnItemChildClickListener
                        //                        } else if (MyApplication.instance.userInfo?.is_signature == 0) {
                        //                            //签名
                        //                            if (!signDialog.isShowing) {
                        //                                signDialog.onOkClickListener = object : SignDialog.OnOkClickListener {
                        //                                    override fun onOkClick(filePath: String) {
                        //                                        MyApplication.instance.onUploadCompleteListner =
                        //                                                object : MyApplication.OnUploadCompleteListner {
                        //                                                    override fun onUploadComplete(src: String) {
                        //                                                        uploadSign(src, list[position].number, list[position].price) //提交签名
                        //                                                    }
                        //                                                }
                        //                                        MyApplication.instance.uploadImage(filePath)
                        //                                        MyApplication.instance.getUserInfoFromWeb()
                        //                                    }
                        //                                }
                        //                                signDialog.show()
                        //                                ToastUtil.showShort("首次转让请先签署协议")
                        //                            }

                        //                        } else {
                        sellDialog.setInfo("确认要转让${list[position].product_name}吗？")
                        sellDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                            override fun onOkClick() {
                                getSignStatus()
                            }
                        }
                        sellDialog.show()
                        //                            sell(list[position].number, list[position].price)
                        //                        }
                    }
                }
                R.id.textBtnExtract -> {
                    //提货
                    var b = Bundle()
                    b.putString("id", list[position].id.toString())
                    b.putInt("number", list[position].total_number)
                    ActivityUtils.openActivity(mContext, ExtractActivity::class.java, b)
                }
                R.id.textBtnExchange -> {
                    //兑换
                    dd2jfDialog.setInfo("确认要将${list[position].product_name}兑换为${list[position].exchange_integral_num}积分吗？")
                    dd2jfDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                        override fun onOkClick() {
                            order2integral(list[position].id.toString())
                        }
                    }
                    if (!dd2jfDialog.isShowing) {
                        dd2jfDialog.show()
                    }
                    //                    var b = Bundle()
                    //                    b.putSerializable("wdcc", list[position])
                    //                    b.putInt("mode", XJ2JFActivity.MODE_DD2JF)
                    //                    ActivityUtils.openActivity(mContext, XJ2JFActivity::class.java, b)
                }
            }
        }
        wdccAdapter.bindToRecyclerView(recycler)
        wdccAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = wdccAdapter
        refresh.setOnRefreshListener {
            page = 1
            initCommodity()
        }
        refresh.setOnLoadMoreListener {
            page++
            initCommodity()
        }
        textLogin.setOnClickListener {
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        }
        initCommodity()
    }

    /**
     * 同意数字证书协议
     */
    private fun registerSignTask() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.SIGN_REGISTER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                //                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    //                    if (!signDialog.isShowing) {
                    //                        signDialog.show()
                    //                    }
                    MyApplication.instance.getUserInfoFromWeb()
                    getSignStatus()
                } else {
                    loadingDialog.dismiss()
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

    /**
     * 获取签名状态
     */
    @SuppressLint("SetTextI18n")
    fun getSignStatus() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        loadingDialog.show()
        Http.post {
            url = RiceHttpK.getUrl(Constant.CHECK_CONTRACT)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "integral_product_id" - id
                "type" - "1" //挂卖
            }
            tag = "getSignStatus"
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: SignModel = StringNullAdapter.gson.fromJson(result)
                    checkIsSign(model.is_sign, model.sign_url)
                    MyApplication.instance.getUserInfoFromWeb()
                } else {
                    loadingDialog.dismiss()
                }
            }
            onFail { error ->
                loadingDialog.dismiss()
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    private fun checkIsSign(is_sign: Int, signUrl: String) {
        MyApplication.instance.getUserInfoFromWeb()
        when (is_sign) {
            0 -> {
                //注册
                uploadSign()
            }
            1 -> {
                //签署
                loadingDialog.dismiss()
                var b = Bundle()
                b.putString("url", signUrl)
                ActivityUtils.openActivity(mContext, WebViewInAppActivity::class.java, b)
                //从其他浏览器打开
                //                loadingDialog.dismiss()
                //                val intent = Intent()
                //                intent.action = Intent.ACTION_VIEW
                //                val content_url = Uri.parse(signUrl)
                //                intent.data = content_url
                //                startActivity(Intent.createChooser(intent, "请选择浏览器"))
            }
            2 -> {
                //直接转让
                loadingDialog.dismiss()
                sell()
                //                if (!buyDialog.isShowing) {
                //                    buyDialog.show()
                //                }
            }
        }
    }

    /**
     * 订单转积分
     */
    private fun order2integral(id: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.ORDER_CONVERSION_INTEGRAL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "position_id" - id
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initCommodity()
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

    /**
     * 上传签名
     */
    private fun uploadSign() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_SIGNATURE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                //                "sign_image" - src
                "type" - "1" //挂卖
            }
            onSuccess { bytes ->
                val result = RiceHttpK.getResult(mContext, bytes)
                if (TextUtils.isNotEmpty(result)) {
                    val model: SignModel = StringNullAdapter.gson.fromJson(result)
                    checkIsSign(1, model.sign_url)
                    MyApplication.instance.getUserInfoFromWeb()
                } else {
                    loadingDialog.dismiss()
                }
            }
            onFail { error ->
                loadingDialog.dismiss()
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
     * 获取产品详情
     */
    @SuppressLint("SetTextI18n")
    fun initDetail() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRODUCT_DETAIL)
            params {
                if (TextUtils.isNotEmpty(id)) {
                    "id" - id
                }
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: CommodityDeModel = StringNullAdapter.gson.fromJson(result)
                    //                    textCode.text = model.model.no_number
                    id = model.model.id.toString()
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

    /**
     * 转让
     */
    private fun sell() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.HANG_UP_POSITION)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "position_id" - position_id
                //                "number" - number.toString()
                //                "sign_image" - signUrl
                //                "price" - price
            }
            onFinish {
                loadingDialog.dismiss()
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initCommodity()
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

    /**
     * 取消转让
     */
    private fun cancel() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.CANCEL_SALE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "position_id" - position_id
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initCommodity()
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

    /**
     * 获取产品列表数据
     */
    @SuppressLint("SetTextI18n")
    fun initCommodity() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            mActivity.runOnUiThread { frameNoLogin.visibility = View.VISIBLE }
            return
        }
        mActivity.runOnUiThread { frameNoLogin.visibility = View.INVISIBLE }
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_POSITION)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                wdccAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: WDCCListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        list.clear()
                    }
                    list.addAll(model.lists)
                    textBasic.text = "提货值：" + model.now_basic_income
                    wdccAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                wdccAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isResume = true
        loadingDialog.dismiss()
        MyApplication.instance.getUserInfoFromWeb()
    }

    override fun onDestroy() {
        super.onDestroy()
        isResume = false
    }

}