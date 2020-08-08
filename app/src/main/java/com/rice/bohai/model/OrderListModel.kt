package com.rice.bohai.model

import java.io.Serializable

data class OrderListModel(
        var lists: MutableList<OrderModel> = ArrayList()
) : Serializable