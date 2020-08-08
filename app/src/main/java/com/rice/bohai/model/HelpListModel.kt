package com.rice.bohai.model

import java.io.Serializable

data class HelpListModel(
        var lists: MutableList<HelpModel> = ArrayList()
) : Serializable