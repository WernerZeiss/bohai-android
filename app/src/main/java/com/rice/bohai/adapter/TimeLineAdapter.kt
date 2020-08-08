package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.model.GlideUrl
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.TranslateModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils
import com.rice.view.CircleView

class TimeLineAdapter(var context: Context, data: MutableList<TranslateModel>) :
    BaseQuickAdapter<TranslateModel, BaseViewHolder>(R.layout.item_timeline, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: TranslateModel) {
        helper.setText(R.id.textDate, bean.AcceptTime.split(" ")[0])
        helper.setText(R.id.textTime, bean.AcceptTime.split(" ")[1])
        helper.setText(R.id.textInfo, bean.AcceptStation)
        var timelineTop = helper.getView<View>(R.id.timelineTop)
//        var pointOut = helper.getView<CircleView>(R.id.pointOut)
        var pointIn = helper.getView<ImageView>(R.id.pointIn)
        var timelineBottom = helper.getView<View>(R.id.timelineBottom)
        if (data.indexOf(bean) == 0) {
            //首项
            helper.setTextColor(R.id.textInfo, context.resources.getColor(R.color.cir_in))
//            pointOut.visibility = View.VISIBLE
            pointIn.visibility = View.VISIBLE
            timelineBottom.visibility = View.VISIBLE
            timelineTop.visibility = View.GONE
//            pointIn.setColor(context.resources.getColor(R.color.cir_in).toLong())
            pointIn.setImageResource(R.drawable.time_point_focus)
        } else if (data.indexOf(bean) == data.lastIndex) {
            //尾项
            helper.setTextColor(R.id.textInfo, context.resources.getColor(R.color.gray6))
//            pointOut.visibility = View.GONE
            pointIn.visibility = View.VISIBLE
            timelineBottom.visibility = View.GONE
            timelineTop.visibility = View.VISIBLE
//            pointIn.setColor(context.resources.getColor(R.color.graydc).toLong())
            pointIn.setImageResource(R.drawable.time_point)
        } else {
            //中间项
            helper.setTextColor(R.id.textInfo, context.resources.getColor(R.color.gray6))
//            pointOut.visibility = View.GONE
            pointIn.visibility = View.VISIBLE
            timelineBottom.visibility = View.VISIBLE
            timelineTop.visibility = View.VISIBLE
//            pointIn.setColor(context.resources.getColor(R.color.graydc).toLong())
            pointIn.setImageResource(R.drawable.time_point)
        }
//        var constraintRoot = helper.getView<ConstraintLayout>(R.id.constraintRoot)
//        constraintRoot.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//            var lp = constraintRoot.layoutParams
//            lp.height = bottom - top + context.resources.getDimensionPixelOffset(R.dimen.dp_16)
//            constraintRoot.layoutParams = lp
//        }
    }

}
