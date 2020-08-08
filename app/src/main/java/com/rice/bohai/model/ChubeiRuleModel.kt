package com.rice.bohai.model

import java.io.Serializable

data class ChubeiRuleModel(
    var title: String = "",
    var rules: List<String> = listOf()
) : Serializable