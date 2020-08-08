package com.rice.bohai.model

import com.android.volley.ServerError
import java.io.Serializable

data class WXPayModel(
        var appid: String = "",
        var noncestr: String = "",
        var `package`: String = "",
        var partnerid: String = "",
        var prepayid: String = "",
        var sign: String = "",
        var timestamp: String = ""
) : Serializable