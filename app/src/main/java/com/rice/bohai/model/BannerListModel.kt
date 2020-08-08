package com.rice.bohai.model

import java.io.Serializable

data class BannerListModel(
        var lists: MutableList<BannerModel> = ArrayList()
) : Serializable