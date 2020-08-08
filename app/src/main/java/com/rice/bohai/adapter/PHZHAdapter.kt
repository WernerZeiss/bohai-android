package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.ProfitHistoryModel
import com.rice.tool.TextUtils

class PHZHAdapter(var context: Context, data: MutableList<ProfitHistoryModel>) :
        BaseQuickAdapter<ProfitHistoryModel, BaseViewHolder>(R.layout.item_xjsy, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: ProfitHistoryModel) {
        helper.setText(R.id.textName, bean.product_name)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textStatus, bean.status_name)
        helper.setText(R.id.textPrice, bean.price)
    }

}
