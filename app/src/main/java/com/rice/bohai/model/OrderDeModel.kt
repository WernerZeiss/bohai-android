package com.rice.bohai.model

import java.io.Serializable

data class OrderDeModel(
        var order: OrderModel = OrderModel()
) : Serializable