package com.rice.bohai.model

import java.io.Serializable

data class SystemModel(
    var help_center: String = "", //帮助中心
    var order_conversion_integral: String = "", //订单转化率
    var price_conversion_integral: String = "", //现金转化率
    var service_qq: String = "", //客服QQ
    var sign_agreement: String = "", //转让签名协议
    var buy_sign_agreement: String = "", //购买签名协议
    var user_rule: String = "",//交易规则
    var certificate_pdf: String = "", //数字证书协议
    var is_start_wx_pay: String = "1",//是否开启微信支付
    var is_start_ali_pay: String = "1"//是否开启支付宝支付
) : Serializable