package com.rice.bohai.model

import java.io.Serializable

data class BigClassModel(
    var created_at: String = "",
    var id: Int = 0,
    var is_delete: Int = 0,
    var name: String = "",
    var order_num: Int = 0,
    var updated_at: String = "",
    var isChecked: Boolean = false
) : Serializable