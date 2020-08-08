package com.rice.aobo.model

import java.io.Serializable

data class ChubeiRankModel(
    var mobile: String = "",
    var percent: String = "",
    var logo: String = "",
    var index: Int = 0
) : Serializable