package com.rice.bohai.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rice.bohai.R
import com.rice.bohai.adapter.SelectCardAdapter
import com.rice.bohai.listener.OnDoubleSelectListener
import com.rice.bohai.listener.OnSelectCardListener
import com.rice.bohai.listener.OnSelectPayListener
import com.rice.bohai.model.CardModel
import kotlinx.android.synthetic.main.item_dialog_pay_selector.view.*
import kotlinx.android.synthetic.main.item_dialog_select_card.view.*
import kotlinx.android.synthetic.main.item_dialog_single.view.*
import me.jessyan.autosize.utils.ScreenUtils

/**
 * @author CWQ
 * @date 2020/8/8
 */
object DialogHelper {

    /**
     * 选择银行卡
     */
    fun getSelectCardDialog(
        context: Context?,
        list: List<CardModel>,
        listener: OnSelectCardListener?
    ): Dialog? {
        return if (context != null) {
            val dialog = Dialog(context, R.style.translateDialog)
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.item_dialog_select_card, null)
            with(view) {
                rv_dialog_cards.layoutManager = LinearLayoutManager(context)
                val adapter = SelectCardAdapter(context, list)
                adapter.setOnItemClickListener { adapter, view, position ->
                    listener?.onSelectCard(position, list[position])
                }
                rv_dialog_cards.adapter = adapter

                iv_dialog_close.setOnClickListener {
                    dialog.dismiss()
                }
            }

            dialog.setContentView(view)
            dialog.window?.apply {
                setGravity(Gravity.BOTTOM)
                setWindowAnimations(R.style.AnimBottomIn)
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            }
            dialog
        } else {
            null
        }
    }


    fun getSingleDialog(
        context: Context?,
        title: String,
        content: String,
        listener: View.OnClickListener?
    ): Dialog? {
        return if (context != null) {
            val dialog = Dialog(context, R.style.translateDialog)
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.item_dialog_single, null)
            with(view) {
                tv_dialog_title.text = title
                tv_dialog_content.text = content
                tv_dialog_cancel.visibility = View.GONE
                tv_dialog_btn.setOnClickListener {
                    dialog.dismiss()
                    listener?.onClick(it)
                }
            }
            dialog.setContentView(view)
            dialog.window?.apply {
                setGravity(Gravity.CENTER)
                val size = ScreenUtils.getScreenSize(context)
                attributes.width = (size[0] * 0.8).toInt()
            }
            dialog.setCanceledOnTouchOutside(false)
            dialog
        } else {
            null
        }
    }


    fun getDoubleDialog(
        context: Context,
        title: String,
        content: String,
        leftBtnMsg:String,
        rightBtnMsg:String,
        listener: OnDoubleSelectListener
    ): Dialog {
        val dialog = Dialog(context, R.style.translateDialog)
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_dialog_single, null)
        with(view) {
            tv_dialog_title.text = title
            tv_dialog_content.text = content
            tv_dialog_cancel.visibility = View.VISIBLE
            tv_dialog_cancel.setOnClickListener {
                listener.onLeft(dialog)
            }
            tv_dialog_btn.setOnClickListener {
                listener.onRight(dialog)
            }
        }
        dialog.setContentView(view)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            val size = ScreenUtils.getScreenSize(context)
            attributes.width = (size[0] * 0.8).toInt()
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    //支付方式选择弹窗
    fun getPaySelectorDialog(
        context: Context,
        hasCoupon: Boolean,
        listener: OnSelectPayListener
    ): Dialog {
        val dialog = Dialog(context, R.style.translateDialog)
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_dialog_pay_selector, null)
        with(view) {
            tv_pay_type.text = if (hasCoupon) "请选择支付方式" else "支付方式"
            cb_balance.isEnabled = false
            cb_balance.isChecked = true
            if (!hasCoupon) {
                cb_coupon.visibility = View.GONE
            }
            cb_balance.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    cb_coupon.isChecked = false
                    cb_coupon.isEnabled = true
                }
            }
            cb_coupon.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    cb_balance.isChecked = false
                    cb_balance.isEnabled = true
                }
            }

            tv_submit.setOnClickListener {
                dialog.dismiss()
                listener.onSelected(if (cb_balance.isChecked) 0 else 1)
            }
            tv_cancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setContentView(view)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            val size = ScreenUtils.getScreenSize(context)
            attributes.width = (size[0] * 0.8).toInt()
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

}