package com.rice.bohai.model

import java.io.Serializable

data class TranslateModel(
    var AcceptStation: String = "",
    var AcceptTime: String = ""
) : Serializable