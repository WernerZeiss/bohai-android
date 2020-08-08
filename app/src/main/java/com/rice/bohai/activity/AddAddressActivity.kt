package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.AddressModel
import com.rice.dialog.AreaSelecterDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ToastUtil
import com.rice.view.RiceToolbar
import kotlinx.android.synthetic.main.activity_add_address.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class AddAddressActivity : RiceBaseActivity() {

    var model: AddressModel? = null
    var hasBack = true

    override fun getLayoutId(): Int {
        return R.layout.activity_add_address
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (hasBack) {
            toolbar.setBackMode(RiceToolbar.MODE_IMG)
        } else {
            toolbar.setBackMode(RiceToolbar.MODE_HIDE)
        }
        if (model != null) {
            editName.setText(model?.realname)
            editPhone.setText(model?.mobile)
            editHouseNum.setText(model?.house_number)
            textAddress.text = "${model?.province}-${model?.city}-${model?.district}"
            textAddress.setTextColor(mContext.resources.getColor(R.color.black))
        }
        constraintArea.setOnClickListener {
            showAddressSelector()
        }
        textBtnSubmit.setOnClickListener {
            addAddress()
        }
    }

    override fun onBackPressed() {
        if (hasBack) {
            super.onBackPressed()
        }
    }

    /**
     * 显示地址选择器
     */
    @SuppressLint("SetTextI18n")
    fun showAddressSelector() {
        AreaSelecterDialog.setOnSelectedListener { provinceId, cityId, districtId, provinceName, cityName, districtName ->
            textAddress.text = "$provinceName-$cityName-$districtName"
            textAddress.setTextColor(mContext.resources.getColor(R.color.black))
        }
        AreaSelecterDialog.getInstance(mContext as AppCompatActivity)
    }

    /**
     * 添加/修改地址
     */
    private fun addAddress() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_ADDRESS)
            params {
                if (model != null) {
                    "id" - model?.id.toString()
                }
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "realname" - editName.text.toString()
                "mobile" - editPhone.text.toString()
                "province" - textAddress.text.toString().split("-")[0]
                "city" - textAddress.text.toString().split("-")[1]
                "district" - textAddress.text.toString().split("-")[2]
                "house_number" - editHouseNum.text.toString()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                var data = byts.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    MyApplication.instance.userInfo!!.is_address = "1"
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

    override fun getIntentData() {
        hasBack = intent.extras?.getBoolean("hasBack", true) ?: true
        try {
            model = intent.extras?.getSerializable("model") as AddressModel
        } catch (e: Exception) {
            e.printStackTrace()
//            ToastUtil.showShort("数据异常，请重试")
        }
    }

    override fun clear() {

    }

}