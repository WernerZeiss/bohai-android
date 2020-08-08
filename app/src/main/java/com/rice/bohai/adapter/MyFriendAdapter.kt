package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.URLUtil
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.zxing.common.StringUtils
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.FriendModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils

class MyFriendAdapter(var context: Context, data: MutableList<FriendModel>) :
        BaseQuickAdapter<FriendModel, BaseViewHolder>(R.layout.item_myfriend, data) {

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: FriendModel) {
        helper.setText(R.id.textName, bean.nickname)
        helper.setText(R.id.textTime, bean.created_at)
        helper.setText(R.id.textPhone, bean.mobile)
        helper.setText(R.id.textAttr, bean.attribute_name)
        var imgHeader = helper.getView<ImageView>(R.id.imgHeader)
        GlideLoadUtils.getInstance().glideLoad(context, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.avatar), imgHeader)
    }

}
