package com.rice.bohai.fragment

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.MyOrderAdapter
import com.rice.bohai.model.OrderListModel
import com.rice.bohai.model.OrderModel
import com.rice.dialog.OkCancelDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.nio.charset.Charset

class MyOrderFragment : BaseImmersionFragment() {

    lateinit var myOrderAdapter: MyOrderAdapter
    var list: MutableList<OrderModel> = ArrayList()
    var status = 0
    var page = 1
    lateinit var okCancelDialog: OkCancelDialog

    init {
        navigationBarColorId = R.color.white
        statusBarColorId = R.color.white
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = false
    }

    companion object {
        fun newInstance(status: Int): MyOrderFragment {
            val args = Bundle()
            args.putInt("status", status)
            val fragment = MyOrderFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_my_order

    override fun initView() {
        status = arguments?.getInt("status", 0) ?: 0
        okCancelDialog = OkCancelDialog(mContext)
        recycler.layoutManager = LinearLayoutManager(mContext)
        myOrderAdapter = MyOrderAdapter(mContext, list)
        myOrderAdapter.onCancelClickListner = object : MyOrderAdapter.OnCancelClickListener {
            override fun onCancelClick(orderNumber: String) {
                okCancelDialog.setInfo("是否确定取消订单？")
                okCancelDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                    override fun onOkClick() {
                        cancel(orderNumber)
                    }
                }
                if (!okCancelDialog.isShowing) {
                    okCancelDialog.show()
                }
            }
        }
        myOrderAdapter.onSureReceiveListener = object : MyOrderAdapter.OnSureReceiveListener {
            override fun onSureReceive(orderNumber: String) {
                okCancelDialog.setInfo("确认已收到货物？")
                okCancelDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                    override fun onOkClick() {
                        sure(orderNumber)
                    }
                }
                if (!okCancelDialog.isShowing) {
                    okCancelDialog.show()
                }
            }
        }
        myOrderAdapter.bindToRecyclerView(recycler)
        myOrderAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = myOrderAdapter
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        initData()
    }

    /**
     * 取消订单
     */
    private fun cancel(orderNumber: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.CANCEL_INTEGRAL_ORDER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "order_number" - orderNumber
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initData()
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
     * 确认收货
     */
    private fun sure(orderNumber: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.SURE_ORDER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "order_number" - orderNumber
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    initData()
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
     * 订单列表
     */
    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.ORDER_LIST)
            params {
                "page" - page.toString()
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "type" - status.toString()
            }
            onFinish {
                if (isResumed) {
                    refresh.finishRefresh()
                    refresh.finishLoadMore()
                }
            }
            onSuccess { byts ->
                myOrderAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: OrderListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        list.clear()
                    }
                    list.addAll(model.lists)
                    myOrderAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                myOrderAdapter.setEmptyView(R.layout.include_fail)
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