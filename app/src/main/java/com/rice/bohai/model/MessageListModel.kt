package com.rice.bohai.model

import java.io.Serializable

data class MessageListModel(
        var lists: MutableList<MessageModel> = ArrayList()
) : Serializable