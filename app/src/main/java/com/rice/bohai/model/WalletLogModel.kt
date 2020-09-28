package com.rice.bohai.model

import java.io.Serializable

/**
 * @author CWQ
 * @date 2020/9/26
 */
data class WalletLogModel(
    var id: String = "",
    var price: String = "",
    var type: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var get_status: Int = 0,
    var user_id: String = "",
    var source_user_id: String = "",
    var source_id: String = "",
    var is_can_check: Int = 0
) : Serializable