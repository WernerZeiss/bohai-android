package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.DecimalDigitsInputFilter
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_cashout.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class CashoutActivity : RiceBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_cashout
    }

    override fun initView() {
        editPrice.filters = arrayOf(DecimalDigitsInputFilter(2))
        textCashAll.setOnClickListener {
            editPrice.setText(textPrice.text)
        }
        textBtnSubmit.setOnClickListener {
            cash()
        }
        toolbar.setOnOkClickListener {
            var b = Bundle()
            b.putInt("mode", RechargeHistoryActivity.MODE_CASHOUT)
            ActivityUtils.openActivity(mContext, RechargeHistoryActivity::class.java, b)
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance.onUserInfoUpdateCompleteListener = object : MyApplication.OnUserInfoUpdateCompleteListener {
            override fun onUserInfoUpdateComplete() {
                textPrice.text = MyApplication.instance.userInfo?.price
                textPrice2.text = MyApplication.instance.userInfo?.price
            }
        }
        MyApplication.instance.getUserInfoFromWeb()
    }

    /**
     * 提现
     */
    private fun cash() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRESENT)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "price" - editPrice.text.toString()
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
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

    override fun getIntentData() {

    }

    override fun clear() {

    }

}