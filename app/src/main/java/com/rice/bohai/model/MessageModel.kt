package com.rice.bohai.model

import java.io.Serializable

data class MessageModel(
        var content: String = "",
        var created_at: String = "",
        var id: Int = 0,
        var intro: String = "",
        var is_read: Int = 0,
        var name: String = "",
        var updated_at: String = "",
        var user_id: Int = 0
) : Serializable