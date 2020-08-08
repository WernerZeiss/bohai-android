package com.rice.bohai.model

import java.io.Serializable

data class SellPromptModel(
        var model: Model = Model()
) : Serializable {
    data class Model(
            var content: String = "",
            var id: String = "",
            var intro: String = "",
            var is_read: Int = 0,
            var name: String = "",
            var user_id: String = ""
    ) : Serializable
}