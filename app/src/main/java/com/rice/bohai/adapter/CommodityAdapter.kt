package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.CommodityModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils

class CommodityAdapter(var context: Context, data: MutableList<CommodityModel>) :
        BaseQuickAdapter<CommodityModel, BaseViewHolder>(R.layout.item_commodity_jf, data) {

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
        var img = helper.getView<ImageView>(R.id.img)
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.image), mContext.resources.getDimensionPixelOffset(R.dimen.dp_8), img)
        var lp = img.layoutParams as ConstraintLayout.LayoutParams
        if (data.indexOf(bean) % 2 == 0) {
            lp.setMargins(context.resources.getDimensionPixelOffset(R.dimen.dp_16), 0, context.resources.getDimensionPixelOffset(R.dimen.dp_8), 0)
        } else {
            lp.setMargins(context.resources.getDimensionPixelOffset(R.dimen.dp_8), 0, context.resources.getDimensionPixelOffset(R.dimen.dp_16), 0)
        }
        img.layoutParams = lp
        helper.addOnClickListener(R.id.img)
        helper.addOnClickListener(R.id.textBtnExchange)
    }

}
