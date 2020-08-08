package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.NotifyModel
import com.rice.tool.TextUtils

class NotifyAdapter(var context: Context, data: MutableList<NotifyModel>) :
        BaseQuickAdapter<NotifyModel, BaseViewHolder>(R.layout.item_message, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: NotifyModel) {
        helper.setText(R.id.textTitle, bean.name)
        helper.setText(R.id.textContent, bean.intro)
        helper.setText(R.id.textTime, bean.created_at)
        var textSeeNumber = helper.getView<TextView>(R.id.textSeeNumber)
        textSeeNumber.setCompoundDrawablesWithIntrinsicBounds(context.resources.getDrawable(R.drawable.icon_see), null, null, null)
        helper.setText(R.id.textSeeNumber, bean.view_num)
        helper.setTextColor(R.id.textSeeNumber, context.resources.getColor(R.color.gray9))
        helper.setVisible(R.id.textSeeNumber, false)
    }

}
