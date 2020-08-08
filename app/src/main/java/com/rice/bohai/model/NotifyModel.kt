package com.rice.bohai.model

import java.io.Serializable

data class NotifyModel(
    var content: String = "",
    var created_at: String = "",
    var id: Int = 0,
    var intro: String = "",
    var name: String = "",
    var time: String = "",
    var view_num: String = "",
    var updated_at: String = ""
) : Serializable