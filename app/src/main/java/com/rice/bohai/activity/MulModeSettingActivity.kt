package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.rice.adapter.MineFragmentAdapter
import com.rice.base.RiceBaseActivity
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.model.MineFragmnetModel
import com.rice.tool.ActivityUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_smr_recycler_match.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*

@SuppressLint("Registered")
class MulModeSettingActivity : RiceBaseActivity() {

    var mode = MODE_SECURITY

    private lateinit var mAdapter: MineFragmentAdapter
    private val mList = ArrayList<MineFragmnetModel>()
    var firstIds: ArrayList<Int> = ArrayList()//头部项ID
    var endIds: ArrayList<Int> = ArrayList()//尾部项ID

    companion object {
        const val MODE_SECURITY = 0//安全中心
        const val MODE_ZJHZ = 1//划转积分
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_smr_recycler_match_line
    }

    override fun initView() {
        refresh.setEnableRefresh(false)
        refresh.setEnableLoadMore(false)
        initMode()
    }

    fun initMode() {
        when (mode) {
            MODE_SECURITY -> {
                toolbar.setTitle("安全中心")
                val stringArray =
                    mContext.resources.getStringArray(R.array.security_setting)//设置项名称数组
                for (i in stringArray.indices) {
                    val model = MineFragmnetModel()
                    model.title = stringArray[i]
//            model.resId = iconArray[i]
                    if (firstIds.indexOf(stringArray.indices.indexOf(i)) >= 0) {
                        //头部添加空白区域的项，头部
                        model.location = MineFragmnetModel.FIRST
                    } else if (endIds.indexOf(stringArray.indices.indexOf(i)) >= 0) {
                        //不要分割线的项，尾部
                        model.location = MineFragmnetModel.END
                    }
                    mList.add(model)
                }
                if (MyApplication.instance.userInfo?.is_transaction_pass ?: 0 > 0) {
                    for (item in mList) {
                        if (item.title == "设置交易密码") {
                            item.title = "修改交易密码"
                        }
                    }
                }
                mAdapter =
                    MineFragmentAdapter(mList, com.rice.riceframe.R.layout.item_minefragment_big)
                mAdapter.setOnItemClickListener { adapter, view, position ->
                    when (position) {
//                        0 -> {
//                            //设置交易密码
//                            var b = Bundle()
//                            if (MyApplication.instance.userInfo?.is_transaction_pass ?: 0 > 0) {
//                                b.putInt("mode", EditPasswordActivity.MODE_FORGET_PY_PASSWORD)
//                            } else {
//                                b.putInt("mode", EditPasswordActivity.MODE_SET_PY_PASSWORD)
//                            }
//                            ActivityUtils.openActivity(mContext, EditPasswordActivity::class.java, b)
//                        }
                        0 -> {
                            //修改登录密码
                            var b = Bundle()
                            b.putInt("mode", EditPasswordActivity.MODE_MODIFY_LOGIN_PASSWORD)
                            ActivityUtils.openActivity(
                                mContext,
                                EditPasswordActivity::class.java,
                                b
                            )
                        }
                    }
                }
            }
            MODE_ZJHZ -> {
                toolbar.setTitle("划转积分")
                val stringArray =
                    mContext.resources.getStringArray(R.array.hzjf_setting)//设置项名称数组
                for (i in stringArray.indices) {
                    val model = MineFragmnetModel()
                    model.title = stringArray[i]
//            model.resId = iconArray[i]
                    if (firstIds.indexOf(stringArray.indices.indexOf(i)) >= 0) {
                        //头部添加空白区域的项，头部
                        model.location = MineFragmnetModel.FIRST
                    } else if (endIds.indexOf(stringArray.indices.indexOf(i)) >= 0) {
                        //不要分割线的项，尾部
                        model.location = MineFragmnetModel.END
                    }
                    mList.add(model)
                }
                mAdapter =
                    MineFragmentAdapter(mList, com.rice.riceframe.R.layout.item_minefragment_big)
                mAdapter.setOnItemClickListener { adapter, view, position ->
                    when (position) {
                        0 -> {
                            //现金转积分
                            var b = Bundle()
                            b.putInt("mode", XJ2JFActivity.MODE_XJ2JF)
                            ActivityUtils.openActivity(mContext, XJ2JFActivity::class.java, b)
                        }
                        1 -> {
                            //订单转积分
                            var b = Bundle()
                            b.putInt("mode", XJ2JFActivity.MODE_DD2JF)
                            ActivityUtils.openActivity(mContext, XJ2JFActivity::class.java, b)
                        }
                    }
                }
            }
        }
        initData()
    }

    fun initData() {
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.adapter = mAdapter
        recycler.setHasFixedSize(true)
        recycler.isNestedScrollingEnabled = false
    }

    override fun getIntentData() {
        mode = intent?.extras?.getInt("mode", MODE_SECURITY) ?: MODE_SECURITY
    }

    override fun clear() {

    }

}