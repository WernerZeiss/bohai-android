package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.PriceModel
import com.rice.tool.TextUtils
import com.rice.tool.TimeUtils
import java.text.SimpleDateFormat

class PriceAdapter(var context: Context, data: MutableList<PriceModel>, var isTop: Boolean) :
    BaseQuickAdapter<PriceModel, BaseViewHolder>(R.layout.item_price, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: PriceModel) {
//        var time = bean.created_at
//        try {
//            time = SimpleDateFormat("HH:mm").format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        helper.setText(R.id.textCode, bean.created_at)
        helper.setText(R.id.textPrice, bean.price)
//        helper.setVisible(R.id.textNumber, bean.number >= 1)
        helper.setText(R.id.textNumber, bean.number.toString())
        if (isTop) {
            //上半部分价格
            helper.setTextColor(R.id.textPrice, context.resources.getColor(R.color.orangefc))
        } else {
            //下半部分价格
            helper.setTextColor(R.id.textPrice, context.resources.getColor(R.color.green24))
        }
    }

}
