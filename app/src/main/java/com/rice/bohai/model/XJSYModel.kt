package com.rice.bohai.model

import java.io.Serializable

data class XJSYModel(
        var created_at: String = "",
        var id: Int = 0,
        var name: String = "",
        var price: String = "",
        var show_price: String = "",
        var source_user_id: Int = 0,
        var type: Int = 0,
        var type_name: String = "",
        var updated_at: String = "",
        var user_id: Int = 0,
        var user_name: String = ""
) : Serializable