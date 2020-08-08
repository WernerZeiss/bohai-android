package com.rice.bohai.model

/**
 * 首次购买
 */
data class FirstBuyModel(
    var is_first_order: Int = 0,
    var tip: String = ""
)