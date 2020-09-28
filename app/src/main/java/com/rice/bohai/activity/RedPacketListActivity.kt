package com.rice.bohai.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.WalletLogAdapter
import com.rice.bohai.model.WalletLogListModel
import com.rice.bohai.model.WalletLogModel
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_redpacketlist.*
import java.nio.charset.Charset

/**
 * @author CWQ
 * @date 2020/9/26
 */
class RedPacketListActivity : RiceBaseActivity() {

    lateinit var loadingDialog: RLoadingDialog
    private var page = 1
    private var listProduce: MutableList<WalletLogModel> = ArrayList()
    private var mAdapter: WalletLogAdapter? = null
    private var needWalletMoney = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_redpacketlist
    }


    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        srl.setOnRefreshListener {
            page = 1
            initData()
        }
        srl.setOnLoadMoreListener {
            page++
            initData()
        }
        tv_draw.setOnClickListener {
            getDraw()
        }

        initData()
    }


    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_WALLET_LOG)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: WalletLogListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        listProduce.clear()
                        needWalletMoney = model.need_wallet_money
                        tv_red_money.text = "合计领取：¥${model.need_wallet_money}"
                    }
                    listProduce.addAll(model.lists)
                    if (mAdapter == null) {
                        mAdapter = WalletLogAdapter(this@RedPacketListActivity, listProduce)
                        rv_list.layoutManager = LinearLayoutManager(this@RedPacketListActivity)
                        rv_list.adapter = mAdapter
                    } else {
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
            onFinish {
                srl.finishLoadMore()
                srl.finishRefresh()
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
     * 领取金额
     */
    private fun getDraw() {
        if (!TextUtils.isEmpty(needWalletMoney) && needWalletMoney.toDouble() > 0) {
            Http.post {
                url = RiceHttpK.getUrl(Constant.GET_WALLET_NOW)
                params {
                    "access_token" - MyApplication.instance.userInfo!!.access_token
                }
                onStart {
                    loadingDialog.show()
                }
                onSuccess { byts ->
                    var data = byts.toString(Charset.defaultCharset())
                    var status = PublicModel.forjson(data)
                    ToastUtil.showShort(status.message)
                    if (status.code == RiceHttpK.SUCCESS) {
                        page = 1
                        initData()
                    }
                }
                onFinish {
                    loadingDialog.hide()
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
        } else {
            ToastUtil.showShort("暂无可领取金额")
        }
    }

    override fun getIntentData() {

    }

    override fun clear() {

    }
}