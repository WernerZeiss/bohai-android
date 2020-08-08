package com.rice.bohai.fragment

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.fragment_register.*
import java.nio.charset.Charset

class RegisterFragment : BaseImmersionFragment() {

    private var timeCount: TimeCount? = null
    var onRegisterCompleteListener: OnRegisterCompleteListener? = null

    interface OnRegisterCompleteListener {
        fun onRegisterComplete()
    }

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = true
        isWhiteNavigationBarIcon = false
        navigationBarColorId = R.color.new_login_bottom
        viewTopId = R.id.viewTop
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_register

    override fun initView() {
        textSendVer.setOnClickListener {
            if (TextUtils.isEmpty(editPhone.text.toString()) || editPhone.text.toString().length < 11) {
                ToastUtil.showShort("请输入正确的手机号")
                return@setOnClickListener
            }
            sendRequestCode()
        }
        textBtnRegister.setOnClickListener {
            if (TextUtils.isEmpty(editPhone.text.toString()) || editPhone.text.toString().length < 11) {
                ToastUtil.showShort("请输入正确的手机号")
                editPhone.setError("请输入正确的手机号")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editVer.text.toString())) {
                ToastUtil.showShort("请输入验证码")
                editVer.setError("请输入验证码")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editPassword.text.toString())) {
                ToastUtil.showShort("请输入密码")
                editPassword.setError("请输入密码")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editPassword2.text.toString())) {
                ToastUtil.showShort("请再次输入密码")
                editPassword2.setError("请再次输入密码")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editInvCode.text.toString())) {
                ToastUtil.showShort("请输入邀请码")
                editInvCode.setError("请输入邀请码")
                return@setOnClickListener
            }
            if (editPassword.text.toString() != editPassword2.text.toString()) {
                ToastUtil.showShort("两次输入的密码不一致")
                editPassword.setError("两次输入的密码不一致")
                editPassword2.setError("两次输入的密码不一致")
                return@setOnClickListener
            }
//            if (Common.empty(etRecommandPhone!!.text.toString())) {
//                ToastUtil.showShort(R.string.tip_phoneisempty_recommand)
//                return@setOnClickListener
//            }
//            network_register.fathermobile = etRecommandPhone!!.text.toString()

            if (!checkBoxXieyi!!.isChecked) {
                ToastUtil.showShort("您需要同意协议方可注册")
                return@setOnClickListener
            }
//            val preferences = getSharedPreferences(
//                    "DEVICE_TOKEN", Context.MODE_PRIVATE)
//            if (!Common.empty(preferences.getString("device_token", ""))) {
//                network_register.device = preferences.getString("device_token", "")
            register()
//            when (mode) {
//                MODE_REGIST -> {
//                    sendRegister()
//                }
//                MODE_FORGET -> {
//
//                }
//            }
//            } else {
//                ToastUtil.showLong("获取DeviceToken失败")
//            }
        }
    }

    /**
     * 发送验证码请求
     */
    private fun sendRequestCode() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_MOBILE_CODE)
            params {
                "mobile" - editPhone.text.toString()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result: String? = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
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
    internal inner class TimeCount(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            textSendVer.isClickable = false
            textSendVer.text = (millisUntilFinished / 1000).toString() + "秒后重新获取"
        }

        override fun onFinish() {
            textSendVer.text = "点击重新获取"
            textSendVer.isClickable = true
        }
    }

    /**
     * 注册
     */
    private fun register() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.REGISTER)
            params {
                "mobile" - editPhone.text.toString()
                "code" - editVer.text.toString()
                "password" - editPassword.text.toString()
                "rePassword" - editPassword2.text.toString()
                "recommend_code" - editInvCode.text.toString()
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    editPassword.setText("")
                    editPassword2.setText("")
                    timeCount?.cancel()
                    onRegisterCompleteListener?.onRegisterComplete()
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

}