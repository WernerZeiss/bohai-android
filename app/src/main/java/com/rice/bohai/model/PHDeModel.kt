package com.rice.bohai.model

import java.io.Serializable

data class PHDeModel(
        var created_at: String = "",
        var id: Int = 0,
        var profit_num: String = "",
        var show_profit_num: String = "",
        var product_name: String = "",
        var market_value: String = "",
        var type: Int = 0,
        var type_name: String = "",
        var product_unit: String = "",
        var updated_at: String = "",
        var user_name: String = ""
) : Serializable