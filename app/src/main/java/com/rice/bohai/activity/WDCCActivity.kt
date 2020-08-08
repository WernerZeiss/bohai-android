package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.WDCCAdapter
import com.rice.bohai.dialog.SignDialog
import com.rice.bohai.model.CommodityDeModel
import com.rice.bohai.model.WDCCListModel
import com.rice.bohai.model.WDCCModel
import com.rice.dialog.OkCancelDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class WDCCActivity : RiceBaseActivity() {

    lateinit var wdccAdapter: WDCCAdapter
    var list: MutableList<WDCCModel> = ArrayList()
    var page = 1
    var mode = MODE_NORMOL
    lateinit var signDialog: SignDialog
    var id = ""
    var position_id = ""
    lateinit var okCancelDialog: OkCancelDialog

    companion object {
        const val MODE_NORMOL = 0
        const val MODE_SELECT = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_wdcc
    }

    override fun initView() {
        signDialog = SignDialog(mContext)
        okCancelDialog = OkCancelDialog(mContext)
        signDialog.xieyi = MyApplication.instance.systemModel?.sign_agreement
        recycler.layoutManager = LinearLayoutManager(mContext)
        wdccAdapter = WDCCAdapter(mContext, list, mode)
        if (mode == MODE_SELECT) {
            wdccAdapter.setOnItemClickListener { adapter, view, position ->
                var intent = Intent()
                var b = Bundle()
                b.putSerializable("wdcc", list[position])
                intent.putExtras(b)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
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
                        if (MyApplication.instance.userInfo?.is_signature == 2) {
                            ToastUtil.showShort("签名正在审核中，请稍后再试")
                            MyApplication.instance.getUserInfoFromWeb()
                            return@setOnItemChildClickListener
                        } else if (MyApplication.instance.userInfo?.is_signature == 0) {
                            if (!signDialog.isShowing) {
                                signDialog.xieyi = MyApplication.instance.systemModel?.sign_agreement
                                signDialog.onOkClickListener = object : SignDialog.OnOkClickListener {
                                    override fun onOkClick(filePath: String) {
                                        MyApplication.instance.onUploadCompleteListner =
                                                object : MyApplication.OnUploadCompleteListner {
                                                    override fun onUploadComplete(src: String) {
                                                        uploadSign(src, list[position].number, list[position].price) //提交签名
                                                    }
                                                }
                                        MyApplication.instance.uploadImage(filePath)
                                        MyApplication.instance.getUserInfoFromWeb()
                                    }
                                }
                                signDialog.show()
                                ToastUtil.showShort("首次转让请先签署协议")
                            }
                        } else {
                            sell(list[position].number, list[position].price)
                        }
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
                    var b = Bundle()
                    b.putSerializable("wdcc", list[position])
                    b.putInt("mode", XJ2JFActivity.MODE_DD2JF)
                    ActivityUtils.openActivity(mContext, XJ2JFActivity::class.java, b)
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
        initCommodity()
    }

    /**
     * 获取产品列表数据
     */
    private fun initCommodity() {
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
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: WDCCListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        list.clear()
                    }
                    list.addAll(model.lists)
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

    /**
     * 上传签名
     */
    private fun uploadSign(src: String, number: Int, price: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_SIGNATURE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "sign_image" - src
                "type" - "1" //转让签名
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    sell(number, price)
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
                val result = RiceHttpK.getResult(mContext,byts)
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
    private fun sell(number: Int, price: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.HANG_UP_POSITION)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "position_id" - position_id
                "number" - number.toString()
                //                "sign_image" - signUrl
                "price" - price
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


    override fun getIntentData() {
        mode = intent.extras?.getInt("mode", MODE_NORMOL) ?: MODE_NORMOL
    }

    override fun clear() {

    }

}