package com.rice.bohai.fragment

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.ExtractActivity
import com.rice.bohai.adapter.THJEAdapter
import com.rice.bohai.model.NumberModel
import com.rice.bohai.model.THJEListModel
import com.rice.bohai.model.THJEModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.include_smr_recycler_match.*

/**
 * 提货金额Fragment
 */
class THJEFragment : BaseImmersionFragment() {

    init {
        isContentInvade = true
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = true
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    var page = 1

    var listTHJE: MutableList<THJEModel> = ArrayList()
    lateinit var thjeAdapter: THJEAdapter

    companion object {
        fun newInstance(): THJEFragment {
            val args = Bundle()
            val fragment = THJEFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override val contentViewLayoutID: Int
        get() = R.layout.include_smr_recycler_match

    override fun initView() {
        //提货金额
        recycler.layoutManager = LinearLayoutManager(mContext)
        thjeAdapter = THJEAdapter(mContext, listTHJE)
//        thjeAdapter.setOnItemChildClickListener { adapter, view, position ->
//            when (view.id) {
//                R.id.textExchange -> {
//                    //兑换
//                    pickUp(listTHJE[position].id.toString())
//                }
//            }
//        }
        thjeAdapter.bindToRecyclerView(recycler)
        thjeAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = thjeAdapter
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        initData()
    }

    /**
     * 提货金额
     */
    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_SETTLEMENT_POSITION)
            params {
                "page" - page.toString()
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onFinish {
                if (isResumed) {
                    refresh.finishRefresh()
                    refresh.finishLoadMore()
                }
            }
            onSuccess { byts ->
                thjeAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: THJEListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listTHJE.clear()
                    }
                    listTHJE.addAll(model.lists)
                    thjeAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                thjeAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

//    /**
//     * 获取可提货数量
//     */
//    private fun pickUp(id: String) {
//        Http.post {
//            url = RiceHttpK.getUrl(Constant.PICK_UP_NUMBER)
//            params {
//                "id" - id
//                "access_token" - MyApplication.instance.userInfo!!.access_token
//            }
//            onFinish {
//                if (isResumed) {
//                    refresh.finishRefresh()
//                    refresh.finishLoadMore()
//                }
//            }
//            onSuccess { byts ->
//                Log.i("hel->", url)
//                val result = RiceHttpK.getResult(mContext,byts)
//                if (TextUtils.isNotEmpty(result)) {
//                    val model: NumberModel = StringNullAdapter.gson.fromJson(result)
//                    if (model.number > 0) {
//                        var b = Bundle()
//                        b.putString("id", id)
//                        b.putInt("mode", ExtractActivity.MODE_THQ)
//                        b.putInt("number", model.number)
//                        ActivityUtils.openActivity(mContext, ExtractActivity::class.java, b)
//                    }
//                }
//            }
//            onFail { error ->
//                thjeAdapter.setEmptyView(R.layout.include_fail)
//                var message = error.message
//                if ((error.message ?: "").contains("java")) {
//                    Logger.e(message ?: "")
//                    message = "未知错误"
//                }
//                ToastUtil.showShort(message)
//            }
//        }
//    }

}