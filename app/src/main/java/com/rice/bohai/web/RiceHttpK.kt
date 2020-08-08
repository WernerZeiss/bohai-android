package com.rice.racar.web

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.orhanobut.logger.Logger
import com.rice.bohai.MyApplication
import com.rice.bohai.activity.LoginActivity
import com.rice.bohai.activity.RechargeActivity
import com.rice.dialog.OkCancelDialog
import com.rice.tool.ActivityUtils
import com.rice.tool.ToastUtil
import java.nio.charset.Charset


/**
 * 网络请求二次封装
 * 使用kolley框架
 */
class RiceHttpK {

    companion object {

        var base_url = "" //服务器地址
        var token = "" //用户token
        const val TAG = "---RiceHttpK---"
        const val SUCCESS = 0 //操作成功的状态码
        const val FAIL = 1 //请求成功但操作失败的状态码
        const val NO_TOKEN = 20 //请求成功但Token失效的状态码
        const val show_me_the_money = 50 //余额不足的状态码

        /**
         * 在Application初始化时调用，设置服务器地址
         */
        fun init(base: String) {
            if (TextUtils.isEmpty(base)) {
                Log.e(TAG, "警告，服务器地址为空")
            }
            base_url = base
        }

        /**
         * 拼接URL
         */
        fun getUrl(api: String): String {
            if (TextUtils.isEmpty(api)) {
                Log.e(TAG, "警告，api地址为空")
            }
            return base_url + api
        }

        /**
         * 拼接URL和token
         */
        fun getUrlWithToken(api: String): String {
            if (TextUtils.isEmpty(api)) {
                Log.e(TAG, "警告，api地址为空")
            }
            if (TextUtils.isEmpty(token)) {
                Log.e(TAG, "警告，token为空")
            }
            return "$base_url$api?token=$token"
        }

        /**
         * 获取服务器返回的结果
         */
        fun getResult(mContext: Context, bytes: ByteArray): String {
            var data = bytes.toString(Charset.defaultCharset())
            var status = PublicModel.forjson(data)
            Logger.i("hel->${data}")
//            Logger.d(status.message)
//            Logger.json(status.data)
            when (status.code) { //根据状态码判断结果
                SUCCESS -> { //成功
                    //var model: Model = StringNullAdapter.gson.fromJson(status.data)
                    return status.data
                }
                FAIL -> { //失败
                    ToastUtil.showShort(status.message)
                    return ""
                }
                NO_TOKEN -> { //token掉了
                    ToastUtil.showShort("请先登录")
                    MyApplication.instance.clear()
                    var intent = Intent(MyApplication.instance, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    var b = Bundle()
                    b.putBoolean("toMain", true)
                    intent.putExtras(b)
                    MyApplication.instance.startActivity(intent)
                    return ""
                }
                show_me_the_money -> {
                    //余额不足
                    if (MyApplication.instance.userInfo?.is_start_recharge == 1) {
                        val notEnoughMoneyDialog = OkCancelDialog(mContext)
                        notEnoughMoneyDialog.setOkText("前往充值")
                        notEnoughMoneyDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                            override fun onOkClick() {
                                ActivityUtils.openActivity(mContext, RechargeActivity::class.java)
                            }
                        }
                        notEnoughMoneyDialog.setInfo(status.message)
                        notEnoughMoneyDialog.show()
                    } else {
                        ToastUtil.showShort(status.message)
                    }
                    return ""
                }
                else -> {
                    return ""
                }
            }
        }

    }

}
