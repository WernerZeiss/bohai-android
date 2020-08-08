package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.ShareBGModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.ScreenUtils
import com.rice.tool.TextUtils

class ShareBGAdapter(var context: Context, data: MutableList<ShareBGModel>) :
        BaseQuickAdapter<ShareBGModel, BaseViewHolder>(R.layout.item_share_bg, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: ShareBGModel) {
        var img = helper.getView<ImageView>(R.id.img)
        var lp = img.layoutParams
        lp.width = (ScreenUtils.getRealScreenWidth(MyApplication.instance) - mContext.resources.getDimensionPixelOffset(R.dimen.dp_32)) / 3
        lp.height = (lp.width / (375.0 / 600.0)).toInt()
        img.layoutParams = lp
//        when (data.indexOf(bean) % 3) {
//            0 -> {
        //第一列
//        if ((data.indexOf(bean) + 1) % 3 == 0) {
//            img.setPadding(mContext.resources.getDimensionPixelOffset(R.dimen.dp_4), 0, mContext.resources.getDimensionPixelOffset(R.dimen.dp_4), mContext.resources.getDimensionPixelOffset(R.dimen.dp_4))
//        } else {
//            img.setPadding(mContext.resources.getDimensionPixelOffset(R.dimen.dp_4), 0, 0, mContext.resources.getDimensionPixelOffset(R.dimen.dp_4))
//        }
//            }
//        }
        GlideLoadUtils.getInstance().glideLoad(mContext, bean.image, img)
    }

}
