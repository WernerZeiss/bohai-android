package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.util.Log
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
import com.rice.bohai.adapter.TimeLineAdapter
import com.rice.bohai.model.OrderDeModel
import com.rice.bohai.model.OrderModel
import com.rice.bohai.model.TranslateModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_order_translate.*

@SuppressLint("Registered")
class OrderTranslateActivity : RiceBaseActivity() {

    var list: MutableList<TranslateModel> = ArrayList()
    lateinit var timeLineAdapter: TimeLineAdapter
    var id = ""
    lateinit var produceInfoBigAdapter: ProduceInfoBigAdapter
    var listProduce: MutableList<OrderModel.OrderInfo.ProductInfo> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.activity_order_translate
    }

    override fun initView() {
        recyclerInfo.layoutManager = LinearLayoutManager(mContext)
        recyclerInfo.setHasFixedSize(true)
        recyclerInfo.isNestedScrollingEnabled = false
        produceInfoBigAdapter = ProduceInfoBigAdapter(mContext, listProduce)
        recyclerInfo.adapter = produceInfoBigAdapter

        recycler.layoutManager = LinearLayoutManager(mContext)
        timeLineAdapter = TimeLineAdapter(mContext, list)
        timeLineAdapter.bindToRecyclerView(recycler)
        timeLineAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = timeLineAdapter
        initData()
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.ORDER_DETAIL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "order_number" - id
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                timeLineAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: OrderDeModel = StringNullAdapter.gson.fromJson(result)
                    list.clear()
                    list.addAll(model.order.shipper_result)
                    timeLineAdapter.notifyDataSetChanged()

                    listProduce.clear()
                    listProduce.addAll(model.order.order_info.product_info)
                    produceInfoBigAdapter.notifyDataSetChanged()

                    textTransportCode.text = model.order.logistic_number
                    textInfo.text = "订单编号：${model.order.order_number}\n创建时间：${model.order.created_at}"

                    textTransportCode.text = "物流编号：${model.order.logistic_number}"
                    textTransportCompany.text = "物流公司：${model.order.shipper_name}"
                }
            }
            onFail { error ->
                timeLineAdapter.setEmptyView(R.layout.include_fail)
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
        id = intent.extras?.getString("id", "") ?: ""
    }

    override fun clear() {

    }

}