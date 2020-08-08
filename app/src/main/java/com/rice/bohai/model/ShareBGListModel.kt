package com.rice.bohai.model

import java.io.Serializable

data class ShareBGListModel(
        var lists: MutableList<ShareBGModel> = ArrayList(),
        var qrcode_src: String = ""
) : Serializable