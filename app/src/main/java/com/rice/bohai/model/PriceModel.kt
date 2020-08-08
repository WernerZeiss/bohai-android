package com.rice.bohai.model

import java.io.Serializable

data class PriceModel(
        var created_at: String = "",
        var id: Int = 0,
        var is_success: Int = 0,
        var number: Int = 0,
        var price: String = "",
        var product_id: Int = 0,
        var product_name: String = "",
        var product_no_number: String = "",
        var no_number: String = "",
        var profit_num: String = "",
        var sign_image: String = "",
        var source_id: Int = 0,
        var status: Int = 0,
        var status_name: String = "",
        var type: Int = 0,
        var updated_at: String = "",
        var user_id: Int = 0
) : Serializable