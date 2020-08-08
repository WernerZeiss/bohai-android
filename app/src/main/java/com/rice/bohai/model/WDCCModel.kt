package com.rice.bohai.model

import java.io.Serializable

data class WDCCModel(
    var buy_at: String = "",
    var buy_price: String = "",
    var can_sale: Int = 0,
    var created_at: String = "",
    var end_at: String = "",
    var id: Int = 0,
    var is_lock: Int = 0,
    var market_value: String = "",
    var number: Int = 0,
    var price: String = "",
    var buy_total_price: String = "",
    var product_id: Int = 0,
    var product_name: String = "",
    var order_number: String = "",
    var product_no_number: String = "",
    var exchange_integral_num: String = "",
    var start_at: String = "",
    var status: Int = 0,
    var status_name: String = "",
    var total_number: Int = 0,
    var updated_at: String = "",
    var user_display_name: String = "",
    var user_id: Int = 0,
    var user_mobile: String = ""
) : Serializable