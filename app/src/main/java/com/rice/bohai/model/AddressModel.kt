package com.rice.bohai.model

import java.io.Serializable

data class AddressModel(
        var city: String = "",
        var created_at: String = "",
        var district: String = "",
        var house_number: String = "",
        var id: Int = 0,
        var is_default: Int = 0,
        var mobile: String = "",
        var province: String = "",
        var realname: String = "",
        var updated_at: String = "",
        var user_id: Int = 0
) : Serializable