package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.ProduceInfoBigAdapter
import com.rice.bohai.model.OrderDeModel
import com.rice.bohai.model.OrderModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_order_info.*


@SuppressLint("Registered")
class OrderInfoActivity : RiceBaseActivity() {

    var model: OrderModel? = null
    lateinit var produceInfoBigAdapter: ProduceInfoBigAdapter
    var list: MutableList<OrderModel.OrderInfo.ProductInfo> = ArrayList()
    var order_number = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_order_info
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.setHasFixedSize(true)
        recycler.isNestedScrollingEnabled = false
        if (model != null) {
            initModel()
        }
    }

    /**
     * 加载数据
     */
    private fun initModel() {
        if (model != null) {
            produceInfoBigAdapter = ProduceInfoBigAdapter(mContext, model!!.order_info.product_info)
            recycler.adapter = produceInfoBigAdapter
            textUserName.text =
                "${model?.order_info?.address_name}  ${model?.order_info?.address_mobile}"
            textAddress.text = model?.order_info?.address_address
            textTransportCode.text = model?.logistic_number
            textInfo.text = "订单编号：${model?.order_number}\n创建时间：${model?.created_at}"
            if (!TextUtils.isEmpty(model?.remark)){
                tv_remark.text = "用户备注："+model?.remark
            }
            if (model?.good_status == 2) {
                llWLBH.visibility = View.VISIBLE
            } else {
                llWLBH.visibility = View.GONE
            }
        }
    }

    override fun getIntentData() {
        model = intent.getSerializableExtra("model") as OrderModel?
        if (model == null) {
            order_number = intent.getStringExtra("order_number")
            initData()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.ORDER_DETAIL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "order_number" - order_number
            }
            onSuccess { byts ->
                if (::produceInfoBigAdapter.isInitialized) {
                    produceInfoBigAdapter.setEmptyView(R.layout.include_no_data)
                }
                val result = RiceHttpK.getResult(mContext,byts)
                Log.i("OrderInfoActivity->", "initData:"+result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: OrderDeModel = StringNullAdapter.gson.fromJson(result)
                    this@OrderInfoActivity.model = model.order
                    initModel()
                }
            }
            onFail { error ->
                if (::produceInfoBigAdapter.isInitialized) {
                    produceInfoBigAdapter.setEmptyView(R.layout.include_fail)
                }
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    override fun clear() {

    }

}