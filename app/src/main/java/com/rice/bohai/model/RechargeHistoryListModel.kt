package com.rice.bohai.model

import java.io.Serializable

data class RechargeHistoryListModel(
    var lists: MutableList<RechargeHistoryModel> = ArrayList(),
    var total_price: String = ""
) : Serializable