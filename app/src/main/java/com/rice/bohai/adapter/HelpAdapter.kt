package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.HelpModel

class HelpAdapter(var context: Context, data: MutableList<HelpModel>) :
        BaseQuickAdapter<HelpModel, BaseViewHolder>(R.layout.item_message, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: HelpModel) {
        helper.setText(R.id.textTitle, bean.title)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setVisible(R.id.textSeeNumber, false)
        helper.setGone(R.id.textContent, false)
    }

}
