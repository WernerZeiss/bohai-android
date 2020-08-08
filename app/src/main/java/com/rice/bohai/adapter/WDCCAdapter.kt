package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.activity.WDCCActivity
import com.rice.bohai.model.WDCCModel
import com.rice.tool.TextUtils

class WDCCAdapter(var context: Context, data: MutableList<WDCCModel>, var mode: Int = WDCCActivity.MODE_NORMOL) :
        BaseQuickAdapter<WDCCModel, BaseViewHolder>(R.layout.item_wdcc, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: WDCCModel) {
        helper.setText(R.id.textName, bean.product_name)
        helper.setText(R.id.textCode, bean.order_number)
        helper.setText(R.id.textPrice, bean.buy_total_price)
        helper.setText(R.id.textNumber, "${bean.number}/${bean.total_number}")
        helper.setText(R.id.textPriceInMarket, bean.market_value)
        helper.setText(R.id.textSellTime, "购买时间：${bean.buy_at}")
        helper.setText(R.id.textCompleteTime, "转让时间：${bean.start_at}")
        helper.setGone(R.id.textSellTime, TextUtils.isNotEmpty(bean.buy_at))
        helper.setGone(R.id.textCompleteTime, TextUtils.isNotEmpty(bean.start_at))
        var textBtnSell = helper.getView<TextView>(R.id.textBtnSell)
        var lp = textBtnSell.layoutParams as FrameLayout.LayoutParams
        if (bean.status == 2) {
            helper.setText(R.id.textBtnSell, "取消转让")
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            helper.setText(R.id.textBtnSell, "转让")
            lp.width = mContext.resources.getDimensionPixelOffset(R.dimen.dp_80)
        }
        textBtnSell.layoutParams = lp

        helper.setGone(R.id.frame, mode == WDCCActivity.MODE_NORMOL)
        helper.setGone(R.id.textBtnExtract, mode == WDCCActivity.MODE_NORMOL && bean.status != 2)
        helper.setGone(R.id.textBtnExchange, mode == WDCCActivity.MODE_NORMOL && bean.is_lock != 1 && bean.status != 2)

        helper.setGone(R.id.textBtnSell, bean.is_lock != 1)

        helper.addOnClickListener(R.id.textBtnSell)
        helper.addOnClickListener(R.id.textBtnExtract)
        helper.addOnClickListener(R.id.textBtnExchange)
    }

}
