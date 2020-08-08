package com.rice.bohai.model

import java.io.Serializable

data class PintuanProduceListModel(
        var lists: MutableList<PintuanProduceModel> = ArrayList()
) : Serializable