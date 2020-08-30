package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.MyFriendAdapter
import com.rice.bohai.model.FriendListModel
import com.rice.bohai.model.FriendModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_myfriend.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.text.SimpleDateFormat

@SuppressLint("Registered")
class MyFriendActivity : RiceBaseActivity() {

    lateinit var myFriendAdapter: MyFriendAdapter
    var list: MutableList<FriendModel> = ArrayList()
    var page = 1
    var type = 1
    var start_time = ""
    var end_time = ""
    var word = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_myfriend
    }

    @SuppressLint("SimpleDateFormat")
    override fun initView() {
        //        imgBack.setOnClickListener { finish() }
        recycler.layoutManager = LinearLayoutManager(mContext)
        myFriendAdapter = MyFriendAdapter(mContext, list)
        myFriendAdapter.bindToRecyclerView(recycler)
        myFriendAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = myFriendAdapter
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                word = editSearch.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        editSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {   // 按下完成按钮，这里和上面imeOptions对应
                if (TextUtils.isNotEmpty(editSearch.text.toString())) {
                    initData()
                    return@setOnEditorActionListener false
                } else {
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener true //返回true，保留软键盘。false，隐藏软键盘
        }
        llSelectDate.setOnClickListener {
            showTimerPicker(true)
        }
        textClear.setOnClickListener {
            start_time = ""
            end_time = ""
            word = ""
            editSearch.setText("")
            page = 1
            initData()
        }
        //        radioGroup.setOnCheckedChangeListener { group, checkedId ->
        //            editSearch.setText("")
        //            when (checkedId) {
        //                R.id.radBtnMyFriend -> {
        //                    //我的好友
        //                    type = 1
        //                    initData()
        //                }
        //                R.id.radBtnMyFriendCir -> {
        //                    //朋友圈
        //                    type = 2
        //                    initData()
        //                }
        //            }
        //        }
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        textSearch.setOnClickListener {
            page = 1
            initData()
        }
        initData()
    }

    /**
     * 显示日期选择器
     */
    @SuppressLint("SimpleDateFormat")
    private fun showTimerPicker(isStart: Boolean) {
        var title = ""
        if (isStart) {
            title = "请选择开始日期"
        } else {
            title = "请选择结束日期"
        }
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            if (isStart) {
                start_time = SimpleDateFormat("yyyy-MM-dd").format(date)
                showTimerPicker(false)
            } else {
                end_time = SimpleDateFormat("yyyy-MM-dd").format(date)
            }
        })
                .setTitleText(title)
                .setType(booleanArrayOf(true, true, true, false, false, false)) // 只显示时分
                .build().show()
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_FRIENDS)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
                //                "type" - type.toString()
                "type" - "1"
                if (TextUtils.isNotEmpty(word)) {
                    "word" - word
                }
                //                if (TextUtils.isNotEmpty(start_time)) {
                //                    "start_time" - start_time
                //                }
                //                if (TextUtils.isNotEmpty(end_time)) {
                //                    "end_time" - end_time
                //                }
            }
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                myFriendAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                Log.i("friends->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: FriendListModel = StringNullAdapter.gson.fromJson(result)
                    textPeopleNumber.text = "总人数：${model.total_count}"
                    if (page == 1) {
                        list.clear()
                    }
                    list.addAll(model.lists)
                    myFriendAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                myFriendAdapter.setEmptyView(R.layout.include_fail)
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

    }

    override fun clear() {

    }

}