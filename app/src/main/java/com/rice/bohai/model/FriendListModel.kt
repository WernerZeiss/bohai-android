package com.rice.bohai.model

import java.io.Serializable

data class FriendListModel(
    var lists: MutableList<FriendModel> = ArrayList(),
    var total_count: String = ""
) : Serializable