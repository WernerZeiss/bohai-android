package com.rice.bohai.model

import java.io.Serializable

data class PriceListModel(
        var lists: MutableList<PriceModel> = ArrayList(),
        var shipmentList: MutableList<NewPriceModel> = ArrayList(),
        var total_number: String = "",
        var position_id: String = ""
) : Serializable