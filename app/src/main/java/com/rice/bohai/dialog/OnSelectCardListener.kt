package com.rice.bohai.dialog

import com.rice.bohai.model.CardModel

/**
 * @author CWQ
 * @date 2020/8/8
 */
interface OnSelectCardListener {

    fun onSelectCard(position: Int, card: CardModel)
}