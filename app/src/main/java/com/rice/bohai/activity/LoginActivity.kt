package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.view.View
import com.rice.base.RiceBaseActivity
import com.rice.bohai.MainActivity
import com.rice.bohai.R
import com.rice.bohai.fragment.LoginFragment
import com.rice.bohai.fragment.RegisterFragment
import com.rice.tool.ActivityUtils
import com.rice.tool.FragmentHelper
import kotlinx.android.synthetic.main.activity_login.*

@SuppressLint("Registered")
class LoginActivity : RiceBaseActivity() {

    var toMain = false //登录后前往主界面

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = true
        isWhiteNavigationBarIcon = false
        navigationBarColorId = R.color.new_login_bottom
        viewTopId = R.id.viewTop
    }

    var loginFragment = LoginFragment()
    var registerFragment = RegisterFragment()

    var mode = MODE_LOGIN

    companion object {
        val MODE_LOGIN = 0
        val MODE_REGISTER = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onBackPressed() {
        finish()
    }

    override fun initView() {
        loginFragment.onLoginCompleteListener = object : LoginFragment.OnLoginCompleteListener {
            override fun onLoginComplete() {
                //                var intent = Intent(MyApplication.instance, MainActivity::class.java)
                //                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                //                MyApplication.instance.startActivity(intent)
                if (toMain) {
//                    EventBus.getDefault().post("loginOnsuccess")
                    ActivityUtils.openActivity(mContext, MainActivity::class.java)
                }
                finish()
            }
        }
        registerFragment.onRegisterCompleteListener =
            object : RegisterFragment.OnRegisterCompleteListener {
                override fun onRegisterComplete() {
                    mode = MODE_LOGIN
                    changeMode()
                }
            }
        llLogin.setOnClickListener {
            mode = MODE_LOGIN
            changeMode()
        }
        llRegister.setOnClickListener {
            mode = MODE_REGISTER
            changeMode()
        }
        FragmentHelper.switchFragment(loginFragment, this, R.id.frameLogin)
    }

    fun changeMode() {
        textLogin.setTextColor(mContext.resources.getColor(R.color.black50))
        lineLogin.visibility = View.INVISIBLE
        lineRegister.visibility = View.INVISIBLE
        textRegister.setTextColor(mContext.resources.getColor(R.color.black50))
        when (mode) {
            MODE_LOGIN -> {
                textLogin.setTextColor(mContext.resources.getColor(R.color.black))
                lineLogin.visibility = View.VISIBLE
                FragmentHelper.switchFragment(loginFragment, this, R.id.frameLogin)
            }
            MODE_REGISTER -> {
                textRegister.setTextColor(mContext.resources.getColor(R.color.black))
                lineRegister.visibility = View.VISIBLE
                FragmentHelper.switchFragment(registerFragment, this, R.id.frameLogin)
            }
        }
    }

    override fun getIntentData() {
        toMain = intent.extras?.getBoolean("toMain", false) ?: false
    }

    override fun clear() {

    }
}