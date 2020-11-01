package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.AddressModel
import com.rice.bohai.model.DefaultAddressModel
import com.rice.bohai.model.TranslatePirceModel
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_extract.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class ExtractActivity : RiceBaseActivity() {

    var position_id = ""
    var address_id = ""
    var number = ""
    var maxNumber = 0
    var mode = MODE_NORMOL

    companion object {
        const val MODE_NORMOL = 0 //普通提货
        const val MODE_THQ = 1 //提货券提货
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_extract
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        textAddress.setOnClickListener {
            var b = Bundle()
            b.putInt("mode", MyAddressActivity.MODE_SELECT)
            ActivityUtils.openActivity(mContext, MyAddressActivity::class.java, b, Constant.REQUEST_SELECT_ADDRESS)
            //            if (TextUtils.isNotEmpty(editNumber.text.toString()) && TextUtils.isPositiveInt(editNumber.text.toString())) {
            //            getPrice()
            //            } else {
            //                textFare.text = "运费：${mContext.resources.getString(R.string.CNY)}?"
            //            }
        }
        initMode()
        textBtnSubmit.setOnClickListener {
            if (TextUtils.isEmpty(address_id)) {
                textAddress.setTextColor(mContext.resources.getColor(R.color.navi_bottom))
                ToastUtil.showShort("请选择地址")
                return@setOnClickListener
            }
            //            if (TextUtils.isEmpty(editNumber.text.toString())) {
            //                editNumber.setError("请输入提货数量")
            //                ToastUtil.showShort("请输入提货数量")
            //                return@setOnClickListener
            //            }
            pickUp()
        }
        getDefaultAddress()
    }

    @SuppressLint("SetTextI18n")
    private fun initMode() {
        when (mode) {
            MODE_NORMOL -> {
                //普通提货
//                textNumber.text = "提货数量：${maxNumber}"
                textNumber.visibility = View.VISIBLE
                textFare.visibility = View.VISIBLE
                editRemarks.visibility = View.VISIBLE
                editNumber.visibility = View.GONE
            }
            MODE_THQ -> {
                //提货券提货
                textNumber.visibility = View.GONE
                editNumber.visibility = View.VISIBLE
                textFare.visibility = View.GONE
                editRemarks.visibility = View.GONE
                editNumber.setHint("提货数量（最多${maxNumber}）")
                //                editNumber.addTextChangedListener(object : TextWatcher {
                //                    override fun afterTextChanged(s: Editable?) {
                //                        if (TextUtils.isNotEmpty(editNumber.text.toString()) && TextUtils.isPositiveInt(editNumber.text.toString())) {
                //                            //                            if (editNumber.text.toString().toInt() > maxNumber) {
                //                            //                                editNumber.setError("最大提货数量不能超过$maxNumber")
                //                            //                                return
                //                            //                            }
                //                            if (TextUtils.isNotEmpty(address_id)) {
                //                                getPrice()
                //                            } else {
                //                                textFare.text = "运费：${mContext.resources.getString(R.string.CNY)}?"
                //                            }
                //                        } else {
                //                            textFare.text = "运费：${mContext.resources.getString(R.string.CNY)}?"
                //                        }
                //                    }
                //
                //                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
                //                    }
                //
                //                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //
                //                    }
                //                })
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
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    Logger.d(result)
                    var address: DefaultAddressModel = StringNullAdapter.gson.fromJson(result)
                    if (address.address != null && address.address.id > 0) {
                        address_id = address.address.id.toString()
                        textAddress.text = address.address.province + address.address.city + address.address.district + address.address.house_number
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

    /**
     * 获取运费
     */
    @SuppressLint("SetTextI18n")
    private fun getPrice() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.POSITION_PICK_UP_GET_FREIGHT_PRICE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "position_id" - position_id
                "address_id" - address_id
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    Logger.d(result)
                    val model: TranslatePirceModel = StringNullAdapter.gson.fromJson(result)
                    textFare.text = "运费：${mContext.resources.getString(R.string.CNY)}${model.freight_price}"
                } else {
                    textFare.text = "运费：${mContext.resources.getString(R.string.CNY)}?"
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
     * 提货
     */
    private fun pickUp() {
        when (mode) {
            MODE_THQ -> {
                if (TextUtils.isEmpty(editNumber.text.toString())) {
                    ToastUtil.showShort("请输入提货数量")
                    return
                }
            }
        }
        Http.post {
            when (mode) {
                MODE_NORMOL -> {
//                    url = RiceHttpK.getUrl(Constant.POSITION_PICK_UP)
                    url = RiceHttpK.getUrl(Constant.MY_PROFIT_POSITION_PICK_UP)
                }
                MODE_THQ -> {
                    url = RiceHttpK.getUrl(Constant.PICK_UP_SETTLEMENT)
                }
            }
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "address_id" - address_id
                when (mode) {
                    MODE_NORMOL -> {
                        "profit_position_id" - position_id
//                        "number" - maxNumber.toString()
                        "remark" - editRemarks.text.toString()
                    }
                    MODE_THQ -> {
                        "number" - editNumber.text.toString()
                        "id" - position_id
                    }
                }
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                Log.e(url+"->",data)
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    finish()
                }
                //                    timeCount = TimeCount(60000, 1000)
                //                    timeCount!!.start()
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
                    textAddress.text = address.province + address.city + address.district + address.house_number
                    textAddress.setTextColor(mContext.resources.getColor(R.color.black))
                }
            }
        }
    }

    override fun getIntentData() {
        position_id = intent.extras?.getString("id", "") ?: ""
        maxNumber = intent.extras?.getInt("number", 0) ?: 0
        mode = intent.extras?.getInt("mode", MODE_NORMOL) ?: MODE_NORMOL
    }

    override fun clear() {

    }

}