package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.RechargeHistoryModel

class RechargeHistoryAdapter(var context: Context, data: MutableList<RechargeHistoryModel>) :
    BaseQuickAdapter<RechargeHistoryModel, BaseViewHolder>(R.layout.item_jf_history, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: RechargeHistoryModel) {
        helper.setText(R.id.textName, "充值方式：${bean.type_name}")
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textScore, bean.price)
        helper.setText(R.id.textStatus, bean.status)
//        if (bean.show_integral.startsWith("-")) {
//            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.gray8))
//        } else {
//            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.orangefc))
//        }
    }

}
