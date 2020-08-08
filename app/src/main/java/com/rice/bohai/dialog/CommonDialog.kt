package com.rice.bohai.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.rice.bohai.R
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.dialog_common.*

class CommonDialog(var mContext: Context, var msg: String) :
    Dialog(mContext, R.style.centerDialog) {

    var view: View = View.inflate(context, R.layout.dialog_common, null)
    lateinit var onCallback: OnCallback

    interface OnCallback {
        fun okClick()
        fun xieyiClick()
    }

    init {
        setContentView(view)
        val window = this.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window.setGravity(Gravity.CENTER)
        val lp = window.attributes
        //设置宽
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        //设置高
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(0))
//        window.setWindowAnimations(R.style.BottomDialog_Animation)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        initView()
    }

    fun initView() {
        text_msg.text = msg
        btn_cancel.setOnClickListener() {
            dismiss()
        }
        btn_save.setOnClickListener() {
            if (checkbox.isChecked) {
                if (onCallback != null) {
                    onCallback.okClick()
                }
                dismiss()
            } else {
                ToastUtil.showLong("请同意此协议")
            }
        }
        text_xieyi.setOnClickListener() {
            if (onCallback != null) {
                onCallback.xieyiClick()
            }
        }
    }


}