package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.THJEModel

class THJEAdapter(var context: Context, data: MutableList<THJEModel>) :
    BaseQuickAdapter<THJEModel, BaseViewHolder>(R.layout.item_jfsy, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: THJEModel) {
        helper.setText(R.id.textName, bean.name)
        helper.setText(R.id.textTime, bean.created_at)
        if (bean.type == 3){
            helper.setText(R.id.textPrice, "+"+bean.money)
            helper.setText(R.id.textExchange, "转让配货券")
        }else if (bean.type == 4){
            helper.setText(R.id.textPrice, "-"+bean.money)
            helper.setText(R.id.textExchange, "释放储备券")
        }
    }

}
