package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.activity.OrderInfoActivity
import com.rice.bohai.activity.OrderTranslateActivity
import com.rice.bohai.model.OrderModel
import com.rice.tool.ActivityUtils

class MyOrderAdapter(var context: Context, data: MutableList<OrderModel>) :
        BaseQuickAdapter<OrderModel, BaseViewHolder>(R.layout.item_my_order, data) {

    var onCancelClickListner: OnCancelClickListener? = null
    var onSureReceiveListener: OnSureReceiveListener? = null

    interface OnCancelClickListener {
        fun onCancelClick(orderNumber: String)
    }

    interface OnSureReceiveListener {
        fun onSureReceive(orderNumber: String)
    }

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: OrderModel) {
        helper.setText(R.id.textOrderId, "订单编号：${bean.order_number}")
        helper.setText(R.id.textStatus, bean.status_name)
        var btn1 = helper.getView<TextView>(R.id.textBtn1)
        var btn2 = helper.getView<TextView>(R.id.textBtn2)
        var btn3 = helper.getView<TextView>(R.id.textBtn3)
        when (bean.status) {
            1 -> {
                //待支付
                btn1.text = "取消订单"
                btn1.visibility = View.VISIBLE
                btn2.text = "立即支付"
                btn2.visibility = View.VISIBLE
                btn3.text = "订单信息"
                btn3.visibility = View.VISIBLE
                btn1.setOnClickListener {
                    //取消订单
                    onCancelClickListner?.onCancelClick(bean.order_number)
                }
                btn3.setOnClickListener {
                    //跳转订单详情
                    var b = Bundle()
                    b.putSerializable("model", bean)
                    ActivityUtils.openActivity(mContext, OrderInfoActivity::class.java, b)
                }
            }
            2 -> {
                //已支付
                when (bean.good_status) {
                    1 -> {
                        //待发货
                        btn1.visibility = View.GONE
                        //                btn2.text = "联系客服"
                        btn2.visibility = View.GONE
                        btn3.text = "订单信息"
                        btn3.visibility = View.VISIBLE
                        btn3.setOnClickListener {
                            //跳转订单详情
                            var b = Bundle()
                            b.putSerializable("model", bean)
                            ActivityUtils.openActivity(mContext, OrderInfoActivity::class.java, b)
                        }
                    }
                    2 -> {
                        //待收货
                        btn1.visibility = View.GONE
                        btn2.text = "确认收货"
                        btn2.visibility = View.VISIBLE
                        btn3.text = "查看物流"
                        btn3.visibility = View.VISIBLE
                        btn2.setOnClickListener {
                            onSureReceiveListener?.onSureReceive(bean.order_number)
                        }
                        btn3.setOnClickListener {
                            var b = Bundle()
                            b.putString("id", bean.order_number)
                            ActivityUtils.openActivity(context, OrderTranslateActivity::class.java, b)
                        }
                    }
                    3 -> {
                        //已完成
                        btn1.visibility = View.GONE
                        btn2.visibility = View.GONE
                        btn3.text = "订单信息"
                        btn3.visibility = View.VISIBLE
                        btn3.setOnClickListener {
                            //跳转订单详情
                            var b = Bundle()
                            b.putSerializable("model", bean)
                            ActivityUtils.openActivity(mContext, OrderInfoActivity::class.java, b)
                        }
                    }
                    9 -> {
                        //已完成(自提)
                        btn1.visibility = View.GONE
                        btn2.visibility = View.GONE
                        btn3.visibility = View.GONE
                    }
                }
            }
        }
        var recycler = helper.getView<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(mContext)
        var mAdapter = ProduceInfoAdapter(mContext, bean.order_info.product_info)
        recycler.adapter = mAdapter
    }


}
