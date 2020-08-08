package com.rice.bohai.model

import java.io.Serializable

data class XieyiListModel(
        var lists: MutableList<XieyiModel> = ArrayList()
) : Serializable