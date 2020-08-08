package com.rice.bohai.model

import java.io.Serializable

data class WDCCListModel(
    var lists: MutableList<WDCCModel> = ArrayList(),
    var now_basic_income: String = ""//提货值
) : Serializable