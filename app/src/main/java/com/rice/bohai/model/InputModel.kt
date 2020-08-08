package com.rice.bohai.model

import java.io.Serializable

data class InputModel(
    var parameName: String = "",
    var tag: String = "",
    var hint: String = "",
    var text: String = "",
    var mode: Int = MODE_EDIT,
    var editAble: Boolean = true
) : Serializable {
    companion object {
        const val MODE_EDIT = 0
        const val MODE_TEXT = 1
    }
}