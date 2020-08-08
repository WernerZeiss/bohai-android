package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.AddressModel
import com.rice.tool.TextUtils

class AddressAdapter(var context: Context, data: MutableList<AddressModel>) :
        BaseQuickAdapter<AddressModel, BaseViewHolder>(R.layout.item_address, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: AddressModel) {
        helper.setText(R.id.textName, bean.realname + "  " + bean.mobile)
        helper.setText(R.id.textAddress, bean.province + bean.city + bean.district + bean.house_number)
        var textSetDefault = helper.getView<TextView>(R.id.textSetDefault)
        if (bean.is_default > 0) {
            textSetDefault.setCompoundDrawablesRelativeWithIntrinsicBounds(context.resources.getDrawable(R.drawable.icon_check_address_focus), null, null, null)
        } else {
            textSetDefault.setCompoundDrawablesRelativeWithIntrinsicBounds(context.resources.getDrawable(R.drawable.icon_check_address), null, null, null)
        }
        helper.addOnClickListener(R.id.textSetDefault)
        helper.addOnClickListener(R.id.textEdit)
        helper.addOnClickListener(R.id.textDelete)
        var constraintRoot = helper.getView<ConstraintLayout>(R.id.constraintRoot)
        var lp = constraintRoot.layoutParams as RecyclerView.LayoutParams
        if (data.indexOf(bean) == 0) {
            lp.setMargins(0, context.resources.getDimensionPixelOffset(R.dimen.dp_88), 0, 0)
        } else if (data.indexOf(bean) == data.lastIndex) {
            lp.setMargins(0, 0, 0, 0)
        } else {
            lp.setMargins(0, 0, 0, context.resources.getDimensionPixelOffset(R.dimen.dp_16))
        }
        constraintRoot.layoutParams = lp
    }

}
