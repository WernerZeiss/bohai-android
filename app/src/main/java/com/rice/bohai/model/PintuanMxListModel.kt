package com.rice.bohai.model

import java.io.Serializable

data class PintuanMxListModel(
        var lists: MutableList<PintuanMxModel> = ArrayList()
) : Serializable