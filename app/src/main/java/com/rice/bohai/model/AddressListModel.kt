package com.rice.bohai.model

import java.io.Serializable

data class AddressListModel(
        var addressList: MutableList<AddressModel> = ArrayList()
) : Serializable