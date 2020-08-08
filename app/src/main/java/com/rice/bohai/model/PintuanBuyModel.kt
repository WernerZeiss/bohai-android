package com.rice.bohai.model

import java.io.Serializable

data class PintuanBuyModel(
    var id: String = "",
    var order_number: String = "",
    var product_id: String = "",
    var created_at: String = "",
    var updated_at: String = "",
    var name: String = ""
) : Serializable