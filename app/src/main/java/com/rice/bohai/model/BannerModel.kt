package com.rice.bohai.model

import java.io.Serializable

data class BannerModel(
        var content: String = "",
        var created_at: String = "",
        var href_id: String = "",
        var id: Int = 0,
        var image: String = "",
        var order_num: Int = 0,
        var status: Int = 0,
        var title: String = "",
        var type: String = "",
        var updated_at: String = ""
) : Serializable