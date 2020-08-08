package com.rice.bohai

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.permission.FloatPermissionManager
import com.rice.base.RiceBaseActivity
import com.rice.bohai.activity.AddAddressActivity
import com.rice.bohai.fragment.ChubeiFragment
import com.rice.bohai.fragment.HomeFragment
import com.rice.bohai.fragment.MineFragment
import com.rice.bohai.fragment.PintuanFragment
import com.rice.bohai.service.SellPromptService
import com.rice.dialog.OkDialog
import com.rice.tool.ActivityUtils
import com.rice.tool.ToastUtil
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_bottom_navigation.*
import permison.FloatWindowManager

@SuppressLint("Registered")
class MainActivity : RiceBaseActivity() {

    var homeFragment = HomeFragment()
    var pintuanFragment = PintuanFragment()
    var chubeiFragment = ChubeiFragment()

    //    var jiaoyiFragment = JiaoyiFragment()
    var mineFragment = MineFragment()

    //    var shopcarFragment = ShopcarFragment.newInstance(ShopcarFragment.MODE_NORMAL)
    var listFragment: MutableList<Fragment> = ArrayList()
    lateinit var floatingPermissionDialog: OkDialog

    companion object {
        val TAB_HOME = 0

        //        val TAB_JIAOYI = 1
//        val TAB_SHOP_CAR = 2
        val TAB_PINTUAN = 1
        val TAB_CHUBEI = 2
        val TAB_MINE = 3
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        floatingPermissionDialog = OkDialog(this)
        floatingPermissionDialog.setInfo("为了正常使用该应用，请开启悬浮窗权限。")
        floatingPermissionDialog.setOkText("立即开启")
        floatingPermissionDialog.onOkClickListener = object : OkDialog.OnOkClickListener {
            override fun onOkClick() {
                FloatWindowManager.getInstance().applyOrShowFloatWindow(mContext)
            }
        }
        if (listFragment.size < 1) {
            listFragment.add(homeFragment)
//            listFragment.add(jiaoyiFragment)
//            listFragment.add(shopcarFragment)
            listFragment.add(pintuanFragment)
            listFragment.add(chubeiFragment)
            listFragment.add(mineFragment)
            viewPageMain.offscreenPageLimit = 4
            viewPageMain.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getItem(position: Int): Fragment {
                    return listFragment[position]
                }

                override fun getCount(): Int {
                    return listFragment.size
                }
            }
            viewPageMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    changeTab(position)
                    viewPageMain.currentItem = position
                    onResume()
                }
            })
        }
        viewPageMain.currentItem = TAB_HOME
        llHome.setOnClickListener {
            viewPageMain.currentItem = TAB_HOME
            changeTab(TAB_HOME)
        }
//        llJiaoyi.setOnClickListener {
//            viewPageMain.currentItem = TAB_JIAOYI
//            changeTab(TAB_JIAOYI)
//        }
//        llShopcar.setOnClickListener {
//            viewPageMain.currentItem = TAB_SHOP_CAR
//            changeTab(TAB_SHOP_CAR)
//        }
        llMine.setOnClickListener {
            viewPageMain.currentItem = TAB_MINE
            changeTab(TAB_MINE)
        }
        llPintuan.setOnClickListener {
            viewPageMain.currentItem = TAB_PINTUAN
            changeTab(TAB_PINTUAN)
        }
        llChubei.setOnClickListener {
            viewPageMain.currentItem = TAB_CHUBEI
            changeTab(TAB_CHUBEI)
        }
        Beta.checkUpgrade(false, false)
//        if (MyApplication.instance.userInfo != null && TextUtils.isNotEmpty(MyApplication.instance.userInfo!!.access_token)) {
//            if (!floatingPermissionDialog.isShowing && !PermissionUtils.checkFloatPermission(mContext)) {
//                floatingPermissionDialog.show()
//            }
//        }
        var mFloatPermissionManager = FloatPermissionManager()
        if (!mFloatPermissionManager.checkPermission(mContext)) {
            mFloatPermissionManager.applyOrShowFloatWindow(mContext)
        }
//        EventBus.getDefault().register(this)
        startService(Intent(MyApplication.instance, SellPromptService::class.java))
    }

    fun changeTab(tab: Int) {
        imgHome.setImageResource(R.drawable.icon_home)
//        imgJiaoyi.setImageResource(R.drawable.icon_jiaoyi)
//        imgShopcar.setImageResource(R.drawable.icon_shopcar)
        imgPintuan.setImageResource(R.drawable.icon_pintuan)
        imgChubei.setImageResource(R.drawable.icon_chubei)
        imgMine.setImageResource(R.drawable.icon_mine)
        textHome.setTextColor(mContext.resources.getColor(R.color.gray9))
//        textShopcar.setTextColor(mContext.resources.getColor(R.color.gray9))
//        textJiaoyi.setTextColor(mContext.resources.getColor(R.color.gray9))
        textPintuan.setTextColor(mContext.resources.getColor(R.color.gray9))
        textChubei.setTextColor(mContext.resources.getColor(R.color.gray9))
        textMine.setTextColor(mContext.resources.getColor(R.color.gray9))
        when (tab) {
            TAB_HOME -> {
                imgHome.setImageResource(R.drawable.icon_home_focus)
                textHome.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
            }
//            TAB_JIAOYI -> {
//                imgJiaoyi.setImageResource(R.drawable.icon_jiaoyi_focus)
//                textJiaoyi.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
//            }
//            TAB_SHOP_CAR -> {
//                imgShopcar.setImageResource(R.drawable.icon_shopcar_focus)
//                textShopcar.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
//            }
            TAB_PINTUAN -> {
                imgPintuan.setImageResource(R.drawable.icon_pintuan_focus)
                textPintuan.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
            }
            TAB_CHUBEI -> {
                imgChubei.setImageResource(R.drawable.icon_chubei_focus)
                textChubei.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
            }
            TAB_MINE -> {
                imgMine.setImageResource(R.drawable.icon_mine_focus)
                textMine.setTextColor(mContext.resources.getColor(R.color.bottom_focus))
            }
        }
//        if (tab == TAB_SHOP_CAR) {
//            imgShadow.visibility = View.INVISIBLE
//        } else {
        imgShadow.visibility = View.VISIBLE
//        }
    }

    override fun onResume() {
        super.onResume()
        if (MyApplication.instance.userInfo?.is_address ?: "1" != "1") {
            //没有填写地址
            ToastUtil.showShort("请填写收货地址")
            val b = Bundle()
            b.putBoolean("hasBack", false)
            ActivityUtils.openActivity(mContext, AddAddressActivity::class.java, b)
        }
        MyApplication.instance.onUserInfoUpdateCompleteListener =
            object : MyApplication.OnUserInfoUpdateCompleteListener {
                override fun onUserInfoUpdateComplete() {
                    if (mineFragment.isResumed) {
                        mineFragment.initData()
                    }
                    if (viewPageMain.currentItem == TAB_PINTUAN) {
                        pintuanFragment.initData()
                    } else if (viewPageMain.currentItem == TAB_CHUBEI) {
                        chubeiFragment.initData()
                    }
                }
            }
        MyApplication.instance.getUserInfoFromWeb()
        if (MyApplication.instance.userInfo == null) {
            if (viewPageMain.currentItem == TAB_PINTUAN) {
                pintuanFragment.initData()
            } else if (viewPageMain.currentItem == TAB_CHUBEI) {
                chubeiFragment.initData()
            }
        }
//        if (jiaoyiFragment.isResume) {
//            jiaoyiFragment.onResume()
//        }
//        if (shopcarFragment.isResumed) {
//            shopcarFragment.initData()
//        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun loginOnsuccess(msg: String) {
//        if ("loginOnsuccess" == msg) {
//            MyApplication.instance.getUserInfoFromWeb()
//        }
//    }

    override fun getIntentData() {

    }

    override fun clear() {

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        EventBus.getDefault().unregister(this)
//    }

}