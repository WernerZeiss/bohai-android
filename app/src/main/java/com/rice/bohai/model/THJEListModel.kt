package com.rice.bohai.model

import java.io.Serializable

data class THJEListModel(
        var lists: MutableList<THJEModel> = ArrayList()
) : Serializable