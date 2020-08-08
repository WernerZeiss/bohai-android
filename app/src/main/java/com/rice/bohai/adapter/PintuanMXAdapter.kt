package com.rice.bohai.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.PintuanMxModel

class PintuanMXAdapter(var context: Context, data: MutableList<PintuanMxModel>) :
    BaseQuickAdapter<PintuanMxModel, BaseViewHolder>(R.layout.item_py_history, data) {

    override fun convert(helper: BaseViewHolder, bean: PintuanMxModel) {
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.texttitle, "拼团结果：")
        if (bean.status == "2") {
            helper.setText(R.id.textType, "成功")
        } else if (bean.status == "3") {
            helper.setText(R.id.textType, "失败")
        }
        helper.setText(R.id.textName, bean.name)
    }

}
