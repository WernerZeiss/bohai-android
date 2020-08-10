package com.rice.bohai.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.listener.OnCardListListener
import com.rice.bohai.model.CardModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils
import kotlinx.android.synthetic.main.item_adapter_cardlist_content.view.*

/**
 * @author CWQ
 * @date 2020/8/8
 */
class CardListAdapter(var data: MutableList<CardModel>) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val TYPE_CARD = 0
        const val TYPE_ADD = 1
        const val TYPE_TEXT = 2
    }

    private var mListener: OnCardListListener? = null

    fun setOnCardListener(listener: OnCardListListener) {
        mListener = listener
    }

    fun update(list: List<CardModel>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = when (viewType) {
            TYPE_ADD -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_adapter_cardlist_add, parent, false)
            TYPE_TEXT -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_adapter_cardlist_text, parent, false)
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_adapter_cardlist_content, parent, false)
        }
        return BaseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount - 2 -> {
                TYPE_ADD
            }
            itemCount - 1 -> {
                TYPE_TEXT
            }
            else -> {
                TYPE_CARD
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_CARD -> {//银行卡
                with(holder.itemView) {
                    GlideLoadUtils.getInstance().glideLoad(
                        context,
                        TextUtils.getImgUrl(
                            Constant.getBaseUrl(),
                            Constant.IMAGE_PRE_URL + data[position].bank_logo
                        ),
                        iv_bank_logo
                    )
                    tv_bank_name.text = data[position].name
                    tv_bank_number.text = data[position].bank_number
                    iv_default_check.setImageResource(if (data[position].is_default == 1) R.drawable.icon_check_focus_white else R.drawable.icon_check_white)

                    ll_default.setOnClickListener {
                        if (data[position].is_default != 1) {
                            mListener?.onClickDefault(data[position])
                        }
                    }
                }
            }
            TYPE_ADD -> {//添加银行卡
                holder.itemView.setOnClickListener {
                    mListener?.onAddCard()
                }
            }
        }
    }
}