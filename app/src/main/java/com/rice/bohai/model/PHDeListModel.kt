package com.rice.bohai.model

import java.io.Serializable

data class PHDeListModel(
        var lists: MutableList<PHDeModel> = ArrayList()
) : Serializable