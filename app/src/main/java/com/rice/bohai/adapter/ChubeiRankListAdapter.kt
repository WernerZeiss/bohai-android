package com.rice.bohai.adapter

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.aobo.model.ChubeiRankModel
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils

class ChubeiRankListAdapter(var context: Context, data: MutableList<ChubeiRankModel>) :
    BaseQuickAdapter<ChubeiRankModel, BaseViewHolder>(R.layout.item_chubei_rank, data) {

    override fun convert(helper: BaseViewHolder, bean: ChubeiRankModel) {
        var img = helper.getView<ImageView>(R.id.imageview_logo)
        GlideLoadUtils.getInstance()
            .glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.logo), img)
        helper.setText(R.id.textMobile, bean.mobile)
        helper.setText(R.id.textPercent, bean.percent)
        var layout = helper.getView<FrameLayout>(R.id.rootlayout)
        var framelayoutLogo = helper.getView<FrameLayout>(R.id.framelayout_logo)
        if (bean.index == 0) {
            layout.setBackgroundResource(R.drawable.bg_btn_orange6)
            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_1)
        } else if (bean.index == 1) {
            layout.setBackgroundResource(R.drawable.bg_btn_orange7)
            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_2)
        } else if (bean.index == 2) {
            layout.setBackgroundResource(R.drawable.bg_btn_orange8)
            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_3)
        } else {
            layout.setBackgroundResource(R.drawable.bg_grayef5)
            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_n)
        }
//        if (data.indexOf(bean) == 0) {
//            layout.setBackgroundResource(R.drawable.bg_btn_orange6)
//            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_1)
//        } else if (data.indexOf(bean) == 1) {
//            layout.setBackgroundResource(R.drawable.bg_btn_orange7)
//            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_2)
//        } else if (data.indexOf(bean) == 2) {
//            layout.setBackgroundResource(R.drawable.bg_btn_orange8)
//            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_3)
//        } else {
//            layout.setBackgroundResource(R.drawable.bg_grayef5)
//            framelayoutLogo.setBackgroundResource(R.drawable.chubei_rank_n)
//        }
    }

}
