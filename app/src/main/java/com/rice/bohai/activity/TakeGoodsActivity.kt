package com.rice.bohai.activity

import android.app.Activity
import android.app.Dialog
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
import com.rice.bohai.dialog.DialogHelper
import com.rice.bohai.model.AddressModel
import com.rice.bohai.model.DefaultAddressModel
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_takegoods.*
import java.nio.charset.Charset

/**
 * @author CWQ
 * @date 2020/10/27
 * 提货
 */
class TakeGoodsActivity : RiceBaseActivity(), View.OnClickListener {

    lateinit var loadingDialog: RLoadingDialog
    private var addressModel: AddressModel? = null
    private var failDialog: Dialog? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_takegoods
    }

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        cl_address.setOnClickListener(this)
        tv_submit.setOnClickListener(this)

        getDefaultAddress()
    }

    override fun getIntentData() {

    }

    override fun clear() {

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cl_address -> {
                //选择地址
                val b = Bundle()
                b.putInt("mode", MyAddressActivity.MODE_SELECT)
                ActivityUtils.openActivity(
                    mContext,
                    MyAddressActivity::class.java,
                    b,
                    Constant.REQUEST_SELECT_ADDRESS
                )
            }
            R.id.tv_submit -> {
                //提交
                submit()
            }
        }
    }


    private fun submit() {
        if (addressModel == null) {
            ToastUtil.showShort("请选择地址")
            return
        }
        val numStr = et_goods_num.text.toString().trim()
        if (TextUtils.isEmpty(numStr) || numStr.toInt() <= 0) {
            ToastUtil.showShort("请输入提货数量")
            return
        }
        val remark = et_take_remark.text.toString().trim()
        Http.post {
            url = RiceHttpK.getUrl(Constant.GROUP_PICK_UP)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "address_id" - addressModel?.id.toString()
                if (TextUtils.isNotEmpty(remark)){
                    "remark" - remark
                }
                "number" - numStr
            }
            onStart {
                loadingDialog.show()
            }
            onSuccess { byts ->
                val data = byts.toString(Charset.defaultCharset())
//                Log.i("group-pick-up->", data)
                val status = PublicModel.forjson(data)
                if (status.code == RiceHttpK.SUCCESS) {
                    ToastUtil.showShort(status.message)
                    finish()
                }else{
                    showFailDialog()
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
            onFinish {
                loadingDialog.hide()
            }
        }
    }


    private fun showFailDialog() {
        failDialog = DialogHelper.getSingleDialog(this,
            "温馨提示", "您好，您当前购货券不足，暂时不能兑换您所输入的数量，请重新输入",null)
        failDialog?.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_SELECT_ADDRESS -> {
                //选择地址返回
                if (resultCode == Activity.RESULT_OK) {
                    addressModel = data?.getSerializableExtra("address") as AddressModel
                    setAddressMsg()
                }
            }
        }
    }


    /**
     * 获取默认地址
     */
    private fun getDefaultAddress() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_DEFAULT_ADDRESS)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: DefaultAddressModel = StringNullAdapter.gson.fromJson(result)
                    if (model.address != null && model.address.id > 0) {
                        addressModel = model.address
                        setAddressMsg()
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


    private fun setAddressMsg() {
        tv_name.text = "姓名：" + addressModel?.realname
        tv_address.text = "详细地址：" +
                addressModel?.province + addressModel?.city + addressModel?.district + addressModel?.house_number
        tv_phone.text = "收货电话：" + addressModel?.mobile
    }
}