package com.rice.bohai.activity

import android.util.Log
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
import com.rice.bohai.adapter.PHDeAdapter
import com.rice.bohai.dialog.ChooseCouponDialog
import com.rice.bohai.model.PHDeListModel
import com.rice.bohai.model.PHDeModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_xsph_de.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.text.SimpleDateFormat

/**
 * @author CWQ
 * @date 2020/8/9
 * 配货券明细
 */
class PHQListActivity : RiceBaseActivity() {

    var page = 1

    lateinit var pintuanMXAdapter: PHDeAdapter
    var listPHDe: MutableList<PHDeModel> = ArrayList()
    private var start_time = ""
    var type = 0//0全部 1收入 2支出

    override fun getLayoutId(): Int {
        return R.layout.activity_phq_list
    }

    override fun initView() {
        recycler.layoutManager = LinearLayoutManager(mContext)
        pintuanMXAdapter = PHDeAdapter(mContext, listPHDe)
        pintuanMXAdapter.bindToRecyclerView(recycler)
        pintuanMXAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = pintuanMXAdapter
        llSelectDate.setOnClickListener {
            showTimerPicker()
        }
        layout_choose.setOnClickListener() {
            var dialog = ChooseCouponDialog(this)
            dialog.onCallback = object : ChooseCouponDialog.OnCallback {
                override fun onclick(t: Int) {
                    page = 1
                    type = t
                    updateTypeText()
                    initData()
                }
            }
            dialog.show()
        }
        //                recycler.addItemDecoration(SpaceItemDecoration2(mContext.resources.getDimensionPixelOffset(R.dimen.dp_4)))
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


    private fun updateTypeText() {
        tv_choose.text = when (type) {
            0 -> "显示全部"
            1 -> "仅收入"
            else -> "仅支出"
        }
    }

    /**
     * 显示日期选择器
     */
    private fun showTimerPicker() {
        var title = "请选择日期"
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            start_time = SimpleDateFormat("yyyy-MM-dd").format(date)
            page = 1
            initData()
        })
            .setTitleText(title)
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .build().show()
    }

    /**
     * 拼团明细
     */
    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PROFIT_RECORD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
                if (TextUtils.isNotEmpty(start_time)) {
                    "start_time" - start_time
                }
                "type" - type.toString()
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                pintuanMXAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                Log.i("hel->", result)
                if (TextUtils.isNotEmpty(result)) {
                    val model: PHDeListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listPHDe.clear()
                    }
                    listPHDe.addAll(model.lists)
                    pintuanMXAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                pintuanMXAdapter.setEmptyView(R.layout.include_fail)
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