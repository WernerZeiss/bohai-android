package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.PintuanOrderModel

class ProducePintuanAdapter(var context: Context, data: MutableList<PintuanOrderModel>) :
    BaseQuickAdapter<PintuanOrderModel, BaseViewHolder>(R.layout.item_produce_pintuan, data) {

    lateinit var onPintuanClickListener: OnPintuanClickListener

    interface OnPintuanClickListener {
        fun onPintuanClick(orderId: String)
    }

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: PintuanOrderModel) {
        helper.setText(R.id.textName, bean.name)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textPrice, bean.price)
        var textPintuan = helper.getView<TextView>(R.id.textPintuan)
        // 自动拼团开启
        if (MyApplication.instance.userInfo !== null && MyApplication.instance.userInfo!!.p_open_auto_group == 1) {
            textPintuan.setBackgroundResource(R.drawable.bg_btn_orange3)
            textPintuan.text = "自动拼团中"
            textPintuan.textSize = 10f
            textPintuan.isEnabled = false
        } else {
            // 手动拼团中
            if (bean.status == "2") {
                textPintuan.setBackgroundResource(R.drawable.bg_btn_green)
                textPintuan.text = "拼团中"
                textPintuan.textSize = 12f
                textPintuan.isEnabled = false
            }
            // 未拼团
            else {
                textPintuan.setBackgroundResource(R.drawable.bg_btn_orange3)
                textPintuan.text = "拼团"
                textPintuan.textSize = 12f
                textPintuan.isEnabled = true
                textPintuan.setOnClickListener() {
                    if (onPintuanClickListener != null) {
                        onPintuanClickListener.onPintuanClick(bean.id)
                    }
                }
            }
        }
    }

}
