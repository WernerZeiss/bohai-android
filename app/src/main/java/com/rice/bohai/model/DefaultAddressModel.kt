package com.rice.bohai.model

import java.io.Serializable

data class DefaultAddressModel(
        var address: AddressModel = AddressModel()
) : Serializable