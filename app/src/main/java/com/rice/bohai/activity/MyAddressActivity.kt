package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.AddressAdapter
import com.rice.bohai.model.AddressListModel
import com.rice.bohai.model.AddressModel
import com.rice.dialog.OkCancelDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_address.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class MyAddressActivity : RiceBaseActivity() {

    lateinit var addressAdapter: AddressAdapter
    var list: MutableList<AddressModel> = ArrayList()
    lateinit var deleteDialog: OkCancelDialog
    var position = -1

    var mode = MODE_NORMAL

    companion object {
        const val MODE_NORMAL = 0
        const val MODE_SELECT = 1
    }

    init {
        isContentInvade = true
        navigationBarColorId = R.color.white
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = true
        viewTopId = R.id.viewTop
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_address
    }

    override fun initView() {
        deleteDialog = OkCancelDialog(mContext)
        deleteDialog.setInfo("确定要删除该地址吗？")
        deleteDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
            override fun onOkClick() {
                deleteAddress()
            }
        }
        toolbar.setOnOkClickListener { ActivityUtils.openActivity(mContext, AddAddressActivity::class.java) }
        recycler.layoutManager = LinearLayoutManager(mContext)
        addressAdapter = AddressAdapter(mContext, list)
        addressAdapter.bindToRecyclerView(recycler)
        addressAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        if (mode == MODE_SELECT) {
            addressAdapter.setOnItemClickListener { adapter, view, position ->
                var intent = Intent()
                var b = Bundle()
                b.putSerializable("address", list[position])
                intent.putExtras(b)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        addressAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.textSetDefault -> {
                    //设为默认
                    setDefault(position)
                }
                R.id.textEdit -> {
                    //编辑
                    var b = Bundle()
                    b.putSerializable("model", list[position])
                    ActivityUtils.openActivity(mContext, AddAddressActivity::class.java, b)
                }
                R.id.textDelete -> {
                    //删除
                    this@MyAddressActivity.position = position
                    if (!deleteDialog.isShowing) {
                        deleteDialog.show()
                    }
                }
            }
        }
//        addressAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        smr.setOnRefreshListener {
            getAddress()
        }
        smr.setEnableLoadMore(false)
        recycler.adapter = addressAdapter
    }

    override fun onResume() {
        super.onResume()
        getAddress()
    }

    /**
     * 获取地址列表
     */
    private fun getAddress() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.ADDRESS_LIST)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onFinish {
                smr.finishRefresh()
                smr.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                addressAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    Logger.d(result)
                    val model: AddressListModel = StringNullAdapter.gson.fromJson(result)
                    list.clear()
                    list.addAll(model.addressList)
                    addressAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                addressAdapter.setEmptyView(R.layout.include_fail)
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
     * 删除地址
     */
    private fun deleteAddress() {
        if (position < 0) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.DELETE_ADDRESS)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "id" - list[position].id.toString()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                var data = byts.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    list.removeAt(position)
                    addressAdapter.notifyItemRemoved(position)
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
     * 设置默认地址
     */
    private fun setDefault(position: Int) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.ADDRESS_IS_DEFAULT)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "id" - list[position].id.toString()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                var data = byts.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                if (status.code == RiceHttpK.SUCCESS) {
                    for (item in list) {
                        if ((list.indexOf(item)) == position) {
                            item.is_default = 1
                        } else {
                            item.is_default = 0
                        }
                    }
                    addressAdapter.notifyDataSetChanged()
                } else {
                    ToastUtil.showShort(status.message)
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

    override fun getIntentData() {
        mode = intent.extras?.getInt("mode", MODE_NORMAL) ?: MODE_NORMAL
    }

    override fun clear() {

    }

}