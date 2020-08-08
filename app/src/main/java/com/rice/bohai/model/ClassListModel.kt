package com.rice.bohai.model

import java.io.Serializable

data class ClassListModel(
    var lists: MutableList<ClassModel> = ArrayList()
) : Serializable