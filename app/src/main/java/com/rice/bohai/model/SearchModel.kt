package com.rice.bohai.model

import java.io.Serializable

data class SearchModel(
    var id: Int = 0,
    var name: String = "",
    var code: String = "",
    var price: String = "",
    var perday: String = ""
) : Serializable