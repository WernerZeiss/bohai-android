package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.rice.adapter.SimpeViewPaperAdaper
import com.rice.base.RiceBaseActivity
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.fragment.DataFragment
import com.rice.bohai.fragment.XSPHFragment
import com.rice.tool.ActivityUtils
import kotlinx.android.synthetic.main.activity_income.*

/**
 * 我的钱包
 */
@SuppressLint("Registered")
class InComeActivity : RiceBaseActivity() {

    var listTabs: MutableList<String> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()
    private lateinit var mAdapter: SimpeViewPaperAdaper
    private var curindex = 0
    var page = 1

    override fun getLayoutId(): Int {
        return R.layout.activity_income
    }

    override fun initView() {
        listTabs.add("现金收益")
        listTabs.add("积分收益")
        listTabs.add("提货金额")
        listTabs.add("销售配货")
        fragmentList.add(DataFragment.newInstance(DataFragment.MODE_XJSY))
        fragmentList.add(DataFragment.newInstance(DataFragment.MODE_JFSY))
        fragmentList.add(DataFragment.newInstance(DataFragment.MODE_THJE))
        fragmentList.add(XSPHFragment())
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
        mAdapter = SimpeViewPaperAdaper(this@InComeActivity, fragmentList, listTabs)
        viewPager.adapter = mAdapter
        mPagerTab.setViewPager(viewPager, 0)
        viewPager.offscreenPageLimit = listTabs.size
        //        mPagerTab!!.setTextColor(Color.parseColor("#060606"), curindex,
        //                resources.getColor(R.color.colorPrimaryDark), true)
        mPagerTab.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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
        textRecharge.setOnClickListener {
            //充值
            ActivityUtils.openActivity(mContext, RechargeActivity::class.java)
        }
        textCashout.setOnClickListener {
            //去提现
            ActivityUtils.openActivity(mContext, CashoutActivity::class.java)
        }
        viewPager.currentItem = curindex
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance.onUserInfoUpdateCompleteListener = object : MyApplication.OnUserInfoUpdateCompleteListener {
            override fun onUserInfoUpdateComplete() {
                textPrice.text = MyApplication.instance.userInfo?.price
                var lp = textRecharge.layoutParams
                if (MyApplication.instance.userInfo?.is_start_recharge == 0) {
                    lp.width = 0
                    lp.height = 0
                } else {
                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                textRecharge.layoutParams = lp
            }
        }
        MyApplication.instance.getUserInfoFromWeb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun getIntentData() {
        curindex = intent.extras?.getInt("index", 0) ?: 0
    }

    override fun clear() {

    }

}