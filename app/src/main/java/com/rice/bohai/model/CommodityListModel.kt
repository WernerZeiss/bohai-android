package com.rice.bohai.model

import java.io.Serializable

data class CommodityListModel(
        var lists: MutableList<CommodityModel> = ArrayList()
) : Serializable