package com.rice.bohai.adapter

import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.CardModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils

/**
 * @author CWQ
 * @date 2020/8/8
 */
class SelectCardAdapter(val context: Context, val list: List<CardModel>) :
    BaseQuickAdapter<CardModel, BaseViewHolder>(R.layout.item_adapter_cards, list) {

    override fun convert(helper: BaseViewHolder, bean: CardModel) {

        val ivIcon = helper.getView<ImageView>(R.id.iv_card_icon)
        GlideLoadUtils.getInstance().glideLoad(
            context,
            TextUtils.getImgUrl(Constant.getBaseUrl(), Constant.IMAGE_PRE_URL + bean.bank_logo),
            ivIcon
        )
        helper.setText(R.id.tv_card_name, bean.name)
        helper.setText(R.id.tv_card_num, bean.bank_number)
    }
}