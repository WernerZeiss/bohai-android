package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_edit_login_password.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class EditPasswordActivity : RiceBaseActivity() {

    var mode = MODE_MODIFY_LOGIN_PASSWORD
    private var timeCount: TimeCount? = null

    companion object {
        const val MODE_MODIFY_LOGIN_PASSWORD = 0//修改登录密码
        const val MODE_SET_PY_PASSWORD = 1//设置交易密码
        const val MODE_FORGET_PY_PASSWORD = 2//忘记交易密码
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_edit_login_password
    }

    override fun initView() {
        initMode()
        textSendVer.setOnClickListener {
            if (TextUtils.isEmpty(editPhone.text.toString()) || editPhone.text.toString().length < 11) {
                ToastUtil.showShort("请输入正确的手机号")
                editPhone.setError("请输入正确的手机号")
                return@setOnClickListener
            }
            sendRequestCode()
        }
        textBtnSubmit.setOnClickListener {
            if (mode == MODE_FORGET_PY_PASSWORD) {
                if (TextUtils.isEmpty(editBankCard.text.toString())) {
                    ToastUtil.showShort("请输入银行卡号")
                    editBankCard.setError("请输入银行卡号")
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(editIdCard.text.toString())) {
                    ToastUtil.showShort("请输入身份证号")
                    editIdCard.setError("请输入身份证号")
                    return@setOnClickListener
                }
            }
            if (TextUtils.isEmpty(editPhone.text.toString()) || editPhone.text.toString().length < 11) {
                ToastUtil.showShort("请输入正确的手机号")
                editPhone.setError("请输入正确的手机号")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editVer.text.toString())) {
                ToastUtil.showShort("请输入${editVer.hint}")
                editVer.setError("请输入${editVer.hint}")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editOldPassword.text.toString())) {
                ToastUtil.showShort("请输入${editOldPassword.hint}")
                editOldPassword.setError("请输入${editOldPassword.hint}")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editNewPassword.text.toString())) {
                ToastUtil.showShort("请输入${editNewPassword.hint}")
                editNewPassword.setError("请输入${editNewPassword.hint}")
                return@setOnClickListener
            }
            if (mode == MODE_SET_PY_PASSWORD) {
                if (editOldPassword.text.toString() != editNewPassword.text.toString()) {
                    ToastUtil.showShort("两次输入的密码不一致")
                    editOldPassword.setError("两次输入的密码不一致")
                    editNewPassword.setError("两次输入的密码不一致")
                    return@setOnClickListener
                }
            }
            setPassword()
        }
    }

    private fun initMode() {
        when (mode) {
            MODE_MODIFY_LOGIN_PASSWORD -> {
                //修改登录密码
                toolbar.setTitle("修改登录密码")
                editOldPassword.hint = "原密码"
                editNewPassword.hint = "新密码"
                editBankCard.visibility = View.GONE
                editIdCard.visibility = View.GONE
            }
            MODE_SET_PY_PASSWORD -> {
                //设置交易密码
                toolbar.setTitle("设置交易密码")
                editOldPassword.hint = "密码"
                editNewPassword.hint = "确认密码"
                editBankCard.visibility = View.GONE
                editIdCard.visibility = View.GONE
            }
            MODE_FORGET_PY_PASSWORD -> {
                //忘记交易密码
                toolbar.setTitle("忘记交易密码")
                editOldPassword.hint = "原密码"
                editNewPassword.hint = "新密码"
                editBankCard.visibility = View.VISIBLE
                editIdCard.visibility = View.VISIBLE
            }
        }
    }

    override fun getIntentData() {
        mode =
                intent.extras?.getInt("mode", MODE_MODIFY_LOGIN_PASSWORD)
                        ?: MODE_MODIFY_LOGIN_PASSWORD
    }

    private fun setPassword() {
        Http.post {
            when (mode) {
                MODE_MODIFY_LOGIN_PASSWORD -> {
                    //修改登录密码
                    url = RiceHttpK.getUrl(Constant.UPDATE_PASSWORD)
                }
                MODE_SET_PY_PASSWORD -> {
                    //设置交易密码
                    url = RiceHttpK.getUrl(Constant.SET_TRANSACTION_PASSWORD)
                }
                MODE_FORGET_PY_PASSWORD -> {
                    //忘记交易密码
                    url = RiceHttpK.getUrl(Constant.FORGET_TRANSACTION_PASSWORD)
                }
            }
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "mobile" - editPhone.text.toString()
                "code" - editVer.text.toString()
                when (mode) {
                    MODE_MODIFY_LOGIN_PASSWORD -> {
                        //修改登录密码
                        "old_password" - editOldPassword.text.toString()
                        "new_password" - editNewPassword.text.toString()
                    }
                    MODE_SET_PY_PASSWORD -> {
                        //设置交易密码
                        "password" - editOldPassword.text.toString()
                        "rePassword" - editNewPassword.text.toString()
                    }
                    MODE_FORGET_PY_PASSWORD -> {
                        //忘记交易密码
                        "password" - editOldPassword.text.toString()
                        "bank_number" - editBankCard.text.toString()
                        "id_number" - editIdCard.text.toString()
                    }
                }
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                var data = byts.toString(Charset.defaultCharset())
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

    override fun clear() {

    }

}