package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.CommodityModel
import com.rice.tool.TextUtils

class SearchAdapter(var context: Context, data: MutableList<CommodityModel>) :
    BaseQuickAdapter<CommodityModel, BaseViewHolder>(R.layout.item_search, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: CommodityModel) {
        helper.setText(R.id.textTitle, bean.name)
//        helper.setText(R.id.textCode, bean.no_number)
        helper.setText(R.id.textPrice, mContext.resources.getString(R.string.CNY) + bean.price)
//        helper.setText(R.id.textPerDay, bean.day_trade_volume)
    }

}
