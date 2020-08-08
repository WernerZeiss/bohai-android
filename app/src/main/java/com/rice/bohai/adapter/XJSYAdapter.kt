package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.XJSYModel

class XJSYAdapter(var context: Context, data: MutableList<XJSYModel>) :
        BaseQuickAdapter<XJSYModel, BaseViewHolder>(R.layout.item_xjsy, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: XJSYModel) {
        helper.setText(R.id.textName, bean.name)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textStatus, bean.type_name)
        helper.setText(R.id.textPrice, bean.show_price)
        if (bean.show_price.startsWith("-")) {
            helper.setTextColor(R.id.textPrice, mContext.resources.getColor(R.color.green24))
        } else {
            helper.setTextColor(R.id.textPrice, mContext.resources.getColor(R.color.orangefc))
        }
    }

}
