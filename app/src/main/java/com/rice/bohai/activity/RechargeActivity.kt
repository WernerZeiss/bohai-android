package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.alipay.sdk.app.PayTask
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.demo.PayResult
import com.rice.bohai.model.OrderNumModel
import com.rice.bohai.model.StringModel
import com.rice.bohai.model.WXPayModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_recharge.*
import kotlinx.android.synthetic.main.include_pay_function.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class RechargeActivity : RiceBaseActivity(), IWXAPIEventHandler {

    val SDK_PAY_FLAG = 1
    var payFunction = PAY_WECHAT

    companion object {
        val PAY_WECHAT = 0
        val PAY_ALIPAY = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_recharge
    }

    override fun initView() {
        toolbar.setOnOkClickListener {
            //            var b = Bundle()
            //            b.putInt("mode", DataActivity.MODE_RECHARGE_HISTORY)
            //            ActivityUtils.openActivity(mContext, DataActivity::class.java, b)
            ActivityUtils.openActivity(mContext, RechargeHistoryActivity::class.java)
        }
        if (MyApplication.instance.systemModel?.is_start_ali_pay == "1") {
            llAlipay.visibility = View.VISIBLE
            if (MyApplication.instance.systemModel?.is_start_wx_pay != "1") {
                //默认选中支付宝支付
                changePayFunction(PAY_ALIPAY)
            }
        } else {
            llAlipay.visibility = View.GONE
        }
        if (MyApplication.instance.systemModel?.is_start_wx_pay == "1") {
            llWechat.visibility = View.VISIBLE
            if (MyApplication.instance.systemModel?.is_start_ali_pay != "1") {
                //默认选中微信支付
                changePayFunction(PAY_WECHAT)
            }
        } else {
            llWechat.visibility = View.GONE
        }
        llWechat.setOnClickListener { changePayFunction(PAY_WECHAT) }
        llAlipay.setOnClickListener { changePayFunction(PAY_ALIPAY) }
        textBtnSubmit.setOnClickListener {
            //充值
            var payFun = ""
            when (payFunction) {
                PAY_WECHAT -> {
                    payFun = "1"
                }
                PAY_ALIPAY -> {
                    payFun = "2"
                }
            }
            if (TextUtils.isEmpty(editPrice.text.toString())) {
                ToastUtil.showShort("请输入充值金额")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(payFun)) {
                ToastUtil.showShort("请选择充值方式")
            } else {
                getOrderNum(payFun)
            }
        }
    }

    fun changePayFunction(payFunction: Int) {
        this.payFunction = payFunction
        when (payFunction) {
            PAY_WECHAT -> {
                imgCheckWechat.setImageResource(R.drawable.icon_check_address_focus)
                imgCheckAlipay.setImageResource(R.drawable.icon_check_address)
            }
            PAY_ALIPAY -> {
                imgCheckWechat.setImageResource(R.drawable.icon_check_address)
                imgCheckAlipay.setImageResource(R.drawable.icon_check_address_focus)
            }
        }
    }

    /**
     * 提交充值订单
     */
    fun getOrderNum(payFun: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.RECHARGE_ORDER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "price" - editPrice.text.toString()
                "type" - payFun
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    when (payFun) {
                        "1" -> {
                            //微信支付
                            val model: OrderNumModel = StringNullAdapter.gson.fromJson(result)
                            payFromWX(model.order_number)
                        }
                        "2" -> {
                            //支付宝支付
                            val model: OrderNumModel = StringNullAdapter.gson.fromJson(result)
                            payFromAliPay(model.order_number)
                        }
                    }
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 获取微信支付数据
     */
    fun payFromWX(order_number: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.WX_PAY_ORDER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "order_number" - order_number
            }
            onSuccess { bytes ->
                val result = RiceHttpK.getResult(mContext, bytes)
                if (TextUtils.isNotEmpty(result)) {
                    val model: WXPayModel = StringNullAdapter.gson.fromJson(result)
                    val wxapi =
                        WXAPIFactory.createWXAPI(mContext, model.appid)  //应用ID 即微信开放平台审核通过的应用APPID
                    wxapi.registerApp(model.appid)    //应用ID
                    wxapi.handleIntent(intent, this@RechargeActivity)
                    val payReq = PayReq()
                    payReq.appId = model.appid        //应用ID
                    payReq.partnerId = model.partnerid      //商户号 即微信支付分配的商户号
                    payReq.prepayId = model.prepayid      //预支付交易会话ID
                    payReq.packageValue = "Sign=WXPay"  //扩展字段
                    payReq.nonceStr = model.noncestr    //随机字符串不长于32位。
                    payReq.timeStamp = model.timestamp.toString()  //时间戳
                    payReq.sign = model.sign     //签名
                    wxapi.sendReq(payReq)
                }
                onFail { error ->
                    var message = error.message
                    if ((error.message ?: "").contains("java")) {
                        message = "未知错误"
                    }
                    ToastUtil.showShort(message)
                }
            }
        }
    }

    /**
     * 获取支付宝支付数据
     */
    fun payFromAliPay(order_number: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.ALI_PAY_ORDER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "order_number" - order_number
            }
            onSuccess { bytes ->
                //                val result = RiceHttpK.getResult(mContext,byts)
                var data = bytes.toString(Charset.defaultCharset())
                //                var data = "{\"code\":0,\"data\":\"alipay_sdk=alipay-sdk-php-20161101&app_id=2018011901973829&biz_content=%7B%22out_trade_no%22%3A%22201911251430013595%22%2C%22total_amount%22%3A0.01%2C%22subject%22%3A%22%5Cu652f%5Cu4ed8%5Cu5b9d%5Cu652f%5Cu4ed8%22%2C%22timeout_express%22%3A%225m%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fjiakao.ricecs.cn%2Fv1%2Fwx-pay-notify&sign_type=RSA2&timestamp=2019-11-25+14%3A30%3A01&version=1.0&sign=C8P73venN5TxNKKp6YffPp6q5ZVNujKclIUIVtHIHUtYOx7hAY%2FNcNw8Cvi41kdqBwgNlzPJ90YQiWxaUKlMnng%2F0Mb96flQSTRfLNSjJelHGtp%2BxG66KN4r1JuInifcZA7ltnu4FDG6SEXOZriKDE1gjIsaLBGveQbjSMGpk%2F%2F9RUDqU12cY5UsfqLS4fGVo%2BUHMQTQjYDX7aXnX722ZzlYjWROFPAj7lfXIaYKFw5BhLHISZ8o9foUZoVF6xI5tm90xtqty8K1T9JCuJLp8jmkoijt8cSc2inSOuPdwV0S7hOEwILova5xQ16EhLt8b8%2FLDJvLp369hcwLEUMaZw%3D%3D\"}"
                val model: StringModel = StringNullAdapter.gson.fromJson(data)
                if (TextUtils.isNotEmpty(model.data)) {
                    //                    val model: StringDataModel = StringNullAdapter.gson.fromJson(result)
                    val payRunnable = Runnable {
                        // 构造PayTask 对象
                        val alipay = PayTask(this@RechargeActivity)
                        // 调用支付接口，获取支付结果
                        Logger.d(model.data)
                        val result: Map<String, String> = alipay.payV2(model.data, true)
                        //                                val result = alipay.payV2(string, true)

                        val msg = Message()
                        msg.what = SDK_PAY_FLAG
                        msg.obj = result
                        mHandler.sendMessage(msg)
                    }
                    // 必须异步调用
                    val payThread = Thread(payRunnable)
                    payThread.start()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }


    // 支付宝
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    val payResult = PayResult(msg.obj as Map<String, String>)
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    val resultInfo = payResult.result // 同步返回需要验证的信息
                    when (payResult.resultStatus) {
                        "9000" -> { // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                            ToastUtil.showShort("支付成功")
                            MyApplication.instance.getUserInfoFromWeb()
                            runOnUiThread {
                                finish()
                            }
                        }
                        "8000" -> ToastUtil.showShort("支付结果确认中") // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        //                        "4000" -> ToastUtils.showShort(payResult.memo)
                        else -> ToastUtil.showShort("支付失败")
                    }
                }
            }
        }
    }

    override fun onResp(p0: BaseResp?) {

    }

    override fun onReq(p0: BaseReq?) {

    }

    override fun getIntentData() {

    }

    override fun clear() {

    }

}