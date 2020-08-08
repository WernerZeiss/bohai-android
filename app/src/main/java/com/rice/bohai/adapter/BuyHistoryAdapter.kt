package com.rice.bohai.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.PYHistoryModel
import com.rice.bohai.model.PintuanBuyModel

class BuyHistoryAdapter(var context: Context, data: MutableList<PintuanBuyModel>) :
    BaseQuickAdapter<PintuanBuyModel, BaseViewHolder>(R.layout.item_py_history, data) {

    override fun convert(helper: BaseViewHolder, bean: PintuanBuyModel) {
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textName, bean.name)
        helper.setText(R.id.texttitle, "交易类型：")
        helper.setText(R.id.textType, "买入")
    }

}
