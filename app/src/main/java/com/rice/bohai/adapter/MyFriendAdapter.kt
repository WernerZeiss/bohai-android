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
        helper.setText(R.id.textPhone, formatNumber(bean.mobile))
        helper.setText(R.id.textAttr, bean.attribute_name)
        helper.setText(R.id.tvLevel,"V"+bean.member_id)
        helper.setText(R.id.tvGroupNum,"团队当月拼团数："+bean.group_total_number)
        helper.setText(R.id.tvGroupActiveNum,"团队活跃人数："+bean.group_active_total_number)
        var imgHeader = helper.getView<ImageView>(R.id.imgHeader)
        GlideLoadUtils.getInstance()
            .glideLoad(context, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.avatar), imgHeader)
    }

    /**
     * 格式化手机号，隐藏中间4位
     */
    private fun formatNumber(phone: String?): String? {
        var result = phone
        if (!TextUtils.isEmpty(phone) && phone?.length == 11) {
            result = phone.substring(0, 3) + "****" + phone.substring(7)
        }
        return result
    }
}
