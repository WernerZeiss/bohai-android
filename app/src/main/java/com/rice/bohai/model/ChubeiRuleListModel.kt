package com.rice.bohai.model

import java.io.Serializable

data class ChubeiRuleListModel(
    var rules_list: MutableList<ChubeiRuleModel> = ArrayList(),
    var agreement: String = "",
    var private_agreement: String = ""
) : Serializable