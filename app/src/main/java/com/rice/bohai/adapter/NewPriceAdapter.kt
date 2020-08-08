package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.NewPriceModel
import com.rice.tool.TextUtils
import com.rice.tool.TimeUtils
import java.text.SimpleDateFormat

class NewPriceAdapter(var context: Context, data: MutableList<NewPriceModel>, var isTop: Boolean) :
    BaseQuickAdapter<NewPriceModel, BaseViewHolder>(R.layout.item_price, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: NewPriceModel) {
//        var time = bean.start_at
//        try {
//            time = SimpleDateFormat("HH:mm").format(SimpleDateFormat("HH:mm:ss").parse(time))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        helper.setText(R.id.textCode, bean.show_time)
        helper.setText(R.id.textPrice, bean.product_price)
        helper.setText(R.id.textNumber, bean.surplus_num)
        if (isTop) {
            //上半部分价格
            helper.setTextColor(R.id.textPrice, context.resources.getColor(R.color.orangefc))
        } else {
            //下半部分价格
            helper.setTextColor(R.id.textPrice, context.resources.getColor(R.color.green24))
        }
    }

}
