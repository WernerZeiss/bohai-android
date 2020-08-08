package com.rice.bohai.model

import java.io.Serializable

data class CommodityJFListModel(
        var lists: MutableList<CommodityJFModel> = ArrayList()
) : Serializable