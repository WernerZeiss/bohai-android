package com.rice.bohai.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
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
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_storagebuycoupon.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author CWQ
 * @date 2020/10/26
 */
class StorageBuyCouponActivity : RiceBaseActivity(), View.OnClickListener {

    //0仓储券 1购货券
    private var dataType = 0
    lateinit var loadingDialog: RLoadingDialog
    private var mList: MutableList<StorageCouponModel> = ArrayList()
    private var mAdapter: StorageCouponAdapter? = null
    private var page = 1
    private var mStartDate: Date? = null
    private var mEndDate: Date? = null
    private var mStartTime = ""
    private var mEndTime = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_storagebuycoupon
    }

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        if (dataType == 0) {
            toolbarXSPH.setTitle("仓储券")
            cl_header.setBackgroundResource(R.drawable.bg_ccq)
            tv_ccq_flag.visibility = View.VISIBLE
            ll_gwq_container.visibility = View.GONE
        } else {
            toolbarXSPH.setTitle("购货券")
            cl_header.setBackgroundResource(R.drawable.bg_ghq)
            tv_ccq_flag.visibility = View.GONE
            ll_gwq_container.visibility = View.VISIBLE
        }


        tv_rule.setOnClickListener(this)
        tv_start_time.setOnClickListener(this)
        tv_end_time.setOnClickListener(this)
        tv_clear.setOnClickListener(this)
        tv_search.setOnClickListener(this)
        tv_gwq_wait_get.setOnClickListener(this)
        tv_gwq_get_goods.setOnClickListener(this)
        srl.setOnRefreshListener {
            page = 1
            initData()
        }
        srl.setOnLoadMoreListener {
            page++
            initData()
        }
    }


    override fun getIntentData() {
        dataType = intent.getIntExtra("dataType", 0)
    }

    override fun clear() {
    }


    override fun onStart() {
        super.onStart()
        initData()
    }


    private fun initData() {
        if (dataType == 0) {
            getStorageCouponData()
        } else {
            getBuyCouponData()
        }
    }

    //获取仓储券明细
    private fun getStorageCouponData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_STORAGE_TICKET_LOG)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
                if (TextUtils.isNotEmpty(mStartTime)) {
                    "start_time" - mStartTime
                }
                if (TextUtils.isNotEmpty(mEndTime)) {
                    "end_time" - mEndTime
                }
            }
            onStart {
                loadingDialog.show()
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                Log.i("getStorageCouponData->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: CouponModel = StringNullAdapter.gson.fromJson(result)
                    tv_storage_money.text = model.storage_ticket
                    if (page == 1) {
                        mList.clear()
                    }
                    mList.addAll(model.lists)

                    if (mAdapter == null) {
                        mAdapter = StorageCouponAdapter(mList)
                        rv_storage.layoutManager = LinearLayoutManager(mContext)
                        rv_storage.adapter = mAdapter
                    } else {
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
            onFail {

            }
            onFinish {
                loadingDialog.hide()
                srl.finishRefresh()
                srl.finishLoadMore()
            }
        }
    }

    //获取购货券明细
    private fun getBuyCouponData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_GHQ_LOG)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
                if (TextUtils.isNotEmpty(mStartTime)) {
                    "start_time" - mStartTime
                }
                if (TextUtils.isNotEmpty(mEndTime)) {
                    "end_time" - mEndTime
                }
            }
            onStart {
                loadingDialog.show()
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                Log.i("getBuyCouponData->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: CouponModel = StringNullAdapter.gson.fromJson(result)
                    tv_storage_money.text = "现有购货券:" + model.group_wallet_money
                    tv_wait_get_money.text = model.group_need_wallet_money

                    if (page == 1) {
                        mList.clear()
                    }
                    mList.addAll(model.lists)

                    if (mAdapter == null) {
                        mAdapter = StorageCouponAdapter(mList)
                        rv_storage.layoutManager = LinearLayoutManager(mContext)
                        rv_storage.adapter = mAdapter
                    } else {
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
            onFail {

            }
            onFinish {
                loadingDialog.hide()
                srl.finishRefresh()
                srl.finishLoadMore()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_rule -> ActivityUtils.openActivity(mContext, ChubeiRuleActivity::class.java,
                Bundle().apply {
                    putInt("ruleType", dataType)
                }
            )
            R.id.tv_start_time -> showTimerPicker(1)
            R.id.tv_end_time -> showTimerPicker(2)
            R.id.tv_clear -> {
                mStartTime = ""
                mEndTime = ""
                tv_start_time.text = "开始时间"
                tv_end_time.text = "结束时间"
            }
            R.id.tv_search -> initData()
            R.id.tv_gwq_wait_get -> {
                //待领取
                ActivityUtils.openActivity(this, AvailableBuyCouponActivity::class.java)
            }
            R.id.tv_gwq_get_goods -> {
                //提货
                ActivityUtils.openActivity(mContext, TakeGoodsActivity::class.java)
            }
        }
    }


    /**
     * 显示日期选择器
     * @param type 1开始时间 2结束时间
     */
    private fun showTimerPicker(type: Int) {
        val title = "请选择日期"
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            if (type == 1) {
                if (mEndDate != null) {
                    if (date.time >= mEndDate!!.time) {
                        ToastUtil.showShort("开始时间必须小于结束时间")
                    } else {
                        mStartDate = date
                        mStartTime = SimpleDateFormat("yyyy-MM-dd").format(date)
                        tv_start_time.text = mStartTime
                    }
                } else {
                    mStartDate = date
                    mStartTime = SimpleDateFormat("yyyy-MM-dd").format(date)
                    tv_start_time.text = mStartTime
                }
            } else {
                if (mStartDate != null) {
                    if (date.time <= mStartDate!!.time) {
                        ToastUtil.showShort("结束时间必须大于开始时间")
                    } else {
                        mEndDate = date
                        mEndTime = SimpleDateFormat("yyyy-MM-dd").format(date)
                        tv_end_time.text = mEndTime
                    }
                } else {
                    mEndDate = date
                    mEndTime = SimpleDateFormat("yyyy-MM-dd").format(date)
                    tv_end_time.text = mEndTime
                }
            }
            page = 1
        })
            .setTitleText(title)
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .build().show()
    }
}