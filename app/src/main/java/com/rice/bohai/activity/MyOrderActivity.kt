package com.rice.bohai.activity

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.rice.adapter.SimpeViewPaperAdaper
import com.rice.base.RiceBaseActivity
import com.rice.bohai.R
import com.rice.bohai.fragment.MyOrderFragment
import kotlinx.android.synthetic.main.activity_my_order.*

@SuppressLint("Registered")
class MyOrderActivity : RiceBaseActivity() {

    var listTabs: MutableList<String> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()
    private lateinit var mAdapter: SimpeViewPaperAdaper
    private var curindex = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_my_order
    }

    override fun initView() {
        listTabs.add("全部")
        listTabs.add("待发货")
        listTabs.add("待收货")
        listTabs.add("已完成")
        for (i in 1..4) {
            fragmentList.add(MyOrderFragment.newInstance(i))
        }
        viewPager.offscreenPageLimit = listTabs.size
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
        mAdapter = SimpeViewPaperAdaper(this@MyOrderActivity, fragmentList, listTabs)
        viewPager.adapter = mAdapter
        mPagerTab.setViewPager(viewPager, 0)
        viewPager.offscreenPageLimit = listTabs.size
        //        mPagerTab!!.setTextColor(Color.parseColor("#060606"), curindex,
        //                resources.getColor(R.color.colorPrimaryDark), true)
        mPagerTab!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(arg0: Int) {
                curindex = arg0
                //                mPagerTab!!.setTextColor(Color.parseColor("#060606"), curindex,
                //                        resources.getColor(R.color.colorPrimaryDark), true)
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

            }

            override fun onPageScrollStateChanged(arg0: Int) {
                if (ViewPager.SCROLL_STATE_IDLE == arg0) {
                    fragmentList[curindex].onHiddenChanged(false)
                }
            }
        })
    }

    override fun getIntentData() {

    }

    override fun clear() {

    }

}