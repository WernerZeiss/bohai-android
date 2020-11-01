package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.dialog.DialogHelper
import com.rice.bohai.listener.OnSelectCardListener
import com.rice.bohai.model.CardListModel
import com.rice.bohai.model.CardModel
import com.rice.bohai.tools.ClickUtils
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.DecimalDigitsInputFilter
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_cashout.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class CashoutActivity : RiceBaseActivity() {

    lateinit var loadingDialog: RLoadingDialog
    private var timeCount: TimeCount? = null
    private var selectCardDialog: Dialog? = null
    private var selectedCard: CardModel? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_cashout
    }

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        editPrice.filters = arrayOf(DecimalDigitsInputFilter(2))
        textCashAll.setOnClickListener {
            editPrice.setText(textPrice.text)
        }
        textBtnSubmit.setOnClickListener {
            if (!ClickUtils.enableClick()){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editPrice.text.toString().trim())) {
                ToastUtil.showShort("请输入提现金额~")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(et_phone_code.text.toString().trim())) {
                ToastUtil.showShort("请输入验证码~")
                return@setOnClickListener
            }
            if (selectedCard == null) {
                ToastUtil.showShort("请选择提现银行卡~")
                return@setOnClickListener
            }
            cash()
        }
        toolbar.setOnOkClickListener {
            var b = Bundle()
            b.putInt("mode", RechargeHistoryActivity.MODE_CASHOUT)
            ActivityUtils.openActivity(mContext, RechargeHistoryActivity::class.java, b)
        }


        //选择银行卡
        tv_select_card.setOnClickListener {
            selectCard()
        }

        tv_code.setOnClickListener {
            sendCode(MyApplication.instance.userInfo?.user_phone.toString())
        }
        getMyBanks()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance.onUserInfoUpdateCompleteListener =
            object : MyApplication.OnUserInfoUpdateCompleteListener {
                override fun onUserInfoUpdateComplete() {
                    textPrice.text = MyApplication.instance.userInfo?.price
                    textPrice2.text = MyApplication.instance.userInfo?.price
                    tv_phone.text = "手机号：" + MyApplication.instance.userInfo?.user_phone
                }
            }
        MyApplication.instance.getUserInfoFromWeb()
    }

    /**
     * 获取我的银行卡列表
     */
    private fun getMyBanks(){
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_MY_BANK_CARD_LIST)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { bytes ->
                val result = RiceHttpK.getResult(mContext, bytes)
                if (!TextUtils.isEmpty(result)) {
                    val model: CardListModel = StringNullAdapter.gson.fromJson(result)
                    if (!model.lists.isNullOrEmpty()) {
                        model.lists.forEach {
                            if (it.is_default == 1){
                                //默认银行卡
                                selectedCard = it
                                tv_select_card.text = selectedCard?.name + "  ＞"
                            }
                        }
                        selectCardDialog = DialogHelper.getSelectCardDialog(
                            this@CashoutActivity,
                            model.lists,
                            object : OnSelectCardListener {
                                override fun onSelectCard(position: Int, card: CardModel) {
                                    selectCardDialog?.dismiss()
                                    selectedCard = card
                                    tv_select_card.text = selectedCard?.name + "  ＞"
                                }
                            }
                        )
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


    private fun selectCard() {
        if (selectCardDialog == null) {
            Http.post {
                url = RiceHttpK.getUrl(Constant.GET_MY_BANK_CARD_LIST)
                params {
                    "access_token" - MyApplication.instance.userInfo!!.access_token
                }
                onSuccess { bytes ->
                    val result = RiceHttpK.getResult(mContext, bytes)
                    if (!TextUtils.isEmpty(result)) {
                        val model: CardListModel = StringNullAdapter.gson.fromJson(result)
                        if (!model.lists.isNullOrEmpty()) {
                            selectCardDialog = DialogHelper.getSelectCardDialog(
                                this@CashoutActivity,
                                model.lists,
                                object : OnSelectCardListener {
                                    override fun onSelectCard(position: Int, card: CardModel) {
                                        selectCardDialog?.dismiss()
                                        selectedCard = card
                                        tv_select_card.text = selectedCard?.name + "  ＞"
                                    }
                                }
                            )
                            selectCardDialog?.show()
                        } else {
                            ToastUtil.showShort("暂无银行卡~")
                        }
                    } else {
                        ToastUtil.showShort("数据获取错误，请稍后再试~")
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
        } else {
            selectCardDialog?.show()
        }
    }


    /**
     * 发送验证码请求
     */
    private fun sendCode(phone: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_MOBILE_CODE)
            params {
                "mobile" - phone
            }
            onSuccess { byts ->
                val result: String? = RiceHttpK.getResult(mContext, byts)
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShort("验证码已发送")
                    timeCount = TimeCount(60000, 1000)
                    timeCount!!.start()
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
     * 验证码计时器
     */
    inner class TimeCount(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            tv_code.isClickable = false
            tv_code.text = (millisUntilFinished / 1000).toString() + "秒后重新获取"
        }

        override fun onFinish() {
            tv_code.text = "点击重新获取"
            tv_code.isClickable = true
        }
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
                "code" - et_phone_code.text.toString()
                "bank_id" - selectedCard!!.id
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
            onStart {
                if (!loadingDialog.isShowing) {
                    loadingDialog.show()
                }
            }
            onFinish {
                loadingDialog.dismiss()
            }
        }
    }


    override fun getIntentData() {

    }

    override fun clear() {

    }

}