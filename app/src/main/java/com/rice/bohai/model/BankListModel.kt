package com.rice.aobo.model

import java.io.Serializable

data class BankListModel(
    var lists: MutableList<BankModel> = ArrayList()
) : Serializable