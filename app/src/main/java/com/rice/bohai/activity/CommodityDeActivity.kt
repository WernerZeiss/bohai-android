package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.model.CommodityDeModel
import com.rice.bohai.tools.GlideImageLoader
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_commodity_de.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class CommodityDeActivity : RiceBaseActivity() {

    var mode = MODE_HOME
    var id = ""
    var number = 1

    companion object {
        const val MODE_HOME = 0
        const val MODE_JF = 1
    }

    init {
        isContentInvade = true
        statusBarColorId = R.color.black50
        isWhiteStatusBarIcon = true
        viewTopId = R.id.toolbar
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_commodity_de
    }

    override fun initView() {
        initMode()
        initDetail()
    }

    private fun initMode() {
        when (mode) {
            MODE_HOME -> {
                //首页模式
                llNumber.visibility = View.VISIBLE
            }
            MODE_JF -> {
                //积分兑换
                llNumber.visibility = View.GONE
            }
        }
    }

    /**
     * 获取产品详情
     */
    @SuppressLint("SetTextI18n")
    fun initDetail() {
        if (TextUtils.isEmpty(id)) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.INTEGRAL_PRODUCT_DETAIL)
            params {
                if (TextUtils.isNotEmpty(id)) {
                    "id" - id
                }
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: CommodityDeModel = StringNullAdapter.gson.fromJson(result)
                    textName.text = model.model.name + model.model.no_number
                    var price = ""
                    if (model.model.price == "0.00") {
                        price = model.model.integral + "积分"
                    } else if (model.model.integral == "0") {
                        price = mContext.resources.getString(R.string.CNY) + model.model.price
                    } else {
                        price = mContext.resources.getString(R.string.CNY) + model.model.price + "+" + model.model.integral + "积分"
                    }
                    textPrice.text = price
                    textKucun.text = "库存：${model.model.surplus_num}"
                    tv_companyname.text = model.model.company_name
                    banner.setImageLoader(GlideImageLoader())
                    banner.setImages(model.model.imageList)
                    banner.start()
                    loadWebView(model.model.detail)
                    textShare.setOnClickListener {
                        //                        share()//TODO：分享
                    }
                    textBtnSub.setOnClickListener {
                        if (number > 1) {
                            number--
                        }
                        editNumber.setText(number.toString())
                    }
                    textBtnAdd.setOnClickListener {
                        if (number < 99) {
                            number++
                        }
                        editNumber.setText(number.toString())
                    }
                    editNumber.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            var input = editNumber.text.toString()
                            if (TextUtils.isEmpty(input) || !TextUtils.isPositiveInt(input)) {
                                ToastUtil.showShort("请输入正确的数量")
                            } else {
                                number = input.toInt()
                            }
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        }
                    })
                    textAddShopcar.setOnClickListener {
                        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                            ToastUtil.showShort("请先登录")
                            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                            return@setOnClickListener
                        }
                        addToShopCar()
                    }
                    textBuy.setOnClickListener {
                        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                            ToastUtil.showShort("请先登录")
                            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                            return@setOnClickListener
                        }
                        var b = Bundle()
                        b.putSerializable("model", model.model)
                        b.putInt("num", number)
                        ActivityUtils.openActivity(mContext, ConfirmExchangeActivity::class.java, b)
                    }
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
     * 加入购物车
     */
    private fun addToShopCar() {
        if (TextUtils.isEmpty(id)) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.JOIN_SHOP_CART)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                if (TextUtils.isNotEmpty(id)) {
                    "id" - id
                }
                "number" - number.toString()
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
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

    private fun loadWebView(url: String) {
        webview!!.settings.pluginState = WebSettings.PluginState.ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview!!.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        //声明WebSettings子类
        val webSettings = webview!!.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN //自适应
        //支持插件
        // webSettings.setPluginsEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(false) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        webview!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                llDetail.requestLayout()
            }
        }

        //
        //        if (textXMode) {
        //            getText()
        //        } else if (!Common.empty(url)) {
        //            textTitle.text = "关于我们"
        //            webview!!.loadData(url, "text/html", "UTF-8")
        webview!!.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null)
    }

    override fun getIntentData() {
        mode = intent.extras?.getInt("mode", MODE_HOME) ?: MODE_HOME
        id = intent.extras?.getString("id", "") ?: ""
    }

    override fun clear() {

    }

}