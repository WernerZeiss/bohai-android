package com.rice.bohai.model

import java.io.Serializable

data class IntegralListModel(
    var lists: MutableList<CommodityModel> = ArrayList()
) : Serializable