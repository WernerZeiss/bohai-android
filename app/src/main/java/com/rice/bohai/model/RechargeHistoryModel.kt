package com.rice.bohai.model

import java.io.Serializable

data class RechargeHistoryModel(
    var created_at: String = "",
    var id: Int = 0,
    var price: String = "",
    var status: String = "",
    var type_name: String = "",
    var user_id: Int = 0,
    var user_mobile: String = "",
    var user_name: String = ""
) : Serializable