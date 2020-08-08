package com.rice.bohai.model

import java.io.Serializable

data class NotifyListModel(
    var lists: MutableList<NotifyModel> = ArrayList()
) : Serializable