package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.PasswordModel
import com.rice.tool.TextUtils

class AccountAdapter(var context: Context, data: MutableList<PasswordModel>) :
    BaseQuickAdapter<PasswordModel, BaseViewHolder>(R.layout.item_account, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: PasswordModel) {
        helper.setText(R.id.text, bean.username)
        helper.setVisible(R.id.img, bean.isChecked())

//        name.setOnClickListener {
//            for (item in data) {
//                item.isChecked = item == bean
//            }
//            notifyDataSetChanged()
//        }
    }

}
