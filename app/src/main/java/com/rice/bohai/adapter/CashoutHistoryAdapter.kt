package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.CashoutModel
import com.rice.tool.TextUtils

class CashoutHistoryAdapter(var context: Context, data: MutableList<CashoutModel>) :
        BaseQuickAdapter<CashoutModel, BaseViewHolder>(R.layout.item_jf_history, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: CashoutModel) {
        helper.setText(R.id.textName, "提现到银行卡：尾号${TextUtils.getEndNum(bean.bank_number, 4)}")
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textScore, bean.price)
        helper.setText(R.id.textStatus, bean.is_published_name)
        //        if (bean.show_integral.startsWith("-")) {
        //            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.gray8))
        //        } else {
        //            helper.setTextColor(R.id.textScore, context.resources.getColor(R.color.orangefc))
        //        }
    }

}
