package com.rice.bohai.activity

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.StorageCouponAdapter
import com.rice.bohai.model.CouponModel
import com.rice.bohai.model.StorageCouponModel
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_availablebuycoupon.*
import java.nio.charset.Charset

/**
 * @author CWQ
 * @date 2020/10/28
 * 待领取购货券列表
 */
class AvailableBuyCouponActivity : RiceBaseActivity() {

    lateinit var loadingDialog: RLoadingDialog
    private var mList: MutableList<StorageCouponModel> = ArrayList()
    private var mAdapter: StorageCouponAdapter? = null
    private var page = 1
    private var allMoney = "0.00"

    override fun getLayoutId(): Int {
        return R.layout.activity_availablebuycoupon
    }

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        srl_available.setOnRefreshListener {
            page = 1
            getAvailableData()
        }
        srl_available.setOnLoadMoreListener {
            page++
            getAvailableData()
        }

        tv_get.setOnClickListener {
            if (TextUtils.isNotEmpty(allMoney) && allMoney.toDouble() > 0) {
                getGHQ()
            } else {
                ToastUtil.showShort("暂无可领取购货券")
            }
        }

        getAvailableData()
    }

    override fun getIntentData() {
    }

    override fun clear() {
    }

    //领取个人购货券
    private fun getGHQ() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_GROUP_BUY_NOW)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onStart {
                loadingDialog.show()
            }
            onSuccess { byts ->
                val data = byts.toString(Charset.defaultCharset())
                Log.i("getGHQ->", data)
                val status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    getAvailableData()
                }
            }
            onFail {
                ToastUtil.showShort("领取失败，稍后重试")
            }
            onFinish {
                loadingDialog.hide()
            }
        }
    }

    /**
     * 获取待领取明细
     */
    private fun getAvailableData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_AVAILABLE_GHQ_LOG)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                Log.i("getAvailableData->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: CouponModel = StringNullAdapter.gson.fromJson(result)
                    allMoney = model.need_wallet_money
                    tv_available_money.text = "合计领取：$allMoney"
                    if (page == 1) {
                        mList.clear()
                    }
                    mList.addAll(model.lists)

                    if (mAdapter == null) {
                        mAdapter = StorageCouponAdapter(mList)
                        rv_available_coupon.layoutManager = LinearLayoutManager(mContext)
                        rv_available_coupon.adapter = mAdapter
                    } else {
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
            onFail {

            }
            onFinish {
                srl_available.finishRefresh()
                srl_available.finishLoadMore()
            }
        }
    }
}