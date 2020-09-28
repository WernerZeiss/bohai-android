package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.OrderModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils

class ProduceInfoBigAdapter(var context: Context, data: MutableList<OrderModel.OrderInfo.ProductInfo>) :
        BaseQuickAdapter<OrderModel.OrderInfo.ProductInfo, BaseViewHolder>(R.layout.item_product_info_big, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: OrderModel.OrderInfo.ProductInfo) {
        helper.setText(R.id.textName, bean.product_name)
        helper.setText(R.id.textCode, bean.number.toString() + bean.unit)
        var price = mContext.resources.getString(R.string.CNY) + bean.price
//        if (bean.price == "0.00") {
//            price = bean.integral + "积分"
//        } else if (bean.integral == "0" || bean.integral == "0.00") {
//            price = mContext.resources.getString(R.string.CNY) + bean.price
//        } else {
//            price = mContext.resources.getString(R.string.CNY) + bean.price + "+" + bean.integral + "积分"
//        }
        helper.setText(R.id.textPrice, price)
        var img = helper.getView<ImageView>(R.id.img)
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.image),
                mContext.resources.getDimensionPixelOffset(R.dimen.dp_6), img)
    }


}
