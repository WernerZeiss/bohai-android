package com.rice.bohai.model

import java.io.Serializable

data class THJEModel(
    var created_at: String = "",
    var id: Int = 0,
    var price: String = "",
    var product_id: Int = 0,
    var product_name: String = "",
    var product_no_number: String = "",
    var updated_at: String = "",
    var user_id: Int = 0,
    var money: String = "",
    var type: Int = 0,
    var source_id: String = "",
    var name: String = ""
) : Serializable