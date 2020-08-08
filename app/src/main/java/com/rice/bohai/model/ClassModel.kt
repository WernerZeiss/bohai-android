package com.rice.bohai.model

import java.io.Serializable

data class ClassModel(
    var id: Int = 0,
    var sid: String = "",
    var name: String = "",
    var isChecked: Boolean = false
) : Serializable