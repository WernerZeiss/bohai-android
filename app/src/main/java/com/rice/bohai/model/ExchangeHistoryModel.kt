package com.rice.bohai.model

import java.io.Serializable

data class ExchangeHistoryModel(
        var created_at: String = "",
        var id: Int = 0,
        var image: String = "",
        var integral_product_id: Int = 0,
        var number: Int = 0,
        var product_name: String = "",
        var order_number: String = "",
        var integral_num: String = "",
        var user_id: Int = 0
) : Serializable