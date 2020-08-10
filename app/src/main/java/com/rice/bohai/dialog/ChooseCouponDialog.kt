package com.rice.bohai.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.rice.bohai.R
import kotlinx.android.synthetic.main.dialog_choose.*

class ChooseCouponDialog(var mContext: Context) : Dialog(mContext, R.style.BottomDialog) {

    var view: View = View.inflate(context, R.layout.dialog_choose_coupon, null)
    lateinit var onCallback: OnCallback

    interface OnCallback {
        fun onclick(type: Int)
    }

    init {
        setContentView(view)
        val window = this.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window.setGravity(Gravity.BOTTOM)
        val lp = window.attributes
        //设置宽
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        //设置高
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(0))
        window.setWindowAnimations(R.style.BottomDialog_Animation)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        initView()
    }

    fun initView() {
        textview_cancel.setOnClickListener() {
            dismiss()
        }
        textview_all.setOnClickListener() {
            if (onCallback != null) {
                onCallback.onclick(0)
            }
            dismiss()
        }
        textview_suc.setOnClickListener() {
            if (onCallback != null) {
                onCallback.onclick(1)
            }
            dismiss()
        }
        textview_fail.setOnClickListener() {
            if (onCallback != null) {
                onCallback.onclick(2)
            }
            dismiss()
        }
    }

}