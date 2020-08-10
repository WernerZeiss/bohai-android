package com.rice.bohai.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.rice.bohai.activity.*
import com.rice.bohai.adapter.XSPHAdapter
import com.rice.bohai.dialog.SignDialog
import com.rice.bohai.dialog.XieyiDialog
import com.rice.bohai.model.ProfitHistoryListModel
import com.rice.bohai.model.ProfitHistoryModel
import com.rice.bohai.model.SignModel
import com.rice.dialog.OkCancelDialog
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.fragment_xsph.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.nio.charset.Charset

class XSPHFragment : BaseImmersionFragment() {

    lateinit var xsphAdapter: XSPHAdapter
    var list: MutableList<ProfitHistoryModel> = ArrayList()
    var page = 1
    lateinit var signDialog: SignDialog
    var id = ""
    var position_id = ""
    lateinit var okCancelDialog: OkCancelDialog
    lateinit var sellDialog: OkCancelDialog
    lateinit var dd2jfDialog: OkCancelDialog
    lateinit var xieyiDialog: XieyiDialog
    lateinit var loadingDialog: RLoadingDialog

    init {
        isContentInvade = true
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = true
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_xsph

    override fun initView() {
        signDialog = SignDialog(mContext)
        loadingDialog = RLoadingDialog(mContext, true)
        okCancelDialog = OkCancelDialog(mContext)
        sellDialog = OkCancelDialog(mContext)
        dd2jfDialog = OkCancelDialog(mContext)
        signDialog.xieyi = MyApplication.instance.systemModel?.sign_agreement
        recycler.layoutManager = LinearLayoutManager(mContext)
        xsphAdapter = XSPHAdapter(mContext, list)
        xsphAdapter.bindToRecyclerView(recycler)
        xsphAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        xsphAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.textBtnSell -> {
                    //转让
                    //                    onGuamaiClickListener?.onGuamaiClickListener(
                    //                        list[position].product_id.toString(),
                    //                        list[position].id.toString()
                    //                    )
                    if (MyApplication.instance.userInfo?.is_valid == 0) {
                        //先实名认证
                        ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
                        ToastUtil.showShort("请先实名认证")
                        return@setOnItemChildClickListener
                    }
//                    if (MyApplication.instance.userInfo?.is_sign_task == 0) {
//                        //签署数字签名
//                        xieyiDialog = XieyiDialog(mContext)
//                        xieyiDialog.onOkClickListener = object : XieyiDialog.OnOkClickListener {
//                            override fun onOkClick() {
//                                registerSignTask(list[position].id.toString())
//                            }
//                        }
//                        xieyiDialog.show()
//                        ToastUtil.showShort("首次转让请先签署协议")
//                        return@setOnItemChildClickListener
//                    }

                    //                    if (MyApplication.instance.userInfo?.is_signature == 2) {
                    //                        ToastUtil.showShort("签名正在审核中，请稍后再试")
                    //                        MyApplication.instance.getUserInfoFromWeb()
                    //                        return@setOnItemChildClickListener
                    //                    } else if (MyApplication.instance.userInfo?.is_signature == 0) {
                    //                        //先签名
                    //                        if (!signDialog.isShowing) {
                    //                            signDialog.onOkClickListener = object : SignDialog.OnOkClickListener {
                    //                                override fun onOkClick(filePath: String) {
                    //                                    MyApplication.instance.onUploadCompleteListner =
                    //                                            object : MyApplication.OnUploadCompleteListner {
                    //                                                override fun onUploadComplete(src: String) {
                    //                                                    uploadSign(src, list[position].id.toString(), list[position].price) //提交签名
                    //                                                }
                    //                                            }
                    //                                    MyApplication.instance.uploadImage(filePath)
                    //                                    MyApplication.instance.getUserInfoFromWeb()
                    //                                }
                    //                            }
                    //                            signDialog.show()
                    //                            ToastUtil.showShort("首次转让请先签署协议")
                    //                        }
                    //                    } else {
                    //                        sell(list[position].id.toString())
                    //                    }
                    sellDialog.setInfo("确认要转让${list[position].product_name}吗？")
                    sellDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                        override fun onOkClick() {
//                            getSignStatus(list[position].id.toString())
                            sell(list[position].id.toString())
                        }
                    }
                    sellDialog.show()
                }
                R.id.textBtnExtract -> {
                    //提货
                    var number = list[position].market_value.toDouble().toInt()
                    if (number >= 1) {
                        var b = Bundle()
                        b.putString("id", list[position].id.toString())
                        b.putInt("number", number)
                        ActivityUtils.openActivity(mContext, ExtractActivity::class.java, b, 123)
                    } else {
                        ToastUtil.showShort("该产品配货数量不足以提货")
                    }
                }
                R.id.textBtnExchange -> {
                    //兑换
                    //                    var b = Bundle()
                    //                    b.putSerializable("xsph", list[position])
                    //                    b.putInt("mode", XJ2JFActivity.MODE_XSPH2JF)
                    //                    var intent = Intent(mContext, XJ2JFActivity::class.java)
                    //                    intent.putExtras(b)
                    //                    startActivityForResult(intent, Constant.REQUEST_XSPH2JF)
                    dd2jfDialog.setInfo("确认要将${list[position].product_name}兑换为${list[position].exchange_integral_num}积分吗？")
                    dd2jfDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                        override fun onOkClick() {
                            order2integral(list[position].id.toString())
                        }
                    }
                    if (!dd2jfDialog.isShowing) {
                        dd2jfDialog.show()
                    }
                }
                R.id.textBtnCancel -> {
                    //取消
                    id = list[position].product_id.toString()
                    position_id = list[position].id.toString()
                    if (list[position].status == 2 || list[position].status == 7) {
                        //转让中
                        okCancelDialog.setInfo("确认要取消转让${list[position].product_name}吗？")
                        okCancelDialog.onOkClickListener =
                            object : OkCancelDialog.OnOkClickListener {
                                override fun onOkClick() {
                                    cancel(list[position].id.toString())
                                }
                            }
                        if (!okCancelDialog.isShowing) {
                            okCancelDialog.show()
                        }
                    }
                }
            }
        }
        recycler.adapter = xsphAdapter
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        textDe.setOnClickListener {
//            var b = Bundle()
//            b.putInt("mode", DataActivity.MODE_XSPHDE)
            ActivityUtils.openActivity(mContext, PHQListActivity::class.java)
        }
        initData()
    }

    override fun onResume() {
        super.onResume()
        loadingDialog.dismiss()
    }

    /**
     * 获取签名状态
     */
    @SuppressLint("SetTextI18n")
    fun getSignStatus(id: String) {
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
                    checkIsSign(model.is_sign, model.sign_url, id)
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

    private fun checkIsSign(is_sign: Int, signUrl: String, id: String = "") {
        MyApplication.instance.getUserInfoFromWeb()
        when (is_sign) {
            0 -> {
                //注册
                uploadSign(id)
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
                sell(id)
            }
        }
    }

    /**
     * 同意数字证书协议
     */
    private fun registerSignTask(id: String) {
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
                    getSignStatus(id)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        page = 1
        initData()
    }

    /**
     * 订单转积分
     */
    private fun order2integral(id: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PROFIT_POSITION_CONVERSION_INTEGRAL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "profit_position_id" - id
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initData()
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

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //        super.onActivityResult(requestCode, resultCode, data)
    //        when (requestCode) {
    //            Constant.REQUEST_XSPH2JF -> {
    //                //销售配货转积分返回
    //                if (resultCode == RESULT_OK) {
    //                    page = 1
    //                    initData()
    //                }
    //            }
    //        }
    //    }

    fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_PROFIT_RECORD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                if (isResumed) {
                    refresh.finishLoadMore()
                    refresh.finishRefresh()
                }
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                xsphAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ProfitHistoryListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        list.clear()
                    }
                    list.addAll(model.lists)
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

    /**
     * 取消转让/拼团
     */
    private fun cancel(id: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.CANCEL_PROFIT_SALE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "profit_position_id" - id
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initData()
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
    private fun uploadSign(id: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_SIGNATURE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                //                "sign_image" - src
                "type" - "1" //转让签名
            }
            onSuccess { bytes ->
                val result = RiceHttpK.getResult(mContext, bytes)
                if (TextUtils.isNotEmpty(result)) {
                    val model: SignModel = StringNullAdapter.gson.fromJson(result)
                    checkIsSign(1, model.sign_url, id)
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
     * 转让
     */
    private fun sell(positionId: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_PROFIT_POSITION_SALE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "profit_position_id" - positionId
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initData()
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