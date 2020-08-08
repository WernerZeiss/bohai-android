package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.IntegralHistoryModel
import com.rice.tool.TextUtils

class IntegralHistoryAdapter(var context: Context, data: MutableList<IntegralHistoryModel>) :
    BaseQuickAdapter<IntegralHistoryModel, BaseViewHolder>(R.layout.item_jf_history, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: IntegralHistoryModel) {
        helper.setText(R.id.textName, bean.show_name)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textScore, bean.show_integral)
        helper.setText(R.id.textStatus, bean.type_name)
        if (bean.show_integral.startsWith("-")) {
            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.gray8))
        } else {
            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.orangefc))
        }
    }

}
