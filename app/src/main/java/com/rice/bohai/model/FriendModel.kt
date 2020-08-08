package com.rice.bohai.model

import java.io.Serializable

data class FriendModel(
        var address: String = "",
        var avatar: String = "",
        var bank_mobile: String = "",
        var bank_name: String = "",
        var bank_number: String = "",
        var bank_username: String = "",
        var city: String = "",
        var created_at: String = "",
        var id: Int = 0,
        var integral_num: Int = 0,
        var invite_code: String = "",
        var is_already_lock: Int = 0,
        var is_black: Int = 0,
        var is_effective_user: Int = 0,
        var is_sale: Int = 0,
        var last_login: String = "",
        var member_id: Int = 0,
        var mobile: String = "",
        var nickname: String = "",
        var price: String = "",
        var province: String = "",
        var show_name: String = "",
        var subbranch: String = "",
        var total_position_price: Int = 0,
        var transaction_password: String = "",
        var union_mobile: String = "",
        var union_name: String = "",
        var union_user_id: Int = 0,
        var updated_at: String = "",
        var attribute_name: String = "" //好友属性
) : Serializable