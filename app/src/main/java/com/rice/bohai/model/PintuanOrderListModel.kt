package com.rice.bohai.model

import java.io.Serializable

data class PintuanOrderListModel(
    var lists: MutableList<PintuanOrderModel> = ArrayList()
) : Serializable