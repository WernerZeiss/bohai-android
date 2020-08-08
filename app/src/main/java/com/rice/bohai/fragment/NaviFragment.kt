package com.rice.bohai.fragment

import android.os.Bundle
import android.view.View
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.R
import kotlinx.android.synthetic.main.fragment_navi.*

class NaviFragment : BaseImmersionFragment() {

    var onEnterClickListener: OnEnterClickListener? = null

    interface OnEnterClickListener {
        fun onEnterClick()
    }

    init {
        navigationBarColorId = R.color.navi_bottom2
        statusBarColorId = R.color.white
        isWhiteStatusBarIcon = true
        isWhiteNavigationBarIcon = true
    }

    var page = 1

    companion object {
        fun newInstance(page: Int): NaviFragment {
            val args = Bundle()
            args.putInt("page", page)
            val fragment = NaviFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_navi

    override fun initView() {
        page = arguments?.getInt("page", 1) ?: 1
        textEnter.setOnClickListener {
            onEnterClickListener?.onEnterClick()
        }
        line1.visibility = View.INVISIBLE
        line2.visibility = View.INVISIBLE
        line3.visibility = View.INVISIBLE
        when (page) {
            1 -> {
                imgText.setImageResource(R.drawable.img_navi_text1)
                img.setImageResource(R.drawable.img_navi1)
                line1.visibility = View.VISIBLE
                textEnter.visibility = View.INVISIBLE
            }
            2 -> {
                imgText.setImageResource(R.drawable.img_navi_text2)
                img.setImageResource(R.drawable.img_navi2)
                line2.visibility = View.VISIBLE
                textEnter.visibility = View.INVISIBLE
            }
            3 -> {
                imgText.setImageResource(R.drawable.img_navi_text3)
                img.setImageResource(R.drawable.img_navi3)
                line3.visibility = View.VISIBLE
                textEnter.visibility = View.VISIBLE
            }
        }
    }

}