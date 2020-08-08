package com.rice.bohai.model

import java.io.Serializable

data class ShareBGModel(
        var created_at: String = "",
        var id: Int = 0,
        var image: String = "",
        var is_delete: Int = 0,
        var order_num: Int = 0,
        var status: Int = 0,
        var updated_at: String = ""
) : Serializable