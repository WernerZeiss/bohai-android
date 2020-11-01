package com.rice.bohai.model

import java.io.Serializable

/**
 * @author CWQ
 * @date 2020/10/27
 */
data class CouponModel(
    var is_load: Int = 0,
    var total_page: Int = 0,
    var total_count: Int = 0,
    var group_wallet_money: String = "",
    var group_need_wallet_money: String = "",
    var need_wallet_money: String = "",
    var storage_ticket:String = "",
    var lists: MutableList<StorageCouponModel>
) : Serializable