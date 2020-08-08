package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.WDCCModel
import com.rice.tool.TextUtils

class ZHSZAdapter(var context: Context, data: MutableList<WDCCModel>) :
        BaseQuickAdapter<WDCCModel, BaseViewHolder>(R.layout.item_xjsy, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: WDCCModel) {
        helper.setText(R.id.textName, bean.product_name)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textStatus, bean.status_name)
        helper.setText(R.id.textPrice, bean.market_value)
    }

}
