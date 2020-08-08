package com.rice.bohai.model

import java.io.Serializable

data class CashoutModel(
        var bank_name: String = "",
        var bank_number: String = "",
        var bank_username: String = "",
        var created_at: String = "",
        var id: Int = 0,
        var is_published: Int = 0,
        var is_published_name: String = "",
        var order_number: String = "",
        var payed_at: String = "",
        var price: String = "",
        var show_bank_username: String = "",
        var updated_at: String = "",
        var user_id: Int = 0,
        var user_mobile: String = "",
        var user_nickname: String = ""
) : Serializable