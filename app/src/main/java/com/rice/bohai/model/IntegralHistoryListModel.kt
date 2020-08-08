package com.rice.bohai.model

import java.io.Serializable

data class IntegralHistoryListModel(
        var lists: MutableList<IntegralHistoryModel> = ArrayList()
) : Serializable