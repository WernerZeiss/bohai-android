package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cloud.progressbar.ProgressButton
import com.rice.bohai.R
import com.rice.bohai.model.ProfitHistoryModel
import com.rice.tool.TextUtils

class XSPHAdapter(var context: Context, data: MutableList<ProfitHistoryModel>) :
        BaseQuickAdapter<ProfitHistoryModel, BaseViewHolder>(R.layout.item_xsph, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: ProfitHistoryModel) {
        helper.setText(R.id.textName, bean.product_name)
        //        helper.setText(R.id.textCode, bean.product_no_number)
        helper.setText(R.id.textPrice, bean.price)
        helper.setText(R.id.textNumber, bean.market_value)
        helper.setText(R.id.textPriceInMarket, bean.profit_num)
        helper.setText(R.id.textSellTime, "购买时间：${bean.buy_at}")
        helper.setText(R.id.textCompleteTime, "转让时间：${bean.start_at}")
        helper.setGone(R.id.textSellTime, TextUtils.isNotEmpty(bean.buy_at))
        helper.setGone(R.id.textCompleteTime, TextUtils.isNotEmpty(bean.start_at))

        helper.setVisible(R.id.progress_button, bean.status == 2)
        helper.setVisible(R.id.textBtnSell, bean.status != 2)
        helper.setGone(R.id.textBtnCancel, bean.status == 2 || bean.status == 7)
        helper.setGone(R.id.textBtnExtract, bean.status != 2 && bean.status != 7 && bean.profit_num.toDouble() >= 1.0)
        helper.setGone(R.id.textBtnExchange, bean.status != 2 && bean.status != 7)
        helper.setText(R.id.tv_type,"类型："+bean.type_name)

        if (bean.profit_num.toDouble() >= 1.0) {
            helper.setText(R.id.textBtnSell, "转让")
        } else {
            helper.setText(R.id.textBtnSell, "拼团")
        }

        try {
            helper.setText(R.id.textBtnCancel, "取消${bean.status_name.substring(0, bean.status_name.lastIndex)}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var progress_button = helper.getView<ProgressButton>(R.id.progress_button)
        if (bean.status == 2) {
            progress_button.setProgress(bean.fight_percent.substring(0, bean.fight_percent.lastIndex).toDouble().toInt())
            progress_button.setText(bean.fight_percent)
        }

        helper.addOnClickListener(R.id.textBtnSell)
        helper.addOnClickListener(R.id.textBtnExtract)
        helper.addOnClickListener(R.id.textBtnExchange)
        helper.addOnClickListener(R.id.textBtnCancel)
    }

}
