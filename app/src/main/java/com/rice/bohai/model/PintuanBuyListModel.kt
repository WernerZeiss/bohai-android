package com.rice.bohai.model

import java.io.Serializable

data class PintuanBuyListModel(
        var lists: MutableList<PintuanBuyModel> = ArrayList()
) : Serializable