package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.ClassModel
import com.rice.tool.TextUtils

class ClassAdapter(var context: Context, data: MutableList<ClassModel>) :
    BaseQuickAdapter<ClassModel, BaseViewHolder>(R.layout.item_class, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: ClassModel) {
        helper.setText(R.id.name, bean.name)
        var name = helper.getView<TextView>(R.id.name)
        if (bean.isChecked) {
            name.setTextColor(context.resources.getColor(R.color.yellowfc))
        } else {
            name.setTextColor(context.resources.getColor(R.color.black))
        }
//        name.setOnClickListener {
//            for (item in data) {
//                item.isChecked = item == bean
//            }
//            notifyDataSetChanged()
//        }
    }

}
