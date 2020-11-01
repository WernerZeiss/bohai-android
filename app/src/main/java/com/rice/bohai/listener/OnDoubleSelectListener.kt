package com.rice.bohai.listener

import android.app.Dialog

/**
 * @author CWQ
 * @date 2020/10/28
 */
interface OnDoubleSelectListener {

    fun onLeft(dialog: Dialog)

    fun onRight(dialog: Dialog)
}