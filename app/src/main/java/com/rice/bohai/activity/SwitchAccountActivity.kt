package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MainActivity
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.AccountAdapter
import com.rice.bohai.model.UserModel
import com.rice.dialog.OkCancelDialog
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_switch.*

@SuppressLint("Registered")
class SwitchAccountActivity : RiceBaseActivity() {

    lateinit var accountAdapter: AccountAdapter
    lateinit var loginDialog: OkCancelDialog
    var mobile = ""
    var password = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_switch
    }

    override fun initView() {
        loginDialog = OkCancelDialog(mContext)
        loginDialog.setInfo("确定要切换账号吗？当前登录的账号将会退出。")
        loginDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
            override fun onOkClick() {
                login()
            }
        }
        accountAdapter =
            AccountAdapter(mContext, MyApplication.instance.passwordList ?: ArrayList())
        accountAdapter.setOnItemClickListener { adapter, view, position ->
            mobile = MyApplication.instance.passwordList!![position].username
            password = MyApplication.instance.passwordList!![position].password
            if (!loginDialog.isShowing) {
                loginDialog.show()
            }
        }
        toolbar.setOnOkClickListener {
            //添加账号
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        }
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.adapter = accountAdapter
    }

    private fun login() {
        if (TextUtils.isEmpty(mobile) ||
            TextUtils.isEmpty(password)
        ) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.LOGIN)
            params {
                "mobile" - mobile
                "password" - password
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: UserModel = StringNullAdapter.gson.fromJson(result)
                    MyApplication.instance.saveUserInfo(model)
                    var intent = Intent(MyApplication.instance, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    MyApplication.instance.startActivity(intent)
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

    }

    override fun clear() {

    }

}