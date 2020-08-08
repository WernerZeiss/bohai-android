package com.rice.bohai.model

import com.rice.bohai.MyApplication
import java.io.Serializable

data class PasswordModel(
    var username: String = "",
    var password: String = ""
) : Serializable {
    fun isChecked(): Boolean {
        return MyApplication.instance.userInfo?.user_phone == username
    }
}