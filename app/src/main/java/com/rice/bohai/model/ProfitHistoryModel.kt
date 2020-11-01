package com.rice.bohai.model

import java.io.Serializable

/**
 * 销售配货记录
 */
data class ProfitHistoryModel(
        var created_at: String = "",
        var end_at: String = "",
        var buy_at: String = "",
        var fight_percent: String = "", //转让进度，状态为2才显示
        var id: Int = 0,
        var market_value: String = "", //市值
        var price: String = "",
        var product_id: Int = 0,
        var product_name: String = "", //产品名称
        var product_no_number: String = "", //产品编号
        var exchange_integral_num: String = "", //可兑换积分价值
        var profit_num: String = "", //配货数量，>1可兑换、提货，>0可转让。
        var start_at: String = "",
        var status: Int = 0, //状态，为1时可以点击转让
        //1持仓中，2拼团中，3已拼团，4提货中，5已提货，6兑换积分，7转让中，8已转让
        var status_name: String = "", //状态文本
        var updated_at: String = "",
        var user_id: Int = 0,
        var type_name:String = ""
) : Serializable