package com.rice.bohai.activity

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.aobo.model.BankListModel
import com.rice.aobo.model.BankModel
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.dialog.RLoadingDialog
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_add_bank_card.*
import java.nio.charset.Charset

/**
 * @author CWQ
 * @date 2020/8/8
 * 添加银行卡
 */
class AddBankCardActivity : RiceBaseActivity() {

    lateinit var loadingDialog: RLoadingDialog
    private var selectedBank: BankModel? = null
    var bankList: MutableList<BankModel> = ArrayList()
    var bankNameList: MutableList<String> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.activity_add_bank_card
    }

    override fun initView() {
        loadingDialog = RLoadingDialog(mContext, true)

        tv_submit.setOnClickListener {
            //提交
            if (TextUtils.isEmpty(et_name.text.toString())) {
                ToastUtil.showShort("请输入姓名~")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(et_card_number.text.toString())) {
                ToastUtil.showShort("请输入银行卡号~")
                return@setOnClickListener
            }
            if (selectedBank == null) {
                ToastUtil.showShort("请选择银行~")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(et_idcard.text.toString())) {
                ToastUtil.showShort("请输入身份证号码~")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(et_phone.text.toString())) {
                ToastUtil.showShort("请输入手机号~")
                return@setOnClickListener
            }
            addBankCard()
        }
    }


    override fun getIntentData() {
        getBankList()
    }


    //获取银行列表
    private fun getBankList() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.BANK_LIST)
            onSuccess { byts ->
                val result = RiceHttpK.getResult(mContext, byts)
                if (com.rice.tool.TextUtils.isNotEmpty(result)) {
                    val model: BankListModel = StringNullAdapter.gson.fromJson(result)
                    bankNameList.clear()
                    for (item in model.lists) {
                        bankNameList.add(item.name)
                    }
                    bankList.clear()
                    bankList.addAll(model.lists)


                    val spinnerAdapter = ArrayAdapter<String>(
                        this@AddBankCardActivity,
                        R.layout.item_spinner_bank,
                        bankNameList
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = spinnerAdapter
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedBank = bankList[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

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

    private fun addBankCard() {
        val name = et_name.text.toString()
        val cardNumber = et_card_number.text.toString()
        val idcard = et_idcard.text.toString()
        val phone = et_phone.text.toString()

        Http.post {
            url = RiceHttpK.getUrl(Constant.BIND_BANK)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "bank_truename" - name
                "bank_number" - cardNumber
                "id_number" - idcard
                "bank_mobile" - phone
                "bank_id" - selectedBank?.id.toString()
            }
            onStart {
                if (!loadingDialog.isShowing) {
                    loadingDialog.show()
                }
            }
            onFinish {
                loadingDialog.dismiss()
            }
            onSuccess {bytes ->
                val result = RiceHttpK.getResult(mContext, bytes)
                Log.e("AddBankCard",result)
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    finish()
                }
            }
            onFail {error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    override fun clear() {

    }
}