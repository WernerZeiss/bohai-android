package com.rice.bohai.model

import java.io.Serializable

data class PayInfoModel(
        var is_pay: Int = 0,
        var order_number: String = "",
        var price: String = ""
) : Serializable