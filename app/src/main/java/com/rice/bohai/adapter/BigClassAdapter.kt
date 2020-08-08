package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.BigClassModel
import com.rice.tool.TextUtils

class BigClassAdapter(var context: Context, data: MutableList<BigClassModel>) :
        BaseQuickAdapter<BigClassModel, BaseViewHolder>(R.layout.item_big_class, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: BigClassModel) {
        helper.setText(R.id.text, bean.name)
        helper.setVisible(R.id.line, data.indexOf(bean) != 0)
        helper.setVisible(R.id.imgLeft, bean.isChecked)
        helper.setVisible(R.id.imgRight, bean.isChecked)
        var text = helper.getView<TextView>(R.id.text)
        if (bean.isChecked) {
            text.setTextColor(context.resources.getColor(R.color.yellowfc))
//            text.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_cpq_left), null,
//                    mContext.resources.getDrawable(R.drawable.icon_cpq_right), null)
        } else {
            text.setTextColor(context.resources.getColor(R.color.black))
//            text.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

}
