package com.rice.bohai.model

import java.io.Serializable

data class ShopcarListModel(
        var lists: MutableList<ShopcarModel> = ArrayList()
) : Serializable