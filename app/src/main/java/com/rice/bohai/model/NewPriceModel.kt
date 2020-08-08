package com.rice.bohai.model

import java.io.Serializable

data class NewPriceModel(
    var created_at: String = "",
    var day: String = "",
    var end_at: String = "",
    var id: Int = 0,
    var is_invalid: Int = 0,
    var number: Int = 0,
    var product_id: Int = 0,
    var product_price: String = "",
    var product_name: String = "",
    var product_no_number: String = "",
    var surplus_num: String = "",
    var show_time: String = "",
    var start_at: String = "",
    var updated_at: String = "",
    var use_num: Int = 0
) : Serializable