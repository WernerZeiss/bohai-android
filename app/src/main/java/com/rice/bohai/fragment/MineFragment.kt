package com.rice.bohai.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dubhe.imageselector.ClipImageActivity
import com.dubhe.imageselector.ImageSelector
import com.dubhe.imageselector.Path.REQUEST_PERMISSIONS
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.adapter.MineFragmentAdapter
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.WelcomeActivity
import com.rice.bohai.activity.*
import com.rice.dialog.InputDialog
import com.rice.dialog.OkCancelDialog
import com.rice.dialog.OkDialog
import com.rice.imageloader.GlideLoadUtils
import com.rice.model.MineFragmnetModel
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.*
import com.tencent.bugly.beta.Beta
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.fragment_pintuan.*
import java.nio.charset.Charset

class MineFragment : BaseImmersionFragment() {

    private lateinit var mAdapter: MineFragmentAdapter
    private val mList = ArrayList<MineFragmnetModel>()
    var firstIds: ArrayList<Int> = ArrayList() //头部项ID
    var endIds: ArrayList<Int> = ArrayList() //尾部项ID
    lateinit var permissionDialog: OkDialog
    private var hasPermission = false
    lateinit var logoutDialog: OkCancelDialog
    lateinit var nickNameDialog: InputDialog

    init {
        isContentInvade = true
        isWhiteStatusBarIcon = true
        isWhiteNavigationBarIcon = false
        viewTopId = R.id.viewTop
    }

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_mine

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (VersionUtils.getVersionName(mContext).endsWith("alpha")) {
            textVersion.text = "当前版本：v${VersionUtils.getVersionName(mContext)}\n当前API地址:${Constant.getBaseUrl()}"
        } else {
            textVersion.text = "当前版本：v${VersionUtils.getVersionName(mContext)}"
        }
        ImageSelector.getInstance(this@MineFragment) //初始化图片选择器对象，参数是Activity
                .setEnableClip(true) //是否裁剪图片
                .setClipMode(ClipImageActivity.TYPE_CIRCLE).onProcessFinishListener =
                ImageSelector.OnProcessFinishListener { path ->
                    MyApplication.instance.onUploadCompleteListner =
                            object : MyApplication.OnUploadCompleteListner {
                                override fun onUploadComplete(src: String) {
                                    updateHeader(src)
                                }
                            }
                    MyApplication.instance.uploadImage(path)
                }
        nickNameDialog = InputDialog(mContext, "请输入新昵称")
        nickNameDialog.setTitle("请输入新昵称")
        nickNameDialog.onOkClickListener = object : InputDialog.OnOkClickListener {
            override fun onOkClick(str: String) {
                updateNickName(str)
            }
        }
        logoutDialog = OkCancelDialog(mContext)
        logoutDialog.setInfo("确定要退出当前账号吗？")
        logoutDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
            override fun onOkClick() {
                MyApplication.instance.clear()
                initData()
                //                var intent = Intent(MyApplication.instance, MainActivity::class.java)
                //                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                //                MyApplication.instance.startActivity(intent)
                //                restartApp()
            }
        }
        permissionDialog = OkDialog(mActivity)
        permissionDialog.setTitle("提示")
        permissionDialog.setInfo("您必须授予权限后才能正常使用该功能。")
        permissionDialog.setCancelable(false)
        permissionDialog.onOkClickListener = object : OkDialog.OnOkClickListener {
            override fun onOkClick() {
                initPermission(true)
            }
        }
        var stringArray = mContext.resources.getStringArray(R.array.bohai_setting_str) //设置项名称数组
        //        val iconArray = mContext.resources.getIntArray(com.rice.riceframe.R.array.setting_icon)//设置项图标数组
        //        if (stringArray.size != iconArray.size) {
        //            Log.e(this.toString(), "标题数组setting_str(${stringArray.size})图标数组setting_str(${iconArray.size})长度不一致，请检查arrays.xml")
        //        }
        if (!VersionUtils.getVersionName(mContext).endsWith("alpha")) {
            var listStr: MutableList<String> = ArrayList()
            for (item in stringArray) {
                listStr.add(item)
            }
            listStr.removeAt(listStr.lastIndex)
            stringArray = listStr.toTypedArray()
        }
        firstIds.add(0)
        endIds.add(stringArray.lastIndex)
        for (i in stringArray.indices) {
            val model = MineFragmnetModel()
            model.title = stringArray[i]
            //            model.resId = iconArray[i]
            if (firstIds.indexOf(stringArray.indices.indexOf(i)) >= 0) {
                //头部添加空白区域的项，头部
                model.location = MineFragmnetModel.FIRST
            } else if (endIds.indexOf(stringArray.indices.indexOf(i)) >= 0) {
                //不要分割线的项，尾部
                model.location = MineFragmnetModel.END
            }
            mList.add(model)
        }
        mAdapter = MineFragmentAdapter(mList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (position < 5) {
                if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                    ToastUtil.showShort("请先登录")
                    ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                    return@setOnItemClickListener
                }
            }
            when (position) {
                0 -> {
                    //邀请好友
                    if (MyApplication.instance.userInfo?.is_start_register == 0) {
                        ToastUtil.showShort("暂未开放注册")
                    } else {
                        var b = Bundle()
                        b.putInt("mode", DataActivity.MODE_SHARE_BG)
                        ActivityUtils.openActivity(mContext, DataActivity::class.java, b)
                    }
                }
                //                1 -> {
                //                    //我的好友
                //                    ActivityUtils.openActivity(mContext, MyFriendActivity::class.java)
                //                }
                1 -> {
                    //安全中心
                    var b = Bundle()
                    b.putInt("mode", MulModeSettingActivity.MODE_SECURITY)
                    ActivityUtils.openActivity(mContext, MulModeSettingActivity::class.java, b)
                }
                2 -> {
                    //地址管理
                    ActivityUtils.openActivity(mContext, MyAddressActivity::class.java)
                }
                3 -> {
                    //银行卡管理
                    ActivityUtils.openActivity(mContext, CardManagerActivity::class.java)
                }
//                4 -> {
//                    //我的协议
//                    //                    if (hasPermission) {
//                    //                    initPermission(false)
//                    ActivityUtils.openActivity(mContext, MyXieyiActivity::class.java)
//                    //                    } else {
//                    //                        if (!permissionDialog.isShowing) {
//                    //                            permissionDialog.show()
//                    //                        }
//                    //                    }
//                }
//                //                5 -> {
//                //                    //实名认证
//                //                    ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
//                //                }
                4 -> {
                    //检查更新
                    Beta.checkUpgrade(true, false)
                }
                5 -> {
                    //切换API
                    val switchOkCancelDialog = OkCancelDialog(mContext)
                    var info = ""
                    if (Constant.baseType == Constant.BETA) {
                        info = "要切换到${Constant.BASE_URL_HAIBO}域名吗？（将会重启APP）"
                    } else if (Constant.baseType == Constant.ALPHA) {
                        info = "要切换到${Constant.BASE_URL_XHRD}域名吗？（将会重启APP）"
                    }
                    switchOkCancelDialog.setInfo(info)
                    switchOkCancelDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                        override fun onOkClick() {
                            if (Constant.baseType == Constant.BETA) {
                                Constant.baseType = Constant.ALPHA
                            } else if (Constant.baseType == Constant.ALPHA) {
                                Constant.baseType = Constant.BETA
                            }
                            MyApplication.instance.setBaseTypeFromLocal()
                            restartApp()
                        }
                    }
                    switchOkCancelDialog.show()
                }
            }
        }
        recyclerSetting.layoutManager = LinearLayoutManager(mContext)
        recyclerSetting.adapter = mAdapter
        recyclerSetting.setHasFixedSize(true)
        recyclerSetting.isNestedScrollingEnabled = false
        frameMessage.setOnClickListener {
            //消息中心
            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                ToastUtil.showShort("请先登录")
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                return@setOnClickListener
            }
            ActivityUtils.openActivity(mContext, MessageActivity::class.java)
        }
        llWDHY.setOnClickListener {
            //我的好友
            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                ToastUtil.showShort("请先登录")
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                return@setOnClickListener
            }
            ActivityUtils.openActivity(mContext, MyFriendActivity::class.java)
        }
        llSMRZ.setOnClickListener {
            //实名认证
            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                ToastUtil.showShort("请先登录")
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                return@setOnClickListener
            }
            ActivityUtils.openActivity(mContext, BindBankCardActivity::class.java)
        }
        //        llZHSZ.setOnClickListener {
        //            //账户市值
        //            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
        //                ToastUtil.showShort("请先登录")
        //                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        //                return@setOnClickListener
        //            }
        //            ActivityUtils.openActivity(mContext, MyAccountActivity::class.java)
        //        }
        //        llXJZh.setOnClickListener {
        //            //现金账户
        //            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
        //                ToastUtil.showShort("请先登录")
        //                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        //                return@setOnClickListener
        //            }
        //            var b = Bundle()
        //            b.putInt("mode", ZHSZFragment.MODE_XJZH)
        //            ActivityUtils.openActivity(mContext, MyAccountActivity::class.java, b)
        //        }
//        textBtn.setOnClickListener {
//            //充值
//            ActivityUtils.openActivity(mContext, RechargeActivity::class.java)
//        }
        textBtn2.setOnClickListener {
            //提现
            ActivityUtils.openActivity(mContext, CashoutActivity::class.java)
        }
        //        llPHZH.setOnClickListener {
        //            //提货金额
        //            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
        //                ToastUtil.showShort("请先登录")
        //                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        //                return@setOnClickListener
        //            }
        //            var b = Bundle()
        //            b.putInt("mode", ZHSZFragment.MODE_THZH)
        //            ActivityUtils.openActivity(mContext, MyAccountActivity::class.java, b)
        //        }
        //        llJFZH.setOnClickListener {
        //            //积分账户
        //            ActivityUtils.openActivity(mContext, MyScoreActivity::class.java)
        //        }
        //        llXSPH.setOnClickListener {
        //            //销售配货
        //            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
        //                ToastUtil.showShort("请先登录")
        //                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        //                return@setOnClickListener
        //            }
        //            var b = Bundle()
        //            b.putInt("index", 3)
        //            ActivityUtils.openActivity(mContext, InComeActivity::class.java, b)
        //        }
        //        llWDDD.setOnClickListener {
        //            //我的订单
        //            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
        //                ToastUtil.showShort("请先登录")
        //                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        //                return@setOnClickListener
        //            }
        //            ActivityUtils.openActivity(mContext, WDCCActivity::class.java) //←以前叫我的持仓
        //        }

        iv_tem_money.setOnClickListener {
            //点击领取 待领取金额
            ActivityUtils.openActivity(mContext, RedPacketListActivity::class.java)
        }
        llWDSP.setOnClickListener {
            //我的商品
            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                ToastUtil.showShort("请先登录")
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                return@setOnClickListener
            }
            ActivityUtils.openActivity(mContext, MyOrderActivity::class.java) //←以前叫我的订单
        }
        llWDXJ.setOnClickListener {
            //我的钱包
            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
                ToastUtil.showShort("请先登录")
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
                return@setOnClickListener
            }
            ActivityUtils.openActivity(mContext, MyAccountActivity::class.java)
        }
        //        llHZJF.setOnClickListener {
        //            //划转积分
        //            if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
        //                ToastUtil.showShort("请先登录")
        //                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
        //                return@setOnClickListener
        //            }
        //            var b = Bundle()
        //            b.putInt("mode", MulModeSettingActivity.MODE_ZJHZ)
        //            ActivityUtils.openActivity(mContext, MulModeSettingActivity::class.java, b)
        //        }
        textLogout.setOnClickListener {
            //退出登录
            if (!logoutDialog.isShowing) {
                logoutDialog.show()
            }
        }
        textSwitch.setOnClickListener {
            //切换账号
            ActivityUtils.openActivity(mContext, SwitchAccountActivity::class.java)
        }
        textNumber.setOnClickListener {
            //显示隐藏手机号
            if (textNumber.text.contains("*")) {
                //切换为显示
                var mHideAnimation = AlphaAnimation(1.0f, 0.0f)
                mHideAnimation.duration = 500
                mHideAnimation.fillAfter = true
                textNumber.startAnimation(mHideAnimation)
                textNumber.text = MyApplication.instance.userInfo?.user_phone
                var mShowAnimation = AlphaAnimation(0.0f, 1.0f)
                mShowAnimation.duration = 500
                mShowAnimation.fillAfter = true
                textNumber.startAnimation(mShowAnimation)
                //                textNumber.setAnimation(AnimationUtils.loadAnimation(myContext, android.R.anim.slide_in_left));
                textNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        mContext.resources.getDrawable(R.drawable.icon_hide),
                        null
                )
            } else {
                //切换为隐藏
                var mHideAnimation = AlphaAnimation(1.0f, 0.0f)
                mHideAnimation.duration = 500
                mHideAnimation.fillAfter = true
                textNumber.startAnimation(mHideAnimation)
                textNumber.text = "***********"
                var mShowAnimation = AlphaAnimation(0.0f, 1.0f)
                mShowAnimation.duration = 500
                mShowAnimation.fillAfter = true
                textNumber.startAnimation(mShowAnimation)
                textNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        mContext.resources.getDrawable(R.drawable.icon_show),
                        null
                )
            }
        }
        initData()
    }

    /**
     * 重新启动App -> 杀进程,会短暂黑屏,启动慢
     */
    fun restartApp() {
        Observable.create<String> { e ->
            Thread {
                Thread.sleep(1000)
                e.onComplete()
            }.start()
        }.subscribe(object : Observer<String?> {
            override fun onComplete() {
                var intent = Intent(MyApplication.instance, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                MyApplication.instance.startActivity(intent)
                android.os.Process.killProcess(android.os.Process.myPid())
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(t: String) {

            }

            override fun onError(e: Throwable) {

            }
        })

    }

    /**
     * 修改昵称
     */
    private fun updateNickName(name: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_USER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "nickname" - name
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    MyApplication.instance.getUserInfoFromWeb()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    /**
     * 修改头像
     */
    private fun updateHeader(path: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_USER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "avatar" - path
            }
            onSuccess { bytes ->
                var data = bytes.toString(Charset.defaultCharset())
                var status = PublicModel.forjson(data)
                ToastUtil.showShort(status.message)
                if (status.code == RiceHttpK.SUCCESS) {
                    MyApplication.instance.getUserInfoFromWeb()
                }
            }
            onFail { error ->
                var message = error.message
                if ((error.message ?: "").contains("java")) {
                    Logger.e(message ?: "")
                    message = "未知错误"
                }
                ToastUtil.showShort(message)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageSelector.getInstance(this@MineFragment).onHeaderResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        ImageSelector.getInstance(this@MineFragment).clear()
    }

    /**
     * 加载账户数据
     */
    @SuppressLint("SetTextI18n")
    fun initData() {
        //        textZHSZ.text = mContext.resources.getString(R.string.CNY) + NumberUtils.缩写数字(
        //            MyApplication.instance.userInfo?.total_market_value,
        //            100000.0
        //        )
        //        textXSPH.text = mContext.resources.getString(R.string.CNY) + NumberUtils.缩写数字(
        //            MyApplication.instance.userInfo?.total_profit_price,
        //            100000.0
        //        )
        textStatus.text = MyApplication.instance.userInfo?.is_effective_user_name
        textXianjin.text =
                mContext.resources.getString(R.string.CNY) + MyApplication.instance.userInfo?.price
        if (!TextUtils.isEmpty(MyApplication.instance.userInfo?.need_wallet_money)){
            tv_tem_money.text = mContext.resources.getString(R.string.CNY) + MyApplication.instance.userInfo?.need_wallet_money
        }else{
            tv_tem_money.text = mContext.resources.getString(R.string.CNY) + "0.00"
        }
        //        textJiaoshou.text = mContext.resources.getString(R.string.CNY) + NumberUtils.缩写数字(
        //            MyApplication.instance.userInfo?.total_settlement_price,
        //            100000.0
        //        )
        //        textJifen.text = NumberUtils.缩写数字(MyApplication.instance.userInfo?.integral_num, 100000.0)

        textName.text = MyApplication.instance.userInfo?.display_name ?: "登录/注册"
        textView.text = MyApplication.instance.userInfo?.member_name ?: "0"
        if (MyApplication.instance.userInfo?.is_no_read_message == 1) {
            point.visibility = View.VISIBLE
        } else {
            point.visibility = View.INVISIBLE
        }
        //        textView.setOnClickListener {
        //            //TODO:测试语句
        //            if (MyApplication.instance.userInfo?.member_name!!.toInt() <5) {
        //                MyApplication.instance.userInfo?.member_name = (MyApplication.instance.userInfo?.member_name!!.toInt() + 1).toString()
        //            } else {
        //                MyApplication.instance.userInfo?.member_name = "0"
        //            }
        //            initData()
        //        }
        textView.background = resources.getDrawable(
                ResHelper.getResId(
                        "bg_vip${MyApplication.instance.userInfo?.member_name ?: "0"}",
                        R.drawable::class.java
                )
        )
        if (MyApplication.instance.userInfo != null && TextUtils.isNotEmpty(MyApplication.instance.userInfo!!.access_token)) {
            GlideLoadUtils.getInstance()
                    .glideLoad(mContext, MyApplication.instance.userInfo?.avatar, imgHeader, true)
        } else {
            imgHeader.setImageResource(R.mipmap.ic_launcher_round)
        }
        //        val onClickListener = View.OnClickListener {
        //            //改头像
        //            ImageSelector.getInstance(this@EditMineInfoActivity).showImageSelectMenu()
        //        }
        textName.setOnClickListener {
            if (MyApplication.instance.userInfo != null && TextUtils.isNotEmpty(MyApplication.instance.userInfo!!.access_token)) {
                //改昵称
                if (!nickNameDialog.isShowing) {
                    nickNameDialog.show()
                }
            } else {
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            }
        }
        imgHeader.setOnClickListener {
            //改头像
            if (MyApplication.instance.userInfo != null && TextUtils.isNotEmpty(MyApplication.instance.userInfo!!.access_token)) {
                ImageSelector.getInstance(this@MineFragment).showImageSelectMenu()
//                test()
            } else {
                ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            }
        }
        textName.isSoundEffectsEnabled =
                MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)
        if (MyApplication.instance.userInfo != null && TextUtils.isNotEmpty(MyApplication.instance.userInfo!!.access_token)) {
            llData.visibility = View.VISIBLE
            textNumber.visibility = View.VISIBLE
            textLogout.visibility = View.VISIBLE
            textSwitch.visibility = View.VISIBLE
            frameMessage.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
        } else {
            llData.visibility = View.GONE
            textNumber.visibility = View.GONE
            textLogout.visibility = View.GONE
            textSwitch.visibility = View.GONE
            frameMessage.visibility = View.INVISIBLE
            textView.visibility = View.INVISIBLE
        }
    }

    /**
     * 初始化权限
     * 适配6.0+手机的运行时权限
     *
     * @param forceRequest 强制申请权限
     */
    fun initPermission(forceRequest: Boolean) {
        val TAG = "---申请权限---"
        val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
        //检查权限
        if (ContextCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            //判断权限是否被拒绝过
            if (forceRequest) {
                //强制申请
                Log.d(TAG, "强制申请")
                ActivityCompat.requestPermissions(mActivity, permissions, REQUEST_PERMISSIONS)
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                            mActivity,
                            Manifest.permission.READ_CONTACTS
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                            mActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            ) {
                //用户曾拒绝过权限
                Log.d(TAG, "用户曾拒绝过权限")
                mActivity.runOnUiThread { permissionDialog.show() }
            } else {
                //用户没有拒绝过，首次申请
                Log.d(TAG, "首次申请")
                ActivityCompat.requestPermissions(mActivity, permissions, REQUEST_PERMISSIONS)
            }
        } else {
            //已有权限
            hasPermission = true
            ActivityUtils.openActivity(mContext, PainterActivity::class.java)
        }
    }

}