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
import com.rice.bohai.model.CardModel
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
        listener: View.OnClickListener
    ): Dialog? {
        return if (context != null) {
            val dialog = Dialog(context, R.style.translateDialog)
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.item_dialog_single, null)
            with(view) {
                tv_dialog_title.text = title
                tv_dialog_content.text = content
                tv_dialog_btn.setOnClickListener(listener)
            }
            dialog.setContentView(view)
            dialog.window?.apply {
                setGravity(Gravity.CENTER)
                val size = ScreenUtils.getScreenSize(context)
                attributes.width = (size[0] * 0.8).toInt()
            }
            dialog.setCanceledOnTouchOutside(false)
//            dialog.setCancelable(false)
            dialog
        } else {
            null
        }
    }
}