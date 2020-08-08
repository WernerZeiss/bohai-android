package com.rice.bohai.activity

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.aobo.model.ChubeiRankModel
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.adapter.ChubeiRankListAdapter
import com.rice.bohai.model.ChubeiRankListModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_chubei_rank.*

class ChubeiRankActivity : RiceBaseActivity() {

    lateinit var chubeiRankListAdapter: ChubeiRankListAdapter
    var rankList: MutableList<ChubeiRankModel> = ArrayList()


    override fun getLayoutId(): Int {
        return R.layout.activity_chubei_rank
    }

    override fun initView() {
        recyclerRank.layoutManager = LinearLayoutManager(mContext)
        chubeiRankListAdapter = ChubeiRankListAdapter(mContext, rankList)
        chubeiRankListAdapter.bindToRecyclerView(recyclerRank)
        chubeiRankListAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recyclerRank.adapter = chubeiRankListAdapter

        initRankList()
    }

    override fun getIntentData() {
    }

    override fun clear() {

    }

    private fun initRankList() {
        Http.post {
            url =
                RiceHttpK.getUrl(Constant.CHUBEI_RULE_RANK)
            onSuccess { byts ->
                Log.i("hel->", url)
                chubeiRankListAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ChubeiRankListModel = StringNullAdapter.gson.fromJson(result)
                    tv_chubei_rank_title.text = model.month
                    for (i in 0 until model.list.size) {
                        model.list[i].index = i
                    }
                    rankList.addAll(model.list)
                    chubeiRankListAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                chubeiRankListAdapter.setEmptyView(R.layout.include_fail)
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