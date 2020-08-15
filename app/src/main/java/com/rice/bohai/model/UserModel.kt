package com.rice.bohai.model

import java.io.Serializable

data class UserModel(
    var access_token: String = "",
    var avatar: String = "",
    var bank_mobile: String = "",
    var bank_name: String = "",
    var bank_number: String = "", //银行卡号
    var id_number: String = "", //身份证号
    var bank_id: String = "", //银行ID
    var bank_username: String = "",
    var id_name: String = "", //绑定银行卡姓名
    var city: String = "",
    var display_name: String = "",
    var id: String = "",
    var invite_code: String = "", //邀请码
    var province: String = "",
    var subbranch: String = "",
    var integral_num: String = "", //积分账户
    var total_market_value: String = "", //账户市值
    var price: String = "", //现金账户
    var total_profit_price: String = "", //销售配货
    var ticket_money:String = "",//储备券
    var total_settlement_price: String = "", //提货金额
    var is_transaction_pass: Int = 0, //是否设置过交易密码
    var is_signature: Int = 0, //是否签过转让签名
    var is_buy_signature: Int = 0, //是否签过购买签名
    var is_start_recharge: Int = 0, //是否开启充值入口，1表示开启
    var is_valid: Int = 0, //是否进行了实名认证
    var user_phone: String = "",
    var member_id: String = "", //等级
    var member_name: String = "", //等级名称
    var now_basic_income: String = "", //提货值
    var is_start_register: Int = 0, //是否开放邀请好友入口
    var is_effective_user_name: String = "", //账户状态
    var is_address: String = "", //是否填写了地址
    var is_no_read_message: Int = 0, //是否有未读消息
    var is_sign_task: Int = 0, //是否同意了上上签协议
    var p_open_auto_group: Int = 0,
    var agreement_doc:String = ""//数字证书协议
) : Serializable