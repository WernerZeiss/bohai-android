package com.rice.bohai.listener

import com.rice.bohai.model.CardModel

/**
 * @author CWQ
 * @date 2020/8/8
 */
interface OnSelectCardListener {

    fun onSelectCard(position: Int, card: CardModel)
}