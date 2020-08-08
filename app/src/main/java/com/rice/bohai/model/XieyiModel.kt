package com.rice.bohai.model

import java.io.Serializable

data class XieyiModel(
        var contractId: String = "",
        var created_at: String = "",
        var id: Int = 0,
        var image: String = "",
        var is_sign: Int = 0,
        var name: String = "",
        var type: Int = 0,
        var user_id: Int = 0,
        var show_name: String = "",
        var sign_url: String = "" //协议地址
) : Serializable