package com.rice.bohai.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rice.bohai.R
import com.rice.bohai.adapter.TextSelectAdapter
import kotlinx.android.synthetic.main.text_select_dialog.*

/**
 * 文字单选Dialog
 */
class TextSelectDialog(var mContext: Context) : Dialog(mContext, R.style.centerDialog) {

    var onOkClickListener: OnOkClickListener? = null
    var onCancelClickListener: OnCancelClickListener? = null

    var list: MutableList<String> = ArrayList()
    lateinit var textSelectAdapter: TextSelectAdapter

    interface OnOkClickListener {
        fun onOkClick(str: String)
    }

    interface OnCancelClickListener {
        fun onCancelClick()
    }

    var view: View

    init {
        view = View.inflate(context, R.layout.text_select_dialog, null)
        setContentView(view)
        val window = this.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window.setGravity(Gravity.CENTER)
        val lp = window.attributes
        //设置宽
        lp.width = context.resources.getDimensionPixelOffset(R.dimen.width_ok_cancel_dialog)
        //设置高
        lp.height = context.resources.getDimensionPixelOffset(R.dimen.height_ok_cancel_dialog2)
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(0))
        initView()
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

//    fun setCancleText(text: String) {
//        textbtn_cancel.text = text
//    }

    fun setTitle(title: String) {
        text_tag.text = title
    }

//    fun setOkText(text: String) {
//        textbtn_ok.text = text
//    }

    fun setData(strList: MutableList<String>) {
        list.clear()
        list.addAll(strList)
        textSelectAdapter.notifyDataSetChanged()
    }

    private fun initView() {
        recycler.layoutManager = LinearLayoutManager(mContext)
        textSelectAdapter = TextSelectAdapter(mContext, list)
        textSelectAdapter.setOnItemClickListener { adapter, view, position ->
            onOkClickListener?.onOkClick(list[position])
            dismiss()
        }
        recycler.adapter = textSelectAdapter
        imgClose.setOnClickListener {
            if (onCancelClickListener != null) {
                onCancelClickListener!!.onCancelClick()
            }
            dismiss()
        }
//        textbtn_ok.setOnClickListener {
//            if (onOkClickListener != null) {
//                dismiss()
//                onOkClickListener!!.onOkClick()
//            }
//        }
    }


}
