package com.rice.bohai.model

import java.io.Serializable

data class PintuanOrderModel(
    var id: String = "",
    var order_number: String = "",
    var group_order_id: String = "",
    var product_id: String = "",
    var price: String = "",
    var user_id: String = "",
    var is_group_order: String = "",
    var settlement_status: String = "",
    var created_at: String = "",
    var updated_at: String = "",
    var is_sync_order: String = "",
    var status: String = "",
    var a_order_number: String = "",
    var name: String = ""
) : Serializable