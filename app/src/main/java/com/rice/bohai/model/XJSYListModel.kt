package com.rice.bohai.model

import java.io.Serializable

data class XJSYListModel(
        var lists: MutableList<XJSYModel> = ArrayList()
) : Serializable