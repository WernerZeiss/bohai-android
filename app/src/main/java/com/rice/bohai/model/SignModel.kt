package com.rice.bohai.model

import java.io.Serializable

data class SignModel(
    var is_sign: Int = 0,//是否签署了协议，0表示未注册，1表示未签署，2表示已签署
    var sign_url: String = ""
) : Serializable