package com.rice.bohai.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.ConfirmExchangeActivity
import com.rice.bohai.activity.LoginActivity
import com.rice.bohai.adapter.ShopcarAdapter
import com.rice.bohai.model.ShopcarListModel
import com.rice.bohai.model.ShopcarModel
import com.rice.dialog.InputDialog
import com.rice.dialog.OkCancelDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import com.rice.view.RiceToolbar
import kotlinx.android.synthetic.main.fragment_shopcar.*
import kotlinx.android.synthetic.main.fragment_shopcar.frameNoLogin
import kotlinx.android.synthetic.main.include_no_login.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.io.Serializable
import java.nio.charset.Charset

class ShopcarFragment : BaseImmersionFragment() {

    var mode = MODE_NORMAL
    lateinit var deleteDialog: OkCancelDialog
    var list: MutableList<ShopcarModel> = ArrayList()
    lateinit var shopcarAdapter: ShopcarAdapter
    lateinit var inputDialog: InputDialog
    var page = 1
    var isSelectAll = false

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = false
        isWhiteNavigationBarIcon = false
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    companion object {
        const val MODE_NORMAL = 0
        const val MODE_EDIT = 1

        fun newInstance(mode: Int): ShopcarFragment {
            val args = Bundle()
            args.putInt("mode", mode)
            val fragment = ShopcarFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_shopcar

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mode = arguments?.getInt("mode", MODE_NORMAL) ?: MODE_NORMAL
        deleteDialog = OkCancelDialog(mContext)
        deleteDialog.setInfo("确认要删除选中的商品吗？")
        deleteDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
            override fun onOkClick() {
                deleteShopcar(shopcarAdapter.getSelectedIds())
            }
        }
        toolbar.setOnOkClickListener {
            if (mode == MODE_NORMAL) {
                mode = MODE_EDIT
            } else {
                mode = MODE_NORMAL
            }
            initMode()
        }
        inputDialog = InputDialog(mContext, "请输入兑换数量")
        recycler.layoutManager = LinearLayoutManager(mContext)
        shopcarAdapter = ShopcarAdapter(mContext, list)
        shopcarAdapter.bindToRecyclerView(recycler)
        shopcarAdapter.onSelectedListener = object : ShopcarAdapter.OnSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onSelected(isSelectedAll: Boolean) {
                isSelectAll = isSelectedAll
                if (isSelectAll) {
                    textSelectAll.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_check_shopcar_focus), null, null, null)
                } else {
                    textSelectAll.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_check_shopcar), null, null, null)
                }
                textTotal.text = "合计：${shopcarAdapter.getSelectedPrice()}"
            }
        }
        shopcarAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.textBtnSub -> {
                    if (list[position].number > 1) {
                        list[position].number--
                    }
                    shopcarAdapter.notifyDataSetChanged()
                    modifyShopcar(list[position].id.toString(), list[position].number.toString())
                    textTotal.text = "合计：${shopcarAdapter.getSelectedPrice()}"
                }
                R.id.textBtnAdd -> {
                    if (list[position].number < 99) {
                        list[position].number++
                    }
                    shopcarAdapter.notifyDataSetChanged()
                    modifyShopcar(list[position].id.toString(), list[position].number.toString())
                    textTotal.text = "合计：${shopcarAdapter.getSelectedPrice()}"
                }
                R.id.editNumber -> {
                    inputDialog.setContent(list[position].number.toString())
                    inputDialog.onOkClickListener = object : InputDialog.OnOkClickListener {
                        override fun onOkClick(str: String) {
                            if (TextUtils.isEmpty(str) || !TextUtils.isPositiveInt(str)) {
                                ToastUtil.showShort("请输入正确的数量")
                            } else {
                                list[position].number = str.toInt()
                                shopcarAdapter.notifyDataSetChanged()
                                modifyShopcar(list[position].id.toString(), list[position].number.toString())
                                textTotal.text = "合计：${shopcarAdapter.getSelectedPrice()}"
                            }
                        }
                    }
                    if (!inputDialog.isShowing) {
                        inputDialog.show()
                    }
                }
            }
        }
        textSelectAll.setOnClickListener {
            if (isSelectAll) {
                textSelectAll.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_check_shopcar), null, null, null)
            } else {
                textSelectAll.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_check_shopcar_focus), null, null, null)
            }
            isSelectAll = !isSelectAll
            shopcarAdapter.selectedAll(isSelectAll)
            textTotal.text = "合计：${shopcarAdapter.getSelectedPrice()}"
        }
        recycler.adapter = shopcarAdapter
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        textLogin.setOnClickListener {
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        }
        textBtnSubmit.setOnClickListener {
            when (mode) {
                MODE_NORMAL -> {
                    //兑换
                    var ids = shopcarAdapter.getSelectedIds()
                    if (TextUtils.isEmpty(ids)) {
                        ToastUtil.showShort("请选择至少一个商品")
                        return@setOnClickListener
                    }
                    var b = Bundle()
                    b.putSerializable("list", shopcarAdapter.getSelectedItems() as Serializable)
                    b.putString("ids", shopcarAdapter.getSelectedIds())
                    b.putInt("mode", ConfirmExchangeActivity.MODE_MUTI)
                    var intent = Intent(mContext, ConfirmExchangeActivity::class.java)
                    intent.putExtras(b)
                    startActivityForResult(intent, Constant.REQUEST_SHOP_CAR_BUY)
                }
                MODE_EDIT -> {
                    //删除
                    var ids = shopcarAdapter.getSelectedIds()
                    if (TextUtils.isEmpty(ids)) {
                        ToastUtil.showShort("请选择至少一个商品")
                        return@setOnClickListener
                    }
                    if (!deleteDialog.isShowing) {
                        deleteDialog.show()
                    }
                }
            }
        }
        initMode()
        initData()
    }

    override fun onResume() {
        super.onResume()
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            toolbar.setOkMode(RiceToolbar.MODE_HIDE)
        } else {
            toolbar.setOkMode(RiceToolbar.MODE_TEXT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_SHOP_CAR_BUY -> {
                page = 1
                isSelectAll = false
                textSelectAll.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_check_shopcar), null, null, null)
                initData()
                mode = MODE_NORMAL
            }
        }
    }

    /**
     * 购物车列表
     */
    @SuppressLint("SetTextI18n")
    fun initData() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            mActivity.runOnUiThread { frameNoLogin.visibility = View.VISIBLE }
            return
        }
        mActivity.runOnUiThread { frameNoLogin.visibility = View.INVISIBLE }
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_SHOP_CART)
            params {
                "page" - page.toString()
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onFinish {
                refresh.finishRefresh()
                refresh.finishLoadMore()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                shopcarAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: ShopcarListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        list.clear()
                        textTotal.text = "合计：${shopcarAdapter.getSelectedPrice()}"
                    }
                    if (model.lists.size > 0) {
                        isSelectAll = false
                        textSelectAll.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_check_shopcar), null, null, null)
                    }
                    list.addAll(model.lists)
                    shopcarAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                shopcarAdapter.setEmptyView(R.layout.include_fail)
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
     * 修改购物车商品数量
     */
    fun modifyShopcar(id: String, number: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_SHOP_CART)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "id" - id
                "number" - number
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                if (status.code != RiceHttpK.SUCCESS) {
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

    /**
     * 删除购物车商品
     */
    fun deleteShopcar(ids: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.BATCH_DELETE_CART)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "ids" - ids
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                if (status.code == RiceHttpK.SUCCESS) {
                    page = 1
                    isSelectAll = false
                    textSelectAll.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.resources.getDrawable(R.drawable.icon_check_shopcar), null, null, null)
                    initData()
                    mode = MODE_NORMAL
                    initMode()
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

    private fun initMode() {
        when (mode) {
            MODE_NORMAL -> {
                toolbar.setOkText("编辑")
                textBtnSubmit.text = "确认兑换"
            }
            MODE_EDIT -> {
                toolbar.setOkText("完成")
                textBtnSubmit.text = "删除"
            }
        }
    }

}