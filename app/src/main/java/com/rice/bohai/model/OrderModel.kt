package com.rice.bohai.model

import java.io.Serializable

data class OrderModel(
        var address_id: Int = 0,
        var created_at: String = "",
        var fact_product_price: String = "",
        var finished_at: String = "", //签收时间
        var good_status: Int = 0, //1待发货，2已发货，3已完成
        var id: Int = 0,
        var is_remind: Int = 0,
        var is_return: Int = 0,
        var is_return_locking: Int = 0,
        var logistic_number: String = "", //物流编号
        var logistic_time: String = "",
        var money_payed: String = "",
        var order_info: OrderInfo = OrderInfo(),
        var order_number: String = "", //订单号
        var payed_at: String = "",
        var price: String = "",
        var remark: String = "",
        var shipper_id: Int = 0,
        var shipper_name: String = "", //物流公司名称
        var status: Int = 0, //状态 1待支付 2已支付
        var status_name: String = "", //状态文本
        var total_integral: String = "",
        var type: Int = 0,
        var type_name: String = "",
        var user_id: Int = 0,
        var wx_prepay_id: String = "",
        var shipper_result: MutableList<TranslateModel> = ArrayList()
) : Serializable {
    data class OrderInfo( //订单信息
            var address_address: String = "", //完整收货地址
            var address_mobile: String = "", //收货手机号
            var address_name: String = "", //收件人
            var product_info: MutableList<ProductInfo> = ArrayList()
    ) : Serializable {
        data class ProductInfo( //商品信息
                var image: String = "",
                var integral: String = "",
                var number: Int = 0,
                var price: String = "",
                var product_name: String = "", //产品名称
                var source_id: Int = 0,
                var unit: String = ""
        ) : Serializable
    }
}