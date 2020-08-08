package com.rice.bohai.model

import java.io.Serializable

data class ShopcarModel(
        var created_at: String = "",
        var id: Int = 0,
        var integral_num: String = "", //积分
        var product_name: String = "", //名称
        var image: String = "", //图片
        var integral_product_id: Int = 0,
        var number: Int = 0, //数量
        var price: String = "", //价格
        var updated_at: String = "",
        var unit: String = "", //单位
        var user_id: Int = 0,
        var isChecked: Boolean = false
) : Serializable