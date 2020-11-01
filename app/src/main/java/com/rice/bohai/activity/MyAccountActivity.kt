package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.view.View
import com.rice.base.RiceBaseActivity
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.fragment.THJEFragment
import com.rice.bohai.fragment.XSPHFragment
import com.rice.bohai.fragment.ZHSZFragment
import com.rice.bohai.fragment.ZHSZFragment.Companion.MODE_THZH
import com.rice.bohai.fragment.ZHSZFragment.Companion.MODE_XJZH
import com.rice.bohai.fragment.ZHSZFragment.Companion.MODE_XSPH
import com.rice.tool.ActivityUtils
import com.rice.tool.FragmentHelper
import kotlinx.android.synthetic.main.activity_my_account.*

@SuppressLint("Registered")
class MyAccountActivity : RiceBaseActivity() {

    init {
        isContentInvade = true
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = true
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    override fun onBackPressed() {
        finish()
    }

    var mode = MODE_XSPH
    //    var zhszFragment = ZHSZFragment.newInstance(MODE_XJZH)
    var xjzhFragment = ZHSZFragment.newInstance(MODE_XJZH)
    var xsphFragment = XSPHFragment()
    var jszhFragment = THJEFragment.newInstance()

    override fun getLayoutId(): Int {
        return R.layout.activity_my_account
    }

    override fun initView() {
        constraintXSPH.setOnClickListener {
            mode = MODE_XSPH
            initMode()
        }
        constraintJSZH.setOnClickListener {
            mode = MODE_THZH
            initMode()
        }
        textXianjin.setOnClickListener {
            mode = MODE_XJZH
            initMode()
        }
        constraintJFZH.setOnClickListener {
            ActivityUtils.openActivity(mContext, MyScoreActivity::class.java)
        }
        initMode()
    }

    fun initMode() {
        lineXJZH.visibility = View.GONE
        lineJSZH.visibility = View.GONE
        when (mode) {
            MODE_XSPH -> {
                lineXJZH.visibility = View.VISIBLE
                FragmentHelper.switchFragment(xsphFragment, this@MyAccountActivity, R.id.frameMyAccount)
            }
            MODE_XJZH -> {
                FragmentHelper.switchFragment(xjzhFragment, this@MyAccountActivity, R.id.frameMyAccount)
            }
            MODE_THZH -> {
                lineJSZH.visibility = View.VISIBLE
                FragmentHelper.switchFragment(jszhFragment, this@MyAccountActivity, R.id.frameMyAccount)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance.onUserInfoUpdateCompleteListener = object : MyApplication.OnUserInfoUpdateCompleteListener {
            override fun onUserInfoUpdateComplete() {
                initData()
            }
        }
        MyApplication.instance.getUserInfoFromWeb()
        if (mode == MODE_XSPH){
            xsphFragment.initData()
        }
    }

    /**
     * 加载账户数据
     */
    @SuppressLint("SetTextI18n")
    fun initData() {
        textXSPH.text = mContext.resources.getString(R.string.CNY) + MyApplication.instance.userInfo?.total_profit_price
        textXianjin.text = mContext.resources.getString(R.string.CNY) + MyApplication.instance.userInfo?.price
        textJiaoshou.text = mContext.resources.getString(R.string.CNY) + MyApplication.instance.userInfo?.ticket_money
        textJifen.text = MyApplication.instance.userInfo?.integral_num
    }

    override fun getIntentData() {
        mode = intent.extras?.getInt("mode", MODE_XSPH) ?: MODE_XSPH
    }

    override fun clear() {

    }

}