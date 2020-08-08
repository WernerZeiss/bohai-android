package com.rice.bohai.model

import java.io.Serializable

data class HelpModel(
        var created_at: String = "",
        var detail: String = "",
        var id: Int = 0,
        var title: String = "",
        var updated_at: String = ""
) : Serializable