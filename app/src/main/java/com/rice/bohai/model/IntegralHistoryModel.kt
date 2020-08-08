package com.rice.bohai.model

import java.io.Serializable

/**
 * 兑换记录、积分记录、积分收益均为此Model
 */
data class IntegralHistoryModel(
        var created_at: String = "",
        var id: Int = 0,
        var integral: String = "",
        var show_integral: String = "",
        var show_name: String = "",
        var source_id: Int = 0,
        var source_user_id: Int = 0,
        var type: Int = 0,
        var type_name: String = "",
        var updated_at: String = "",
        var user_id: Int = 0
) : Serializable