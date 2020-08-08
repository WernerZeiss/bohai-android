package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.ProfitHistoryModel
import com.rice.bohai.model.WDCCModel
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.DecimalDigitsInputFilter
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_xj2jf.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class XJ2JFActivity : RiceBaseActivity() {

    var mode = MODE_XJ2JF
    var model: WDCCModel? = null
    var xsphModel: ProfitHistoryModel? = null

    companion object {
        const val MODE_XJ2JF = 0
        const val MODE_DD2JF = 1
        const val MODE_XSPH2JF = 2
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_xj2jf
    }

    override fun initView() {
        //        llSelectOrder.setOnClickListener {
        //          if(mode==MODE_DD2JF){
        //
        //            }
        //        }
        //        textSelectOrder
        //        textBtn.setOnClickListener {  }
//        toolbar.setOnOkClickListener {
//            //积分明细
//            var b = Bundle()
//            b.putInt("tag", MyScoreActivity.TAG_JFMX)
//            ActivityUtils.openActivity(mContext, MyScoreActivity::class.java, b)
//        }
        initMode()
    }

    @SuppressLint("SetTextI18n")
    private fun initMode() {
        when (mode) {
            MODE_XJ2JF -> {
                //现金转积分
                toolbar.setTitle("现金转积分")
                textScale.text = MyApplication.instance.systemModel?.price_conversion_integral
                edit.hint = "请输入转换金额"
                edit.filters = arrayOf(DecimalDigitsInputFilter(2))
                edit.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                edit.isFocusable = true
                edit.isFocusableInTouchMode = true
                imgArrow.visibility = View.GONE
                textBtn.setOnClickListener { price2integral() }
            }
            MODE_DD2JF, MODE_XSPH2JF -> {
                //订单转积分
                toolbar.setTitle("订单转积分")
                when (mode) {
                    MODE_DD2JF -> {
                        if (model != null) {
                            llInfo.visibility = View.VISIBLE
                            edit.setText(model?.product_name)
                            textScore.text = model?.exchange_integral_num + "积分"
                        }
                    }
                    MODE_XSPH2JF -> {
                        llInfo.visibility = View.VISIBLE
                        edit.setText(xsphModel?.product_name)
                        textScore.text = xsphModel?.exchange_integral_num + "积分"
                    }
                }
                textScale.text = MyApplication.instance.systemModel?.order_conversion_integral
                edit.hint = "请选择转换订单"
                edit.filters = arrayOf()
                edit.inputType = InputType.TYPE_CLASS_TEXT
                edit.isFocusable = false
                edit.isFocusableInTouchMode = false
                imgArrow.visibility = View.VISIBLE
                llSelectOrder.setOnClickListener {
                    var b = Bundle()
                    b.putInt("mode", WDCCActivity.MODE_SELECT)
                    ActivityUtils.openActivity(
                        mContext,
                        WDCCActivity::class.java,
                        b,
                        Constant.REQUEST_SELECT_ORDER
                    )
                }
                edit.setOnClickListener {
                    var b = Bundle()
                    b.putInt("mode", WDCCActivity.MODE_SELECT)
                    ActivityUtils.openActivity(
                        mContext,
                        WDCCActivity::class.java,
                        b,
                        Constant.REQUEST_SELECT_ORDER
                    )
                }
                textBtn.setOnClickListener { order2integral() }
            }
        }
    }

    /**
     * 订单转积分
     */
    private fun order2integral() {
        if (model == null && xsphModel == null) {
            ToastUtil.showShort("请先选择订单")
            return
        }
        Http.post {
            when (mode) {
                MODE_DD2JF -> {
                    url = RiceHttpK.getUrl(Constant.ORDER_CONVERSION_INTEGRAL)
                }
                MODE_XSPH2JF -> {
                    url = RiceHttpK.getUrl(Constant.PROFIT_POSITION_CONVERSION_INTEGRAL)
                }
            }
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                when (mode) {
                    MODE_DD2JF -> {
                        "position_id" - model?.id.toString()
                    }
                    MODE_XSPH2JF -> {
                        "profit_position_id" - xsphModel?.id.toString()
                    }
                }
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    setResult(Activity.RESULT_OK)
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
     * 现金转积分
     */
    private fun price2integral() {
        if (TextUtils.isEmpty(edit.text.toString())) {
            ToastUtil.showShort("请输入转换金额")
            return
        } else if (edit.text.toString().toDouble() <= 0.00) {
            ToastUtil.showShort("转换金额不能为0")
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRICE_CONVERSION_INTEGRAL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "price" - edit.text.toString()
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_SELECT_ORDER -> {
                //选择订单返回
                if (resultCode == Activity.RESULT_OK) {
                    model = data?.extras?.getSerializable("wdcc") as WDCCModel?
                    if (model != null) {
                        llInfo.visibility = View.VISIBLE
                        edit.setText(model?.product_name)
                        textScore.text = model?.exchange_integral_num + "积分"
                    }
                }
            }
        }
    }

    override fun getIntentData() {
        mode = intent.extras?.getInt("mode", MODE_XJ2JF) ?: MODE_XJ2JF
        model = intent.extras?.getSerializable("wdcc") as WDCCModel?
        xsphModel = intent.extras?.getSerializable("xsph") as ProfitHistoryModel?
    }

    override fun clear() {

    }

}
