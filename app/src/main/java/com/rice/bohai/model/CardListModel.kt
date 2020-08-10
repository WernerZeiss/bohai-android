package com.rice.bohai.model

import java.io.Serializable

/**
 * @author CWQ
 * @date 2020/8/8
 */
data class CardListModel(
    var lists: MutableList<CardModel> = ArrayList()
) : Serializable