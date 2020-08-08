package com.rice.bohai.model

import com.rice.aobo.model.ChubeiRankModel
import java.io.Serializable

data class ChubeiRankListModel(
    var month: String = "",
    var list: MutableList<ChubeiRankModel> = ArrayList()
) : Serializable