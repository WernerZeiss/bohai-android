package com.rice.bohai.fragment

import android.os.Bundle
import android.util.Log
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.FotgetActivity
import com.rice.bohai.activity.XieyiActivity
import com.rice.bohai.model.PasswordModel
import com.rice.bohai.model.UserModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseImmersionFragment() {

    var onLoginCompleteListener: OnLoginCompleteListener? = null

    interface OnLoginCompleteListener {
        fun onLoginComplete()
    }

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = true
        isWhiteNavigationBarIcon = false
        navigationBarColorId = R.color.new_login_bottom
        viewTopId = R.id.viewTop
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_login

    override fun initView() {
//        editUserName
//        editPassword
//        textBtnLogin
        textForgetPassword.setOnClickListener {
            //忘记密码
            ActivityUtils.openActivity(mContext, FotgetActivity::class.java)
        }
        textBtnLogin.setOnClickListener {
            if (TextUtils.isEmpty(editPhone.text.toString()) || editPhone.text.toString().length < 11) {
                ToastUtil.showShort("请输入正确的手机号")
                editPhone.setError("请输入正确的手机号")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(editPassword.text.toString())) {
                ToastUtil.showShort("请输入密码")
                editPassword.setError("请输入密码")
                return@setOnClickListener
            }
            if (!checkbox_login.isChecked) {
                ToastUtil.showShort("请同意协议")
                return@setOnClickListener
            }
            login()
        }
        textview_xieyi.setOnClickListener {
            var b = Bundle()
            b.putInt("type", 2)
            ActivityUtils.openActivity(mContext, XieyiActivity::class.java, b)
        }
    }

    private fun login() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.LOGIN)
            params {
                "mobile" - editPhone.text.toString()
                "password" - editPassword.text.toString()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: UserModel = StringNullAdapter.gson.fromJson(result)
                    MyApplication.instance.saveUserInfo(model)
//                Logger.d(status)
                    MyApplication.instance.savePassword(
                        PasswordModel(
                            editPhone.text.toString(),
                            editPassword.text.toString()
                        )
                    )
                    onLoginCompleteListener?.onLoginComplete()
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

}