package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.MessageModel
import com.rice.tool.TextUtils

class MessageAdapter(var context: Context, data: MutableList<MessageModel>, var isMessage: Boolean = false) :
        BaseQuickAdapter<MessageModel, BaseViewHolder>(R.layout.item_message, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: MessageModel) {
        helper.setText(R.id.textTitle, bean.name)
        helper.setText(R.id.textContent, bean.intro)
        helper.setText(R.id.textTime, bean.created_at)
        var textSeeNumber = helper.getView<TextView>(R.id.textSeeNumber)
        textSeeNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        if (bean.is_read > 0) {
            helper.setText(R.id.textSeeNumber, "已读")
            helper.setTextColor(R.id.textSeeNumber, context.resources.getColor(R.color.gray9))
        } else {
            helper.setText(R.id.textSeeNumber, "未读")
            helper.setTextColor(R.id.textSeeNumber, context.resources.getColor(R.color.yellowfc))
        }
    }

}
