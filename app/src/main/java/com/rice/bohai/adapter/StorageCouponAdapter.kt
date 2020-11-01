package com.rice.bohai.adapter

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.StorageCouponModel

/**
 * @author CWQ
 * @date 2020/10/27
 */
class StorageCouponAdapter(data: MutableList<StorageCouponModel>) :
    BaseQuickAdapter<StorageCouponModel, BaseViewHolder>(
        R.layout.item_adapter_storagecoupon,
        data
    ) {
    override fun convert(helper: BaseViewHolder, item: StorageCouponModel) {
        helper.setText(R.id.tv_item_name, "交易类型:" + item.type_name)
        helper.setText(R.id.tv_item_date, item.created_at)
        val pricePre = if (item.add_type == 1) "+" else "-"
        helper.setText(R.id.tv_item_price, pricePre + item.price)
        val priceColor =
            if (item.add_type == 1) Color.parseColor("#ED4E11") else Color.parseColor("#2DCC23")
        helper.setTextColor(R.id.tv_item_price, priceColor)
    }
}