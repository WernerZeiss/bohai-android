package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.activity.BannerDetailActivity
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.MessageAdapter
import com.rice.bohai.adapter.NotifyAdapter
import com.rice.bohai.model.MessageListModel
import com.rice.bohai.model.MessageModel
import com.rice.bohai.model.NotifyListModel
import com.rice.bohai.model.NotifyModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_message.frameNoLogin
import kotlinx.android.synthetic.main.include_no_login.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*

@SuppressLint("Registered")
class MessageActivity : RiceBaseActivity() {

    var listNotify: MutableList<NotifyModel> = ArrayList()
    var listHYZX: MutableList<NotifyModel> = ArrayList()
    var listMessage: MutableList<MessageModel> = ArrayList()
    lateinit var notifyAdapter: NotifyAdapter
    lateinit var hyzxAdapter: NotifyAdapter
    lateinit var messsageAdapter: MessageAdapter
    var page = 1
    var tab = TAB_MESSAGE

    companion object {
        const val TAB_NOTIFY = 0
        const val TAB_HYZX = 1
        const val TAB_MESSAGE = 2
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_message
    }

    override fun initView() {
        imgBack.setOnClickListener { finish() }
        recycler.layoutManager = LinearLayoutManager(mContext)
        notifyAdapter = NotifyAdapter(mContext, listNotify)
        notifyAdapter.setOnItemClickListener { adapter, view, position ->
            var b = Bundle()
            b.putBoolean("textX", true)
            b.putString("url", listNotify[position].content)
            b.putString("title", listNotify[position].name)
            ActivityUtils.openActivity(mContext, BannerDetailActivity::class.java, b)
        }
        notifyAdapter.bindToRecyclerView(recycler)
        hyzxAdapter = NotifyAdapter(mContext, listHYZX)
        hyzxAdapter.setOnItemClickListener { adapter, view, position ->
            var b = Bundle()
            b.putBoolean("textX", true)
            b.putString("url", listHYZX[position].content)
            b.putString("title", listHYZX[position].name)
            ActivityUtils.openActivity(mContext, BannerDetailActivity::class.java, b)
        }
        hyzxAdapter.bindToRecyclerView(recycler)
        messsageAdapter = MessageAdapter(mContext, listMessage, true)
        messsageAdapter.setOnItemClickListener { adapter, view, position ->
            var b = Bundle()
            b.putString("id", listMessage[position].id.toString())
            ActivityUtils.openActivity(mContext, MessageDeActivity::class.java, b)
        }
        messsageAdapter.bindToRecyclerView(recycler)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radBtnNotify -> {
                    recycler.adapter = notifyAdapter
                    runOnUiThread { frameNoLogin.visibility = View.GONE }
                }
                R.id.radBtnHYZX -> {
                    recycler.adapter = hyzxAdapter
                    runOnUiThread { frameNoLogin.visibility = View.GONE }
                }
                R.id.radBtnMyMessage -> {
                    recycler.adapter = messsageAdapter
                    if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                        runOnUiThread { frameNoLogin.visibility = View.VISIBLE }
                    }
                }
            }
        }
        recycler.adapter = notifyAdapter
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        getNotifyList(Constant.NOTICE_LIST)
        getNotifyList(Constant.NEWS_LIST)
        getMessage()
        textLogin.setOnClickListener {
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        }
        changeTab()
    }

    override fun onResume() {
        super.onResume()
        page = 1
        initData()
    }

    private fun initData() {
        when (radioGroup.checkedRadioButtonId) {
            R.id.radBtnNotify -> {
                getNotifyList(Constant.NOTICE_LIST)
            }
            R.id.radBtnHYZX -> {
                getNotifyList(Constant.NEWS_LIST)
            }
            R.id.radBtnMyMessage -> {
                getMessage()
            }
        }
    }

    private fun changeTab() {
        when (tab) {
            TAB_NOTIFY -> {
                radioGroup.check(R.id.radBtnNotify)
            }
            TAB_HYZX -> {
                radioGroup.check(R.id.radBtnHYZX)
            }
            TAB_MESSAGE -> {
                radioGroup.check(R.id.radBtnMyMessage)
            }
        }
    }

    /**
     * 获取公告信息/行业资讯
     */
    private fun getNotifyList(urlPath: String) {
        Http.post {
            url = RiceHttpK.getUrl(urlPath)
            params {
                "page" - page.toString()
            }
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                notifyAdapter.setEmptyView(R.layout.include_no_data)
                hyzxAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: NotifyListModel = StringNullAdapter.gson.fromJson(result)
                    when (urlPath) {
                        Constant.NOTICE_LIST -> {
                            //公告信息
                            if (page == 1) {
                                listNotify.clear()
                            }
                            listNotify.addAll(model.lists)
                            notifyAdapter.notifyDataSetChanged()
                        }
                        Constant.NEWS_LIST -> {
                            //行业资讯
                            if (page == 1) {
                                listHYZX.clear()
                            }
                            listHYZX.addAll(model.lists)
                            hyzxAdapter.notifyDataSetChanged()
                        }
                    }

                }
            }
            onFail { error ->
                notifyAdapter.setEmptyView(R.layout.include_fail)
                hyzxAdapter.setEmptyView(R.layout.include_fail)
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
     * 我的消息
     */
    private fun getMessage() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.MESSAGE_LIST)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                messsageAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: MessageListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listMessage.clear()
                    }
                    listMessage.addAll(model.lists)
                    messsageAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                messsageAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    override fun getIntentData() {
        tab = intent.extras?.getInt("tab", TAB_MESSAGE) ?: TAB_MESSAGE
    }

    override fun clear() {

    }

}