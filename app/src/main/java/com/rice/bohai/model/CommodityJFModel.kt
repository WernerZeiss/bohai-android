package com.rice.bohai.model

import java.io.Serializable

data class CommodityJFModel(
        var already_exchange_num: Int = 0,
        var created_at: String = "",
        var detail: String = "",
        var id: Int = 0,
        var image: String = "",
        var imageList: MutableList<String> = ArrayList(),
        var integral: String = "",
        var is_delete: Int = 0,
        var name: String = "",
        var no_number: String = "",
        var number: Int = 0,
        var order_num: Int = 0,
        var price: String = "",
        var product_type_id: Int = 0,
        var product_type_name: String = "",
        var status: Int = 0,
        var status_name: String = "",
        var surplus_num: String = "",
        var updated_at: String = "",
        var weight: String = ""
) : Serializable