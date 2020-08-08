package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.PHDeModel
import com.rice.tool.TextUtils

class PHDeAdapter(var context: Context, data: MutableList<PHDeModel>) :
        BaseQuickAdapter<PHDeModel, BaseViewHolder>(R.layout.item_xjsy, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: PHDeModel) {
        helper.setText(R.id.textName, bean.product_name)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textStatus, bean.type_name)
        helper.setText(R.id.textMarket, bean.profit_num)
        helper.setText(R.id.textPrice, bean.market_value + bean.product_unit)
        if (bean.show_profit_num.startsWith("-")) {
            helper.setTextColor(R.id.textPrice, mContext.resources.getColor(R.color.green24))
        } else {
            helper.setTextColor(R.id.textPrice, mContext.resources.getColor(R.color.orangefc))
        }
        helper.setGone(R.id.textTag, true)
        helper.setGone(R.id.textMarket, true)
    }

}
