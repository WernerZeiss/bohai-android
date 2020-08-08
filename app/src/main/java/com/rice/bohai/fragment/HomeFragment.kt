package com.rice.bohai.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.activity.BannerDetailActivity
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.CommodityDeActivity
import com.rice.bohai.activity.DataActivity
import com.rice.bohai.activity.MessageActivity
import com.rice.bohai.activity.MyScoreActivity
import com.rice.bohai.adapter.*
import com.rice.bohai.anim.ScalePageTransformer
import com.rice.bohai.model.*
import com.rice.racar.web.RiceHttpK
import com.rice.tool.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.abs

class HomeFragment : BaseImmersionFragment() {

    var listFragment: MutableList<Fragment> = ArrayList()
    var listBanner: MutableList<BannerModel> = ArrayList()
    var listNotify: MutableList<NotifyModel> = ArrayList()
    lateinit var notifyHomeAdapter: NotifyHomeAdapter
    var listClass: MutableList<ClassModel> = ArrayList()
    lateinit var classHomeAdapter: ClassHomeAdapter
    var listCommodity: MutableList<CommodityModel> = ArrayList()
    lateinit var commodityHomeAdapter: CommodityHomeAdapter
    var listBigClass: MutableList<BigClassModel> = ArrayList()
    lateinit var bigClassAdapter: BigClassAdapter
    var page = 1
    private var x1: Float = 0f
    private var x2: Float = 0f
    private var y1: Float = 0f
    private var y2: Float = 0f
    private var x3: Float = 0f
    private var x4: Float = 0f
    private var y3: Float = 0f
    private var y4: Float = 0f

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = false
        isWhiteNavigationBarIcon = false
        viewTopId = R.id.viewTop
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_home

    override fun initView() {
        var llm = LinearLayoutManager(mContext)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        recyclerTags.layoutManager = llm
        llm = LinearLayoutManager(mContext)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        recyclerBigClass.layoutManager = llm
        bigClassAdapter = BigClassAdapter(mContext, listBigClass)
        bigClassAdapter.setOnItemClickListener { adapter, view, position ->
            for (item in listBigClass) {
                item.isChecked = listBigClass.indexOf(item) == position
            }
            initTypeList()
            bigClassAdapter.notifyDataSetChanged()
        }
        recyclerBigClass.adapter = bigClassAdapter
        classHomeAdapter = ClassHomeAdapter(mContext, listClass)
        classHomeAdapter.setOnItemClickListener { adapter, view, position ->
            RecyclerHelper.moveToPosition(recyclerTags, position)
            for (item in listClass) {
                item.isChecked = listClass.indexOf(item) == position
            }
            page = 1
            initCommodity()
            classHomeAdapter.notifyDataSetChanged()
        }
        recyclerTags.adapter = classHomeAdapter
        recyclerProducts.layoutManager = LinearLayoutManager(mContext)
        commodityHomeAdapter = CommodityHomeAdapter(mContext, listCommodity)
        commodityHomeAdapter.setOnItemClickListener { adapter, view, position ->
            var b = Bundle()
            b.putString("id", listCommodity[position].id.toString())
            ActivityUtils.openActivity(mContext, CommodityDeActivity::class.java, b)
        }
        //        commodityHomeAdapter.onLoadMoreClickListener = object : CommodityHomeAdapter.OnLoadMoreClickListener {
        //            override fun onLoadMoreClick() {
        //                page++
        //                initCommodity()
        //            }
        //        }
        commodityHomeAdapter.bindToRecyclerView(recyclerProducts)
        commodityHomeAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recyclerProducts.adapter = commodityHomeAdapter
        notifyHomeAdapter = NotifyHomeAdapter(mContext, listNotify)
        notifyHomeAdapter.setOnItemClickListener { adapter, view, position ->
            var b = Bundle()
            b.putInt("tab", MessageActivity.TAB_NOTIFY)
            ActivityUtils.openActivity(mContext, MessageActivity::class.java, b)
        }
        frameMessage.setOnClickListener {
            ActivityUtils.openActivity(mContext, MessageActivity::class.java)
        }
        llXXBZ.setOnClickListener {
            //新手帮助
            var b = Bundle()
            b.putInt("mode", DataActivity.MODE_HELP_LIST)
//            b.putBoolean("textX", true)
//            b.putString("url", MyApplication.instance.systemModel?.help_center)
//            b.putString("title", mContext.resources.getString(R.string.help_center))
//            ActivityUtils.openActivity(mContext, BannerDetailActivity::class.java, b)
            ActivityUtils.openActivity(mContext, DataActivity::class.java, b)
        }
        llHYZX.setOnClickListener {
            var b = Bundle()
            b.putInt("tab", MessageActivity.TAB_HYZX)
            ActivityUtils.openActivity(mContext, MessageActivity::class.java, b)
        }
        llJFSC.setOnClickListener {
            //积分商城
            ActivityUtils.openActivity(mContext, MyScoreActivity::class.java)
        }
        llZXKF.setOnClickListener {
            //在线客服
            if (InstallUtils.isQQClientAvailable(mContext)) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=${MyApplication.instance.systemModel?.service_qq}")))
            } else {
                ToastUtil.showShort("请先安装QQ")
            }
        }
        imageView.setOnClickListener {
            var b = Bundle()
            b.putInt("tab", MessageActivity.TAB_NOTIFY)
            ActivityUtils.openActivity(mContext, MessageActivity::class.java, b, Constant.REQUEST_MESSAGE_LIST)
        }
        recyclerNoti.layoutManager = LinearLayoutManager(mContext)
        recyclerNoti.adapter = notifyHomeAdapter
        //        recyclerProducts.setHasFixedSize(true)
        //        recyclerProducts.setNestedScrollingEnabled(false)
        smr.setEnableRefresh(false)
        smr.setOnLoadMoreListener {
            page++
            initCommodity()
        }
        recyclerBigClass.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                //当手指按下的时候
                x1 = event.x
                y1 = event.y
            }
            if (event.action == MotionEvent.ACTION_UP) {
                //当手指离开的时候
                x2 = event.x
                y2 = event.y
                if (abs(y2 - y1) > 20) {
                    //上滑
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
        recyclerTags.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                //当手指按下的时候
                x3 = event.x
                y3 = event.y
            }
            if (event.action == MotionEvent.ACTION_UP) {
                //当手指离开的时候
                x4 = event.x
                y4 = event.y
                if (abs(y4 - y3) > 20) {
                    //上滑
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
        //        appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
        //            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
        //                when (state) {
        //                    State.EXPANDED -> {
        //                        //展开
        //                    }
        //                    State.COLLAPSED -> {
        //                        //折叠状态
        //                        recyclerProducts.scrollToPosition(0)
        //                    }
        //                    State.IDLE -> {
        //                        //中间状态
        //
        //                    }
        //                }
        //            }
        //        })
        //        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
        //            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
        //                page++
        //                initCommodity()
        //            }
        //        }
        initUserData()
        initBigTypeList()
        initBanner()
        getNotifyList()
    }

    private fun initUserData() {
        if (MyApplication.instance.userInfo?.is_no_read_message == 1) {
            point.visibility = View.VISIBLE
        } else {
            point.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        initUserData()
    }

    /**
     * 获取公告信息/行业资讯
     */
    private fun getNotifyList() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.NOTICE_LIST)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: NotifyListModel = StringNullAdapter.gson.fromJson(result)
                    //公告信息
                    listNotify.clear()
                    for (item in model.lists) {
                        if (model.lists.indexOf(item) < 2) {
                            listNotify.add(item)
                        } else {
                            break
                        }
                    }
                    notifyHomeAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 大类型列表
     */
    private fun initBigTypeList() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.BIG_TYPE_LIST)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: BigClassListModel = StringNullAdapter.gson.fromJson(result)
                    listBigClass.clear()
                    listBigClass.addAll(model.lists)
                    for (item in listBigClass) {
                        item.isChecked = listBigClass.indexOf(item) == 0
                        if (listBigClass.indexOf(item) == 0) {
                            initTypeList()
                        }
                    }
                    bigClassAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 类型列表
     */
    private fun initTypeList() {
        var big_type_id = ""
        for (item in listBigClass) {
            if (item.isChecked) {
                big_type_id = item.id.toString()
            }
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.INTEGRAL_PRODUCT_TYPE_LIST)
            params {
                "big_type_id" - big_type_id //首页商品分类
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ClassListModel = StringNullAdapter.gson.fromJson(result)
                    listClass.clear()
                    listClass.addAll(model.lists)
                    for (item in listClass) {
                        item.isChecked = listClass.indexOf(item) == 0
                        if (listClass.indexOf(item) == 0) {
                            initCommodity()
                        }
                    }
                    classHomeAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 获取产品列表数据
     */
    private fun initCommodity() {
        var big_type_id = ""
        for (item in listBigClass) {
            if (item.isChecked) {
                big_type_id = item.id.toString()
                break
            }
        }
        var classId = ""
        for (item in listClass) {
            if (item.isChecked) {
                classId = item.id.toString()
                break
            }
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.INTEGRAL_PRODUCT_LIST)
            params {
                "product_type_id" - classId
                "big_type_id" - big_type_id
                "page" - page.toString()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                commodityHomeAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: IntegralListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listCommodity.clear()
                    }
                    listCommodity.addAll(model.lists)
                    commodityHomeAdapter.notifyDataSetChanged()
                }
            }
            onFinish {
                smr.finishLoadMore()
                smr.finishRefresh()
            }
            onFail { error ->
                commodityHomeAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 轮播图
     */
    private fun initBanner() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.CAROUSEL_LIST)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: BannerListModel = StringNullAdapter.gson.fromJson(result)
                    //                    for (item in model.lists) {
                    //                        listFragment.add(BannerFragment.newInstance(item))
                    //                    }
                    bannerViewPager.offscreenPageLimit = model.lists.size //缓存，根据需求自定义
                    bannerViewPager.pageMargin = 0 - mContext.resources.getDimensionPixelOffset(R.dimen.dp_26) //处理两边碎片显示
                    bannerViewPager.setPageTransformer(true, ScalePageTransformer(true))
                    //                    bannerViewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
                    //                        override fun getItem(i: Int): Fragment {
                    //                            return listFragment[i]
                    //                        }
                    //
                    //                        override fun getCount(): Int {
                    //                            return listFragment.size
                    //                        }
                    //                    }
                    bannerViewPager.adapter = BannerAdapter(mContext, model.lists)
                    bannerViewPager.startAutoScroll()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

}