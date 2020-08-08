package com.rice.bohai.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.activity.WebViewInAppActivity
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.BindBankCardActivity
import com.rice.bohai.activity.LoginActivity
import com.rice.bohai.activity.BuyHistoryActivity
import com.rice.bohai.adapter.*
import com.rice.bohai.dialog.XieyiDialog
import com.rice.bohai.model.*
import com.rice.dialog.OkCancelDialog
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.fragment_guamai.*
import kotlinx.android.synthetic.main.fragment_guamai.frameNoLogin
import kotlinx.android.synthetic.main.fragment_guamai.llSearchClass
import kotlinx.android.synthetic.main.include_no_login.*
import kotlinx.android.synthetic.main.include_search_class.*
import java.nio.charset.Charset


class GuamaiFragment : BaseImmersionFragment() {

    var listPriceTop: MutableList<NewPriceModel> = ArrayList()
    lateinit var priceAdapterTop: NewPriceAdapter
    var listPriceBottom: MutableList<PriceModel> = ArrayList()
    lateinit var priceAdapterBottom: PriceAdapter
    //    lateinit var signDialog: SignDialog

    lateinit var classAdapter: ClassAdapter
    var listClass: MutableList<ClassModel> = ArrayList()

    lateinit var searchAdapter: SearchAdapter
    var listSearch: MutableList<CommodityModel> = ArrayList()

    lateinit var pyHistoryAdapter: BuyHistoryAdapter
    var listHistory: MutableList<PintuanBuyModel> = ArrayList()

    lateinit var buyDialog: OkCancelDialog
    lateinit var xieyiDialog: XieyiDialog
    lateinit var loadingDialog: RLoadingDialog
    var page = 1
    var typeId = ""
    var id = ""
    var unit = ""
    var position_id = ""
    var isResume = false
    var isRefreshData = false
    var isFrist = true

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = false
        isWhiteNavigationBarIcon = false
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    override fun onResume() {
        super.onResume()
        isResume = true
        isRefreshData = true
        loadingDialog.dismiss()
        MyApplication.instance.getUserInfoFromWeb()
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_guamai

    @SuppressLint("SetTextI18n")
    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        buyDialog = OkCancelDialog(mContext)
        buyDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
            override fun onOkClick() {
                buy()
            }
        }
        recyclerClass.layoutManager = LinearLayoutManager(mContext)
        recyclerList.layoutManager = LinearLayoutManager(mContext)
        recyclerTop.layoutManager = LinearLayoutManager(mContext)
        recyclerBottom.layoutManager = LinearLayoutManager(mContext)
        //        signDialog = SignDialog(mContext)
        //        signDialog.xieyi = MyApplication.instance.systemModel?.buy_sign_agreement
        //        signDialog.onOkClickListener = object : SignDialog.OnOkClickListener {
        //            override fun onOkClick(filePath: String) {
        //                MyApplication.instance.onUploadCompleteListner =
        //                        object : MyApplication.OnUploadCompleteListner {
        //                            override fun onUploadComplete(src: String) {
        //                                uploadSign(src) //提交签名
        //                            }
        //                        }
        //                MyApplication.instance.uploadImage(filePath)
        //                MyApplication.instance.getUserInfoFromWeb()
        //            }
        //        }
        smr.setEnableLoadMore(false)
        smr.setEnableRefresh(false)
        priceAdapterTop = NewPriceAdapter(mContext, listPriceTop, true)
        priceAdapterTop.bindToRecyclerView(recyclerTop)
        priceAdapterTop.setEmptyView(R.layout.loading_dialog_gray2)
        recyclerTop.adapter = priceAdapterTop
        priceAdapterBottom = PriceAdapter(mContext, listPriceBottom, false)
        priceAdapterBottom.bindToRecyclerView(recyclerBottom)
        priceAdapterBottom.setEmptyView(R.layout.loading_dialog_gray2)
        recyclerBottom.adapter = priceAdapterBottom
        textSellNow.setOnClickListener {
            if (MyApplication.instance.userInfo?.is_valid == 0) {
                //先实名认证
                ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
                ToastUtil.showShort("请先实名认证")
                return@setOnClickListener
            }
            if (MyApplication.instance.userInfo?.is_sign_task == 0) {
                //签署数字签名
                xieyiDialog = XieyiDialog(mContext)
                xieyiDialog.onOkClickListener = object : XieyiDialog.OnOkClickListener {
                    override fun onOkClick() {
                        registerSignTask()
                    }
                }
                xieyiDialog.show()
                ToastUtil.showShort("首次购买请先签署协议")
                return@setOnClickListener
            }
            //            if (MyApplication.instance.userInfo?.is_buy_signature == 2) {
            //                ToastUtil.showShort("签名正在审核中，请稍后再试")
            //                MyApplication.instance.getUserInfoFromWeb()
            //                return@setOnClickListener
            //            } else if (MyApplication.instance.userInfo?.is_buy_signature == 0) {
            //                //先签名
            ////                if (!signDialog.isShowing) {
            ////                    signDialog.show()
            //                    ToastUtil.showShort("首次购买请先签署协议")
            //                }
            //            } else {

            getSignStatus()
            //            }
        }
        recyclerHistory.layoutManager = LinearLayoutManager(mContext)
        pyHistoryAdapter = BuyHistoryAdapter(mContext, listHistory)
        pyHistoryAdapter.bindToRecyclerView(recyclerHistory)
        pyHistoryAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recyclerHistory.adapter = pyHistoryAdapter

        classAdapter = ClassAdapter(mContext, listClass)
        classAdapter.bindToRecyclerView(recyclerClass)
        classAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        classAdapter.setOnItemClickListener { adapter, view, position ->
            for (item in listClass) {
                item.isChecked = listClass.indexOf(item) == position
            }
            classAdapter.notifyDataSetChanged()
            typeId = listClass[position].id.toString()
            isRefreshData = false
            Http.getRequestQueue().cancelAll("refreshData")
            initCommodity()
        }
        recyclerClass.adapter = classAdapter
        searchAdapter = SearchAdapter(mContext, listSearch)
        searchAdapter.setOnItemClickListener { adapter, view, position ->
            textNumberTag.text = "数量(${listSearch[position].unit})"
            editSearchPrice.setText("")
            editSearch.setText("")
            id = listSearch[position].id.toString()
            unit = listSearch[position].unit
            searchPrice()
        }
        searchAdapter.bindToRecyclerView(recyclerList)
        searchAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recyclerList.adapter = searchAdapter
        smr.setOnRefreshListener {
            page = 1
            initCommodity()
        }
        smr.setOnLoadMoreListener {
            page++
            initCommodity()
        }
        textName.setOnClickListener {
            showHideSearchFrame()
        }
        textCode.setOnClickListener {
            showHideSearchFrame()
        }
        img_shadow.setOnClickListener {
            showHideSearchFrame()
        }
        textLogin.setOnClickListener {
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        }
        textBtnSubNumber.setOnClickListener {
            var number = editNumberInput.text.toString()
            if (TextUtils.isNotEmpty(number) || TextUtils.isInt(number)) {
                if (number.toInt() > 1) {
                    editNumberInput.setText((number.toInt() - 1).toString())
                }
            }
        }
        textBtnAddNumber.setOnClickListener {
            var number = editNumberInput.text.toString()
            var max = textCanUseNumber.text.toString().toInt()
            if (max < 1) {
                //最大可卖为0
                ToastUtil.showShort("已达最大可卖数量")
                return@setOnClickListener
            }
            if (TextUtils.isNotEmpty(number) && TextUtils.isInt(number)) {
                //有输入数量
                if (number.toInt() >= max) {
                    ToastUtil.showShort("已达最大可卖数量")
                } else {
                    editNumberInput.setText((number.toInt() + 1).toString())
                }
            } else {
                //输入数量为空
                editNumberInput.setText("1") //设置数量为1
            }
        }
        editSearchPrice.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {   // 按下完成按钮，这里和上面imeOptions对应
                if (TextUtils.isNotEmpty(editSearchPrice.text.toString())) {
                    initPrice(editSearchPrice.text.toString())
                    return@setOnEditorActionListener false
                } else {
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener true //返回true，保留软键盘。false，隐藏软键盘
        }
        editSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {   // 按下完成按钮，这里和上面imeOptions对应
                if (TextUtils.isNotEmpty(editSearch.text.toString())) {
                    initCommodity(editSearch.text.toString())
                    return@setOnEditorActionListener false
                } else {
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener true //返回true，保留软键盘。false，隐藏软键盘
        }
        textSearch.setOnClickListener {
            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                ToastUtil.showShort("请先登录")
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                return@setOnClickListener
            }
            var b = Bundle()
            b.putString("id", id)
            b.putString("unit", unit)
            ActivityUtils.openActivity(mContext, BuyHistoryActivity::class.java, b)
        }
        initTypeList()
        searchPrice()
    }

    /**
     * 同意数字证书协议
     */
    private fun registerSignTask() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        loadingDialog.show()
        Http.post {
            url = RiceHttpK.getUrl(Constant.SIGN_REGISTER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
//                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    //                    if (!signDialog.isShowing) {
                    //                        signDialog.show()
                    //                    }
                    MyApplication.instance.getUserInfoFromWeb()
                    getSignStatus()
                } else {
                    loadingDialog.dismiss()
                }
            }
            onFail { error ->
                loadingDialog.dismiss()
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
     * 上传签名
     */
    private fun uploadSign() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        loadingDialog.show()
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_SIGNATURE)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                //                "sign_image" - src
                "type" - "2" //购买签名
            }
            onSuccess { bytes ->
                val result = RiceHttpK.getResult(mContext, bytes)
                if (TextUtils.isNotEmpty(result)) {
                    val model: SignModel = StringNullAdapter.gson.fromJson(result)
                    checkIsSign(1, model.sign_url)
                    MyApplication.instance.getUserInfoFromWeb()
                } else {
                    loadingDialog.dismiss()
                }
            }
            onFail { error ->
                loadingDialog.dismiss()
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun searchPrice(showAnim: Boolean = true) {
        initDetail()
        if (showAnim) {
            showHideSearchFrame(2)
        } else {
            showHideSearchFrame(3)
        }
    }

    /**
     * @param mode 0 显示→隐藏,隐藏→显示
     *             1 显示
     *             2 隐藏（有动画）
     *             3 隐藏（无动画）
     */
    fun showHideSearchFrame(mode: Int = 0) {
        when (mode) {
            0 -> {
                if (llSearchClass.visibility == View.VISIBLE) {
                    var topOut = AnimationUtils.loadAnimation(mContext, R.anim.exit_to_top)
                    llSearchClass.startAnimation(topOut)
                    llSearchClass.visibility = View.GONE
                } else {
                    var topIn = AnimationUtils.loadAnimation(mContext, R.anim.enter_from_top)
                    llSearchClass.startAnimation(topIn)
                    llSearchClass.visibility = View.VISIBLE
                }
            }
            1 -> {
                var topIn = AnimationUtils.loadAnimation(mContext, R.anim.enter_from_top)
                llSearchClass.startAnimation(topIn)
                llSearchClass.visibility = View.VISIBLE
            }
            2 -> {
                var topOut = AnimationUtils.loadAnimation(mContext, R.anim.exit_to_top)
                llSearchClass.startAnimation(topOut)
                llSearchClass.visibility = View.GONE
            }
            3 -> {
                llSearchClass.visibility = View.GONE
            }
        }

    }

    /**
     * 类型列表
     */
    fun initTypeList() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRODUCT_TYPE_LIST)
            params {
                "type" - "2" //转让商品分类
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                classAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    smr.setEnableLoadMore(true)
                    smr.setEnableRefresh(true)
                    val model: ClassListModel = StringNullAdapter.gson.fromJson(result)
                    listClass.clear()
                    listClass.addAll(model.lists)
                    classAdapter.notifyDataSetChanged()
                    initCommodity()
                }
            }
            onFail { error ->
                classAdapter.setEmptyView(R.layout.include_fail)
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
    private fun initCommodity(word: String = "") {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRODUCT_LIST)
            params {
                "product_type_id" - typeId
                if (TextUtils.isNotEmpty(word)) {
                    "word" - word
                }
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                searchAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: CommodityListModel = StringNullAdapter.gson.fromJson(result)
                    listSearch.clear()
                    listSearch.addAll(model.lists)
                    searchAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                searchAdapter.setEmptyView(R.layout.include_fail)
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    fun initDetail(id: String, positionId: String) {
        this@GuamaiFragment.id = id
        this@GuamaiFragment.position_id = positionId
        initDetail()
    }

    override fun onStop() {
        super.onStop()
        isRefreshData = false
        Http.getRequestQueue().cancelAll("refreshData")
    }

    /**
     * 获取产品详情
     */
    @SuppressLint("SetTextI18n")
    fun initDetail() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRODUCT_DETAIL)
            params {
                if (TextUtils.isNotEmpty(id)) {
                    "id" - id
                }
            }
            tag = "refreshData"
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    isRefreshData = true
                    val model: CommodityDeModel = StringNullAdapter.gson.fromJson(result)
                    textName.text = model.model.name
                    //                    textCode.text = model.model.no_number
                    textNumberTag.text = "数量(${model.model.unit})"
                    textPriceInput.text =
                        mContext.resources.getString(R.string.CNY) + model.model.price
                    textPrice.text = mContext.resources.getString(R.string.CNY) + model.model.price
                    id = model.model.id.toString()
                    unit = model.model.unit
//                    buyDialog.setInfo("是否确认花费${model.model.buy_total_price}购买${model.model.name}${model.model.number}${model.model.unit}")
                    buyDialog.setInfo("购买${model.model.number}${model.model.unit}${model.model.name}订单：${model.model.buy_total_price}元")
//                    pyHistoryAdapter.unit = model.model.unit
//                    pyHistoryAdapter.notifyDataSetChanged()
//                    if (isFrist) {
//                        Thread {
//                            while (true) {
//                                if (isRefreshData) {
//                                    initPrice()
//                                    initHistory()
//                                }
//                                Thread.sleep(3000)
//                            }
//                        }.start()
//                        isFrist = false
//                    }
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
     * 获取签名状态
     */
    @SuppressLint("SetTextI18n")
    fun getSignStatus() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        loadingDialog.show()
        Http.post {
            url = RiceHttpK.getUrl(Constant.CHECK_CONTRACT)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "integral_product_id" - id
                "type" - "2" //买入
            }
            tag = "getSignStatus"
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: SignModel = StringNullAdapter.gson.fromJson(result)
                    checkIsSign(model.is_sign, model.sign_url)
                    MyApplication.instance.getUserInfoFromWeb()
                } else {
                    loadingDialog.dismiss()
                }
            }
            onFail { error ->
                loadingDialog.dismiss()
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    private fun checkIsSign(is_sign: Int, signUrl: String) {
        MyApplication.instance.getUserInfoFromWeb()
        when (is_sign) {
            0 -> {
                //注册
                uploadSign()
            }
            1 -> {
                //签署
                loadingDialog.dismiss()
                var b = Bundle()
                b.putString("url", signUrl)
                ActivityUtils.openActivity(mContext, WebViewInAppActivity::class.java, b)
                //从其他浏览器打开
//                loadingDialog.dismiss()
//                val intent = Intent()
//                intent.action = Intent.ACTION_VIEW
//                val content_url = Uri.parse(signUrl)
//                intent.data = content_url
//                startActivity(Intent.createChooser(intent, "请选择浏览器"))
            }
            2 -> {
                //直接购买
                isFirst()
            }
        }
    }

    /**
     * 获取价格数据
     */
    private fun initPrice(word: String = "") {
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRODUCT_TRANSACTION_DETAIL)
            params {
                if (TextUtils.isNotEmpty(id)) {
                    "id" - id
                }
                if (TextUtils.isNotEmpty(position_id)) {
                    "position_id" - position_id
                }
                if (TextUtils.isNotEmpty(word)) {
                    "word" - word
                }
            }
            tag = "price"
            onSuccess { byts ->
                Log.i("hel->", url)
                priceAdapterTop.setEmptyView(R.layout.include_no_data)
                priceAdapterBottom.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: PriceListModel = StringNullAdapter.gson.fromJson(result)
                    textCanUseNumber.text = model.total_number
                    if (TextUtils.isEmpty(position_id)) {
                        position_id = model.position_id
                    }
                    listPriceTop.clear()
                    listPriceBottom.clear()
                    listPriceTop.addAll(model.shipmentList)
                    listPriceBottom.addAll(model.lists)
                    priceAdapterTop.notifyDataSetChanged()
                    priceAdapterBottom.notifyDataSetChanged()
                }
            }
            onFail { error ->
                priceAdapterTop.setEmptyView(R.layout.include_fail)
                priceAdapterBottom.setEmptyView(R.layout.include_fail)
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
     * 是否是首单
     */
    private fun isFirst() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.IS_FIRST_ORDER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                if (TextUtils.isNotEmpty(id)) {
                    "product_id" - id
                }
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: FirstBuyModel = StringNullAdapter.gson.fromJson(result)

                    if (model.is_first_order == 1) {
                        buyDialog.setInfo(model.tip)
                    }
                    loadingDialog.dismiss()
                    if (!buyDialog.isShowing) {
                        buyDialog.show()
                    }
                }
            }
            onFail { error ->
                priceAdapterTop.setEmptyView(R.layout.include_fail)
                priceAdapterBottom.setEmptyView(R.layout.include_fail)
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
     * 交易记录
     */
    private fun initHistory() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            mActivity.runOnUiThread { frameNoLogin.visibility = View.VISIBLE }
            return
        }
        mActivity.runOnUiThread { frameNoLogin.visibility = View.INVISIBLE }
        Http.post {
            url = RiceHttpK.getUrl(Constant.PRODUCT_SALE_RECORD)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                if (TextUtils.isNotEmpty(id)) {
                    "id" - id
                }
            }
            tag = "history"
            onSuccess { byts ->
                Log.i("hel->", url)
                pyHistoryAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: PYHistoryListModel = StringNullAdapter.gson.fromJson(result)
                    listHistory.clear()
//                    listHistory.addAll(model.lists)
//                    pyHistoryAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                pyHistoryAdapter.setEmptyView(R.layout.include_fail)
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
     * 购买
     */
    private fun buy() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.BUY_POSITION)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "id" - id
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                //                ToastUtil.showShort(status.message)
                val toast: Toast = Toast.makeText(mContext, status.message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, -200)
                toast.show()
                if (status.code == RiceHttpK.SUCCESS) {
                    initDetail()
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