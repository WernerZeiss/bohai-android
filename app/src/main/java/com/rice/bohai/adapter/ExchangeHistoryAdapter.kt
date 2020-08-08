package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.ExchangeHistoryModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils

class ExchangeHistoryAdapter(var context: Context, data: MutableList<ExchangeHistoryModel>) :
        BaseQuickAdapter<ExchangeHistoryModel, BaseViewHolder>(R.layout.item_exchange_history, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: ExchangeHistoryModel) {
        helper.setText(R.id.textName, bean.product_name)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textPrice, bean.integral_num + "积分")
        var img = helper.getView<ImageView>(R.id.img)
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.image), img)
    }

}
