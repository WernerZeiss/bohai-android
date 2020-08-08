package com.rice.bohai.model

import java.io.Serializable

data class XJZHModel(
        var id: Int = 0,
        var name: String = "",
        var time: String = "",
        var score: String = "",
        var status: String = ""
) : Serializable