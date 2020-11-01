package com.rice.bohai.listener

/**
 * @author CWQ
 * @date 2020/10/28
 */
interface OnSelectPayListener {

    /**
     * @param type 0余额 1购货券
     */
    fun onSelected(type: Int)
}