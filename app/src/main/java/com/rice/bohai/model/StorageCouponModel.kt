package com.rice.bohai.model

import java.io.Serializable

/**
 * @author CWQ
 * @date 2020/10/27
 */
data class StorageCouponModel(
    var id: String = "",
    var price: String = "",
    var type: Int = 0,
    var user_id: String = "",
    var get_status: Int = 0,
    var source_id: String = "",
    var created_at: String = "",
    var updated_at: String = "",
    var type_name: String = "",
    var add_type: Int = 0
) : Serializable