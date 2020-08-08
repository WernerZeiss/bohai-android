package com.rice.bohai.model

import java.io.Serializable

data class PintuanMxModel(
    var id: String = "",
    var order_number: String = "",
    var group_order_id: String = "",
    var product_id: String = "",
    var user_id: String = "",
    var status: String = "",
    var settlement_status: String = "",
    var created_at: String = "",
    var updated_at: String = "",
    var name: String = ""
) : Serializable