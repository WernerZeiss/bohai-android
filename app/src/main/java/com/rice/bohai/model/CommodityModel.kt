package com.rice.bohai.model

import java.io.Serializable

data class CommodityModel(
    var created_at: String = "",
    var detail: String = "",
    var id: Int = 0,
    var image: String = "",
    var imageList: List<String> = listOf(),
    var name: String = "",
    var surplus_num: String = "",//剩余库存
    var number: String = "",//库存
    var no_number: String = "",
    var order_num: String = "",
    var price: String = "", //金额
    var buy_total_price: String = "", //买入价格
    var integral: String = "", //积分
    var is_signature: Int = 0, //是否需要签名
    var sign_agreement: String = "", //签名协议
    var product_type_id: Int = 0,
    var product_type_name: String = "",
    var status_name: String = "",
    var transaction_number: String = "",
    var unit: String = "",//数量单位
    var updated_at: String = "",
    var weight: String = "",
    var day_trade_volume: String = "",
    var is_address: String = "",//是否需要地址
    var already_exchange_num: Int = 0,//已兑换数量
    var company_name: String = ""
) : Serializable