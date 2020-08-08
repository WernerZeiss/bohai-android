package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.NotifyModel
import com.rice.tool.TextUtils
import com.rice.tool.TimeUtils
import java.text.SimpleDateFormat

class NotifyHomeAdapter(var context: Context, data: MutableList<NotifyModel>) :
        BaseQuickAdapter<NotifyModel, BaseViewHolder>(R.layout.item_message_home, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: NotifyModel) {
        helper.setText(R.id.text, bean.name)
        var time = TimeUtils.getDistance(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), bean.updated_at, false)
        helper.setText(R.id.time, time)
    }

}
