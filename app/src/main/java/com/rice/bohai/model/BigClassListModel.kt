package com.rice.bohai.model

import java.io.Serializable

data class BigClassListModel(
    var lists: MutableList<BigClassModel> = ArrayList()
) : Serializable