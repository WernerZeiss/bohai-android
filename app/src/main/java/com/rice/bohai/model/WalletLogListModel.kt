package com.rice.bohai.model

import java.io.Serializable

/**
 * @author CWQ
 * @date 2020/9/26
 */
data class WalletLogListModel(
    var is_load: Int = 0,
    var total_page: Int = 0,
    var total_count: Int = 0,
    var need_wallet_money: String = "",
    var lists: MutableList<WalletLogModel> = ArrayList()
) : Serializable