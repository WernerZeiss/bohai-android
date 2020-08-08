package com.rice.bohai.model

import java.io.Serializable

/**
 * 销售配货记录
 */
data class ProfitHistoryListModel(
        var lists: MutableList<ProfitHistoryModel> = ArrayList()
) : Serializable