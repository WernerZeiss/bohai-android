package com.rice.bohai.activity

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.rice.base.RiceBaseActivity
import com.rice.bohai.MainActivity
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.fragment.NaviFragment
import com.rice.tool.ActivityUtils
import kotlinx.android.synthetic.main.activity_navi.*

@SuppressLint("Registered")
class NaviActivity : RiceBaseActivity() {

    init {
        navigationBarColorId = R.color.navi_bottom
        statusBarColorId = R.color.white
        isWhiteStatusBarIcon = false
        isWhiteNavigationBarIcon = true
    }

    var fragmentList: MutableList<Fragment> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.activity_navi
    }

    override fun initView() {
        for (i in 1..3) {
            var naviFragment = NaviFragment.newInstance(i)
            naviFragment.onEnterClickListener = object : NaviFragment.OnEnterClickListener {
                override fun onEnterClick() {
                    ActivityUtils.openActivity(mContext, MainActivity::class.java)
                    finish()
                }
            }
            fragmentList.add(naviFragment)
        }
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }
        }
        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                //                for (item in listTabs) {
                //                    item.isChecked = listTabs.indexOf(item) == position
                //                }
                //                            tabAdapter.notifyDataSetChanged()
                //                            smoothMoveToPosition(recyclerTab, position)
            }
        })
        viewPager.currentItem = 0
        textSkip.setOnClickListener {
            ActivityUtils.openActivity(mContext, MainActivity::class.java)
            finish()
        }
    }

    override fun onDestroy() {
        MyApplication.instance.setIsFirst(false)
        super.onDestroy()
    }

    override fun finish() {
        MyApplication.instance.setIsFirst(false)
        super.finish()
    }

    override fun getIntentData() {

    }

    override fun clear() {

    }

}