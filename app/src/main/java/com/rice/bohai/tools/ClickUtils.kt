package com.rice.bohai.tools

/**
 * @author CWQ
 * @date 2020/9/7
 */
object ClickUtils {

    var lastTime = 0L

    fun enableClick(): Boolean {
        val dis = System.currentTimeMillis() - lastTime
        lastTime = System.currentTimeMillis()
        return dis >= 1000
    }
}