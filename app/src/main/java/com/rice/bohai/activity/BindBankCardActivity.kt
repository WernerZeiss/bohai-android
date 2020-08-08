package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.rice.aobo.model.BankListModel
import com.rice.aobo.model.BankModel
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.InputAdapter
import com.rice.bohai.dialog.TextSelectDialog
import com.rice.bohai.model.InputModel
import com.rice.dialog.CitySelecterDialog
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_bind_bank_card.*

@SuppressLint("Registered")
class BindBankCardActivity : RiceBaseActivity() {

    var mode = MODE_BANK_CARD
    var inputList: MutableList<InputModel> = ArrayList()
    lateinit var inputAdapter: InputAdapter
    lateinit var textSelectDialog: TextSelectDialog
    var bankList: MutableList<BankModel> = ArrayList()
    lateinit var loadingDialog: RLoadingDialog

    companion object {
        @JvmField
        val MODE_ALIPAY = 0
        val MODE_BANK_CARD = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bind_bank_card
    }

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)
        recycler.layoutManager = LinearLayoutManager(mContext)
        inputAdapter = InputAdapter(mContext, inputList)
        recycler.adapter = inputAdapter
        initMode()
    }

    private fun initMode() {
        when (mode) {
            MODE_ALIPAY -> {
                initAlipayMode()
            }
            MODE_BANK_CARD -> {
                initBankCardMode()
                //                if (MyApplication.instance.userInfo?.is_valid == 0) {
                //                    textBtnSubmit.visibility = View.VISIBLE
                //                } else {
                //                    textBtnSubmit.visibility = View.GONE
                //                }
            }
        }
        inputAdapter.notifyDataSetChanged()
    }

    /**
     * 初始化绑定银行卡
     */
    private fun initBankCardMode() {
        getBanks()
        textSelectDialog = TextSelectDialog(mContext)
        toolbar.setTitle("实名认证")
        inputList.clear()
        var model = InputModel(
                "id_name", "姓名", "请输入开户人姓名", MyApplication.instance.userInfo?.id_name
                ?: ""
        )
        inputList.add(model)
        model = InputModel(
                "bank_number", "银行卡号", "请输入您的银行卡号", MyApplication.instance.userInfo?.bank_number
                ?: ""
        )
        inputList.add(model)
        model = InputModel(
                "id_number", "身份证号码", "请输入您的身份证号码", MyApplication.instance.userInfo?.id_number
                ?: ""
        )
        inputList.add(model)
        model = InputModel(
                "bank_id", "银行", "请选择银行", MyApplication.instance.userInfo?.bank_id
                ?: "", InputModel.MODE_TEXT
        )
        inputList.add(model)
        model = InputModel(
                "subbranch", "支行名称", "请输入开户支行名称", MyApplication.instance.userInfo?.subbranch
                ?: ""
        )
        inputList.add(model)
        //        model = InputModel("province", "省份", "请输入开户行所在省份", "")
        //        inputList.add(model)
        model = InputModel(
                "city", "城市", "请选择开户行所在城市", (MyApplication.instance.userInfo?.province
                ?: "") + "-" + (MyApplication.instance.userInfo?.city ?: ""), InputModel.MODE_TEXT
        )
        inputList.add(model)
        model = InputModel("bank_mobile", "银行预留手机号", "请输入手机号", (MyApplication.instance.userInfo?.bank_mobile ?: ""))
        inputList.add(model)
        if (MyApplication.instance.userInfo?.is_valid == 1) {
            for (item in inputList) {
                item.mode = InputModel.MODE_TEXT
            }
            inputAdapter.notifyDataSetChanged()
            textBtnSubmit.text = "修改"
            textBtnSubmit.setOnClickListener {
                for (item in inputList) {
                    if (inputList.indexOf(item) == 3 || inputList.indexOf(item) == 5) {
                        item.mode = InputModel.MODE_TEXT
                    } else {
                        item.mode = InputModel.MODE_EDIT
                    }
                }
                inputAdapter.setOnItemChildClickListener { adapter, view, position ->
                    //            if (MyApplication.instance.userInfo?.is_valid == 0) {
                    when (position) {
                        3 -> {
                            //选择银行
                            textSelectDialog.onOkClickListener = object : TextSelectDialog.OnOkClickListener {
                                override fun onOkClick(str: String) {
                                    inputList[3].text = str
                                    inputAdapter.notifyItemChanged(3)
                                }
                            }
                            if (!textSelectDialog.isShowing) {
                                textSelectDialog.show()
                            }
                        }
                        5 -> {
                            //选择城市
                            CitySelecterDialog.setOnSelectedListener { provinceId, cityId, provinceName, cityName ->
                                inputList[5].text = "$provinceName-$cityName"
                                inputAdapter.notifyItemChanged(5)
                            }
                            CitySelecterDialog.getInstance(this)
                        }
                    }
                    //            }
                }
                inputAdapter.notifyDataSetChanged()
                textBtnSubmit.text = "提交"
                textBtnSubmit.setOnClickListener { bindBankCard() }
            }
        } else {
            textBtnSubmit.setOnClickListener { bindBankCard() }
            inputAdapter.setOnItemChildClickListener { adapter, view, position ->
                //            if (MyApplication.instance.userInfo?.is_valid == 0) {
                when (position) {
                    3 -> {
                        //选择银行
                        textSelectDialog.onOkClickListener = object : TextSelectDialog.OnOkClickListener {
                            override fun onOkClick(str: String) {
                                inputList[3].text = str
                                inputAdapter.notifyItemChanged(3)
                            }
                        }
                        if (!textSelectDialog.isShowing) {
                            textSelectDialog.show()
                        }
                    }
                    5 -> {
                        //选择城市
                        CitySelecterDialog.setOnSelectedListener { provinceId, cityId, provinceName, cityName ->
                            inputList[5].text = "$provinceName-$cityName"
                            inputAdapter.notifyItemChanged(5)
                        }
                        CitySelecterDialog.getInstance(this)
                    }
                }
                //            }
            }
        }
    }

    /**
     * 初始化绑定支付宝
     */
    private fun initAlipayMode() {
        inputList.clear()
        var model = InputModel("ali_name", "姓名", "请输入您的真实姓名", "")
        inputList.add(model)
        model = InputModel("ali_number", "支付宝账号", "请输入您的支付宝账号", "")
        inputList.add(model)
        //        textBtnSubmit.setOnClickListener { bindAliPay() }
    }

    /**
     * 加载银行列表
     */
    fun getBanks() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.BANK_LIST)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: BankListModel = StringNullAdapter.gson.fromJson(result)
                    var strList: MutableList<String> = ArrayList()
                    for (item in model.lists) {
                        strList.add(item.name)
                    }
                    bankList.clear()
                    bankList.addAll(model.lists)
                    textSelectDialog.setData(strList)
                    if (TextUtils.isNotEmpty(MyApplication.instance.userInfo?.bank_id)) {
                        for (item in bankList) {
                            if (item.id.toString() == MyApplication.instance.userInfo?.bank_id) {
                                inputList[3].text = item.name
                                inputAdapter.notifyItemChanged(3)
                            }
                        }
                    }
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 绑定银行卡
     */
    fun bindBankCard() {
        for (item in inputList) {
            if (TextUtils.isEmpty(item.text)) {
                ToastUtil.showShort(item.hint)
                return
            }
        }
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.BIND_BANK)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                for (item in inputList) {
                    if (item.parameName == "city") {
                        //取省市
                        "province" - item.text.substring(0, item.text.indexOf("-"))
                        "city" - item.text.substring(item.text.indexOf("-") + 1)
                    } else if (item.parameName == "bank_id") {
                        //取银行ID
                        for (itemBank in bankList) {
                            if (itemBank.name == item.text) {
                                "bank_id" - itemBank.id.toString()
                                break
                            }
                        }
                    } else {
                        //直接传输输入的字段
                        //                        if (MyApplication.instance.userInfo?.is_valid == 1) {
                        //                            if(item.parameName=="")
                        //                        }
                        item.parameName - item.text
                    }
                }
            }
            onStart {
                if (!loadingDialog.isShowing) {
                    loadingDialog.show()
                }
            }
            onFinish {
                loadingDialog.dismiss()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    setResult(Activity.RESULT_OK)
                    ToastUtil.showShort("实名认证提交成功")
                    MyApplication.instance.userInfo?.is_valid = 1
                    MyApplication.instance.getUserInfoFromWeb()
                    finish()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    //    /**
    //     * 绑定支付宝
    //     */
    //    fun bindAliPay() {
    //        var flag = false
    //        for (item in inputList) {
    //            if (TextUtils.isEmpty(item.text)) {
    //                ToastUtil.showShort(item.hint)
    //                flag = true
    //                return
    //            }
    //        }
    //        if (flag) {
    //            return
    //        }
    //        Http.post {
    //            url = RiceHttpK.getUrl(Constant.BIND_ALI)
    //            params {
    //                if (MyApplication.instance.userInfo != null) {
    //                    "access_token" - MyApplication.instance.userInfo!!.access_token
    //                } else {
    //                    ToastUtil.showShort("登录态失效，请重新登录")
    //                    var intent = Intent(MyApplication.instance, LoginActivity::class.java)
    //                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    //                    MyApplication.instance.startActivity(intent)
    //                }
    //                for (item in inputList) {
    //                    item.parameName - item.text
    //                }
    //            }
    //            onSuccess { byts ->
    //                val result = RiceHttpK.getResult(mContext,byts)
    //                if (TextUtils.isNotEmpty(result)) {
    //                    ToastUtil.showShort("提交成功")
    //                    for (item in inputList) {
    //                        if (item.parameName == "ali_name") {
    //                            MyApplication.instance.userInfo?.ali_name = item.text
    //                        }
    //                        if (item.parameName == "ali_number") {
    //                            MyApplication.instance.userInfo?.ali_number = item.text
    //                        }
    //                    }
    //                    var intent = Intent()
    //                    intent.putExtra("mode", mode)
    //                    setResult(Activity.RESULT_OK, intent)
    //                    finish()
    //                }
    //            }
    //            onFail { error ->
    //                 var message = error.message
    //                if((error.message?:"").contains("java")){
    //                    message = "未知错误"
    //                }
    //                ToastUtil.showShort(message)
    //            }
    //        }
    //    }

    override fun getIntentData() {
        if (intent?.extras != null) {
            mode = intent?.extras?.getInt("mode", MODE_BANK_CARD) ?: MODE_BANK_CARD
        }
    }

    override fun clear() {
        inputList.clear()
    }

}