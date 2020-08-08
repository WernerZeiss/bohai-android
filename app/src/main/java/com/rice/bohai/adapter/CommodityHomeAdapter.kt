package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.CommodityModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils

class CommodityHomeAdapter(var context: Context, data: MutableList<CommodityModel>) :
        BaseQuickAdapter<CommodityModel, BaseViewHolder>(R.layout.item_commodity_home, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: CommodityModel) {
        helper.setText(R.id.textName, bean.name)
        helper.setText(R.id.textCode, "库存：${bean.surplus_num}")
        var price = ""
        if (bean.price == "0.00") {
            price = "${bean.integral}积分"
        } else if (bean.integral == "0") {
            price = "${mContext.resources.getString(R.string.CNY)}${bean.price}"
        } else {
            price = "${mContext.resources.getString(R.string.CNY)}${bean.price}+${bean.integral}积分"
        }
        helper.setText(R.id.textPrice, price)
        helper.setText(R.id.textCompanyname, bean.company_name)
        helper.setText(R.id.textTransactionNum, "兑换量：" + bean.already_exchange_num.toString())
        helper.setVisible(R.id.textTransactionNum, false)
        var img = helper.getView<ImageView>(R.id.img)
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.image), mContext.resources.getDimensionPixelOffset(R.dimen.dp_8), img)
//
//        var view5 = helper.getView<View>(R.id.view5)
//        var lp = view5.layoutParams as ConstraintLayout.LayoutParams
        helper.setGone(R.id.space, data.indexOf(bean) == data.lastIndex)
//        if (data.indexOf(bean) == data.lastIndex) {
//            lp.topMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_72)
//            view5.layoutParams = lp
//        } else {
//            lp.topMargin = mContext.resources.getDimensionPixelOffset(R.dimen.dp_16)
//            view5.layoutParams = lp
//        }
    }

}
