package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.InputModel
import com.rice.tool.TextUtils

class InputAdapter(var context: Context, data: MutableList<InputModel>) :
    BaseQuickAdapter<InputModel, BaseViewHolder>(R.layout.item_input, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: InputModel) {
        helper.setText(R.id.textTag, bean.tag)
        helper.setText(R.id.edit, bean.text)
        var edit = helper.getView<EditText>(R.id.edit)
        edit.hint = bean.hint
        when (bean.mode) {
            InputModel.MODE_EDIT -> {
                edit.isFocusable = true
                edit.isFocusableInTouchMode = true
                edit.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        if (TextUtils.isNotEmpty(p0.toString())) {
                            bean.text = p0.toString()
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }
                })
            }
            InputModel.MODE_TEXT -> {
                if (TextUtils.isNotEmpty(bean.text)) {
                    edit.setText(bean.text)
                }
                edit.isFocusable = false
                edit.isFocusableInTouchMode = false
                helper.addOnClickListener(R.id.edit)
//                edit.setOnClickListener { onItemClickListener.onItemClick(this, edit, data.indexOf(bean)) }
            }
        }
    }

}
