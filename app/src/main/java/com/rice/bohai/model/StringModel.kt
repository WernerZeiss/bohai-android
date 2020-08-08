package com.rice.bohai.model

import java.io.Serializable

data class StringModel(
        var code: Int = 0,
        var `data`: String = "",
        var message: String = "",
        var request_time: String = ""
) : Serializable