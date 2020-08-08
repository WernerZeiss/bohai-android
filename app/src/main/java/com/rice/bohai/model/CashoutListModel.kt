package com.rice.bohai.model

import java.io.Serializable

data class CashoutListModel(
        var lists: MutableList<CashoutModel> = ArrayList(),
        var total_price: String = ""
) : Serializable