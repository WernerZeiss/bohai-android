package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.app.Activity
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
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.ShopcarAdapter
import com.rice.bohai.dialog.SignDialog
import com.rice.bohai.dialog.XieyiDialog
import com.rice.bohai.model.*
import com.rice.dialog.InputDialog
import com.rice.dialog.RLoadingDialog
import com.rice.imageloader.GlideLoadUtils
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.Arith
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_confirm_exchange.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class ConfirmExchangeActivity : RiceBaseActivity() {

    var num = 1 //单独兑换数量
    var model: CommodityModel? = null //单独兑换
    var address_id = "" //地址ID
    var ids = "" //批量兑换ID列表
    lateinit var inputDialog: InputDialog
    lateinit var signDialog: SignDialog
    var mode = MODE_SINGLE
    var list: MutableList<ShopcarModel> = ArrayList()
    lateinit var shopcarAdapter: ShopcarAdapter
    lateinit var xieyiDialog: XieyiDialog
    lateinit var loadingDialog: RLoadingDialog

    companion object {
        const val MODE_SINGLE = 0 //单个商品
        const val MODE_MUTI = 1 //多个商品
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_confirm_exchange
    }

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        signDialog = SignDialog(mContext)
        if (TextUtils.isNotEmpty(model?.sign_agreement)) {
            signDialog.xieyi = model?.sign_agreement
        } else {
            signDialog.xieyi = MyApplication.instance.systemModel?.sign_agreement
        }
        signDialog.onOkClickListener = object : SignDialog.OnOkClickListener {
            override fun onOkClick(filePath: String) {
                MyApplication.instance.onUploadCompleteListner =
                    object : MyApplication.OnUploadCompleteListner {
                        override fun onUploadComplete(src: String) {
                            exchange(src)
                        }
                    }
                MyApplication.instance.uploadImage(filePath)
            }
        }
        inputDialog = InputDialog(mContext, "请输入兑换数量")
        inputDialog.setTitle("输入兑换数量")
        inputDialog.onOkClickListener = object : InputDialog.OnOkClickListener {
            override fun onOkClick(str: String) {
                if (TextUtils.isPositiveInt(str)) {
                    num = str.toInt()
                    textNumber.text = num.toString()
                } else {
                    ToastUtil.showShort("输入无效，请重新输入")
                }
            }
        }
        textNumber.setOnClickListener {
            if (!inputDialog.isShowing) {
                inputDialog.show()
            }
        }
        textBtnSub.setOnClickListener {
            if (num > 1) {
                num--
                textNumber.text = num.toString()
            }
        }
        textBtnAdd.setOnClickListener {
            num++
            textNumber.text = num.toString()
        }
        constraintAddress.setOnClickListener {
            //选择地址
            var b = Bundle()
            b.putInt("mode", MyAddressActivity.MODE_SELECT)
            ActivityUtils.openActivity(
                mContext,
                MyAddressActivity::class.java,
                b,
                Constant.REQUEST_SELECT_ADDRESS
            )
        }
        textBtnSubmit.setOnClickListener {
            if (MyApplication.instance.userInfo?.is_valid == 0) {
                //先实名认证
                ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
                ToastUtil.showShort("请先实名认证")
                return@setOnClickListener
            }
//            if (MyApplication.instance.userInfo?.is_sign_task == 0) {
//                //签署数字签名
//                xieyiDialog = XieyiDialog(mContext)
//                xieyiDialog.onOkClickListener = object : XieyiDialog.OnOkClickListener {
//                    override fun onOkClick() {
//                        registerSignTask()
//                    }
//                }
//                xieyiDialog.show()
//                ToastUtil.showShort("首次兑换请先签署协议")
//                return@setOnClickListener
//            }
            when (mode) {
                MODE_SINGLE -> {
                    if (model?.is_signature ?: 1 == 1) {
                        getSignStatus()
                    } else {
                        exchange("")
                    }
                }
                MODE_MUTI -> {
                    submit()
                }
            }

        }
        if (model?.is_address == "1" || list.size > 0) {
            constraintAddress.visibility = View.VISIBLE
        } else {
            constraintAddress.visibility = View.GONE
        }
        getDefaultAddress()
    }

    private fun checkSign() {
        when (mode) {
            MODE_SINGLE -> {
                //                if (model?.is_signature == "1") {
                //                    if (!signDialog.isShowing) {
                //                        signDialog.show()
                //                    }
                //                } else {
                exchange("")
                //                }
            }
            MODE_MUTI -> {
                submit()
            }
        }
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
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    MyApplication.instance.getUserInfoFromWeb()
                    checkSign()
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

    override fun onResume() {
        super.onResume()
        loadingDialog.dismiss()
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
                //直接兑换
                loadingDialog.dismiss()
                checkSign()
                //                if (!buyDialog.isShowing) {
                //                    buyDialog.show()
                //                }
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
                "integral_product_id" - model?.id.toString()
                "type" - "3" //兑换商品
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
                "integral_product_id" - model?.id.toString()
                "type" - "3" //兑换商品
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

    /**
     * 获取默认地址
     */
    @SuppressLint("SetTextI18n")
    private fun getDefaultAddress() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_DEFAULT_ADDRESS)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    Logger.d(result)
                    var address: DefaultAddressModel = StringNullAdapter.gson.fromJson(result)
                    if (address.address != null && address.address.id > 0) {
                        address_id = address.address.id.toString()
                        textName.text = address.address.realname + "  " + address.address.mobile
                        textAddress.text =
                            address.address.province + address.address.city + address.address.district + address.address.house_number
                        textAddress.setTextColor(mContext.resources.getColor(R.color.black))
                        //                        if (mode == MODE_NORMOL) {
                        //                            getPrice()
                        //                        }
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

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_SELECT_ADDRESS -> {
                //选择地址返回
                if (resultCode == Activity.RESULT_OK) {
                    var address = data?.getSerializableExtra("address") as AddressModel
                    address_id = address.id.toString()
                    textAddress.text =
                        address.province + address.city + address.district + address.house_number
                    textName.text = address.realname + "  " + address.mobile
                    textAddress.setTextColor(mContext.resources.getColor(R.color.black))
                }
            }
        }
    }

    /**
     * 兑换
     */
    private fun exchange(sign_image: String) {
        if (model?.is_address == "1") {
            if (TextUtils.isEmpty(address_id)) {
                ToastUtil.showShort("请选择地址")
                return
            }
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.EXCHANGE_PRODUCT)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "integral_product_id" - model?.id.toString()
                "number" - num.toString()
                "address_id" - address_id
                "sign_image" - sign_image
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    finish()
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
     * 兑换选中商品
     */
    private fun submit() {
        if (model?.is_address == "1") {
            if (TextUtils.isEmpty(address_id)) {
                ToastUtil.showShort("请选择地址")
                return
            }
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.CART_EXCHANGE_PRODUCT)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "shopCardIds" - ids
                "address_id" - address_id
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    finish()
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

    @SuppressLint("SetTextI18n")
    override fun getIntentData() {
        mode = intent.extras?.getInt("mode", MODE_SINGLE) ?: MODE_SINGLE
        num = intent.extras?.getInt("num", 1) ?: 1
        when (mode) {
            MODE_SINGLE -> {
                recyclerMul.visibility = View.GONE
                constraintSingle.visibility = View.VISIBLE
                try {
                    model = intent.getSerializableExtra("model") as CommodityModel
                } catch (e: Exception) {
                    ToastUtil.showShort("数据异常，请重试")
                    finish()
                }
                if (model == null) {
                    ToastUtil.showShort("数据异常，请重试")
                    finish()
                } else {
                    GlideLoadUtils.getInstance().glideLoad(
                        mContext,
                        TextUtils.getImgUrl(Constant.getBaseUrl(), model?.image),
                        mContext.resources.getDimensionPixelOffset(R.dimen.dp_8),
                        imgCommodity
                    )
                    textComName.text = model?.name
                    var price = ""
                    if (model?.price == "0.00") {
                        price =
                            "${Arith.mul(model?.integral?.toDouble() ?: 0.00, num.toDouble())}积分"
                    } else if (model?.integral == "0") {
                        price = "${mContext.resources.getString(R.string.CNY)}${Arith.mul(
                            model?.price?.toDouble() ?: 0.00,
                            num.toDouble()
                        )}"
                    } else {
                        price = "${mContext.resources.getString(R.string.CNY)}${Arith.mul(
                            model?.price?.toDouble() ?: 0.00,
                            num.toDouble()
                        )}+${Arith.mul(model?.integral?.toDouble() ?: 0.00, num.toDouble())}积分"
                    }
                    textPrice.text = price
                    textTotal.text = price
                    textNameBottom.text = price
                    textCode.text = num.toString() + model?.unit
                }
            }
            MODE_MUTI -> {
                ids = intent.extras?.getString("ids", "") ?: ""
                recyclerMul.visibility = View.VISIBLE
                constraintSingle.visibility = View.GONE
                try {
                    list.clear()
                    list.addAll(intent.getSerializableExtra("list") as MutableList<ShopcarModel>)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtil.showShort("数据异常，请重试")
                    finish()
                }
                if (list == null || list.size < 1) {
                    ToastUtil.showShort("数据异常，请重试")
                    finish()
                }
                recyclerMul.layoutManager = LinearLayoutManager(mContext)
                shopcarAdapter = ShopcarAdapter(mContext, list, ShopcarAdapter.MODE_INFO)
                recyclerMul.adapter = shopcarAdapter
                textTotal.text = shopcarAdapter.getSelectedPrice()
                textNameBottom.text = shopcarAdapter.getSelectedPrice()
            }
        }
    }

    override fun clear() {

    }

}