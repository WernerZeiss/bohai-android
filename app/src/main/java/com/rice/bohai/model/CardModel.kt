package com.rice.bohai.model

/**
 * @author CWQ
 * @date 2020/8/8
 */
data class CardModel(
    var id: String = "",
    var user_id: String = "",
    var bank_id: String = "",
    var bank_number: String = "",
    var bank_mobile: String = "",
    var bank_truename: String = "",
    var id_number: String = "",
    var is_default: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var name: String = "",
    var bank_logo: String = ""
)