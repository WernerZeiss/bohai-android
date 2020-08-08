package com.rice.bohai.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.rice.adapter.SimpeViewPaperAdaper
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.R
import kotlinx.android.synthetic.main.fragment_jiaoyi.*

class JiaoyiFragment : BaseImmersionFragment() {

    var listTabs: MutableList<String> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()
    private lateinit var mAdapter: SimpeViewPaperAdaper
    private var curindex = 0
    var isResume = false
    var guamaiFragment = GuamaiFragment()
    var orderFragment = OrderFragment()

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = false
        isWhiteNavigationBarIcon = false
        navigationBarColorId = R.color.white
        viewTopId = R.id.viewTop
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_jiaoyi

    override fun initView() {
        listTabs.add("求购/转让")
        listTabs.add("订单")
        fragmentList.add(guamaiFragment)
        orderFragment.onGuamaiClickListener = object : OrderFragment.OnGuamaiClickListener {
            override fun onGuamaiClickListener(id: String, position_id: String) {
                viewPager.currentItem = 0
                guamaiFragment.initDetail(id, position_id)
            }
        }
        fragmentList.add(orderFragment)
        viewPager.offscreenPageLimit = listTabs.size
        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
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

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

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
        mAdapter = SimpeViewPaperAdaper(this@JiaoyiFragment, fragmentList, listTabs)
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
                if (orderFragment.isResume) {
                    orderFragment.initCommodity()
                }
            }

            override fun onPageScrollStateChanged(arg0: Int) {
                if (ViewPager.SCROLL_STATE_IDLE == arg0) {
                    fragmentList[curindex].onHiddenChanged(false)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        isResume = true
        if (orderFragment.isResume) {
            orderFragment.initCommodity()
        }
        if (guamaiFragment.isResume) {
            guamaiFragment.onResume()
            guamaiFragment.initTypeList()
            guamaiFragment.searchPrice(false)
        }
    }

}