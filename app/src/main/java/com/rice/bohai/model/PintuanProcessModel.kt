package com.rice.bohai.model

import java.io.Serializable

//拼团进度
data class PintuanProcessModel(
    var total_winning: String = "",
    var percent: Int = 0
) : Serializable