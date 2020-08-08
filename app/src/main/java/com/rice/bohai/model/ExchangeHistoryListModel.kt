package com.rice.bohai.model

import java.io.Serializable

data class ExchangeHistoryListModel(
        var lists: MutableList<ExchangeHistoryModel> = ArrayList()
) : Serializable