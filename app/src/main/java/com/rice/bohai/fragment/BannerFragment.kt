package com.rice.bohai.fragment

import android.os.Bundle
import android.view.TextureView
import androidx.recyclerview.widget.LinearLayoutManager
import com.rice.activity.BannerDetailActivity
import com.rice.activity.WebViewActivity
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.MyOrderAdapter
import com.rice.bohai.model.BannerModel
import com.rice.bohai.model.OrderModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import kotlinx.android.synthetic.main.item_banner.*

class BannerFragment : BaseImmersionFragment() {

    var item: BannerModel? = null

    init {
        isContentInvade = true
        navigationBarColorId = R.color.white
        statusBarColorId = R.color.white
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = false
        viewTopId = R.id.viewTop
    }

    companion object {
        fun newInstance(item: BannerModel): BannerFragment {
            val args = Bundle()
            args.putSerializable("item", item)
            val fragment = BannerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val contentViewLayoutID: Int
        get() = R.layout.item_banner

    override fun initView() {
        item = arguments?.getSerializable("item") as BannerModel
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), item?.image), mContext.resources.getDimensionPixelOffset(R.dimen.dp_8), img)
        img.setOnClickListener {
            var b = Bundle()
            b.putBoolean("textX", true)
            b.putString("url", item?.content)
            b.putString("title", item?.title)
            ActivityUtils.openActivity(mContext, BannerDetailActivity::class.java, b)
        }
    }

}