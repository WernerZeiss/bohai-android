package com.rice.bohai.model

import java.io.Serializable

data class PYHistoryListModel(
        var lists: MutableList<PYHistoryModel> = ArrayList()
) : Serializable