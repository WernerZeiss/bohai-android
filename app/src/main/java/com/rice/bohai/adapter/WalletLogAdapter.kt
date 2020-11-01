package com.rice.bohai.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.R
import com.rice.bohai.model.WalletLogModel

/**
 * @author CWQ
 * @date 2020/9/26
 */
class WalletLogAdapter(var context: Context, data: MutableList<WalletLogModel>) :
    BaseQuickAdapter<WalletLogModel, BaseViewHolder>(R.layout.item_adapter_walletlog, data) {

    override fun convert(helper: BaseViewHolder?, item: WalletLogModel?) {

        helper?.setText(R.id.tv_title, "现金类型：" + if (item?.type == 26) "释放储备券" else "释放仓储券")
        helper?.setText(R.id.tv_date, item?.created_at)
        helper?.setText(R.id.tv_money, "¥" + item?.price)
    }
}