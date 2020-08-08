package com.rice.racar.web

import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import com.rice.tool.ToastUtil

class PublicModel {
    //公共静态方法
    companion object {
        fun forjson(data: String): data {
//            Logger.json(data)
            try {
                var red = JsonParser().parse(data).asJsonObject
                var model = data()
                model.code = red.get("code").asInt
                if (model.code == 500) {
                    ToastUtil.showShort("服务器繁忙")
                    return data()
                }
                model.message = red.get("message").asString
                model.data = red.get("data").toString()
                return model
            } catch (e: Exception) {
                Logger.d(e)
                ToastUtil.showShort("服务器繁忙")
                return data()
            }
            return data()
        }

    }

    data class data(
        var code: Int = 0,
        var message: String = "",
        var request_time: String = "",
        var data: String = ""
    )


}