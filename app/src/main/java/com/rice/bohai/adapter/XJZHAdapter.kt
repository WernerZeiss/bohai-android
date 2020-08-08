package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.XJZHModel
import com.rice.tool.TextUtils

class XJZHAdapter(var context: Context, data: MutableList<XJZHModel>) :
    BaseQuickAdapter<XJZHModel, BaseViewHolder>(R.layout.item_xjzh, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: XJZHModel) {
        helper.setText(R.id.textName, bean.name)
        helper.setText(R.id.textTime, bean.time)
        helper.setText(R.id.textScore, bean.score)
        helper.setText(R.id.textStatus, bean.status)
        if (bean.score.startsWith("-")) {
            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.gray8))
        } else {
            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.orangefc))
        }
    }

}
