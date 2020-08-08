package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.XieyiModel

class XieyiAdapter(var context: Context, data: MutableList<XieyiModel>) :
        BaseQuickAdapter<XieyiModel, BaseViewHolder>(R.layout.item_my_xieyi, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: XieyiModel) {
        helper.setText(R.id.textTitle, bean.show_name)
        helper.setText(R.id.textTime, "签署时间：${bean.created_at}")

        helper.addOnClickListener(R.id.textBtnSee)
        helper.addOnClickListener(R.id.textBtnDownload)
    }

}
