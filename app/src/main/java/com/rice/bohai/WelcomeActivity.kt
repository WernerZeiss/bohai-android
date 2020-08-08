package com.rice.bohai

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dubhe.imageselector.Path.REQUEST_PERMISSIONS
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.activity.NaviActivity
import com.rice.bohai.model.PintuanProduceModel
import com.rice.dialog.AreaSelecterDialog
import com.rice.dialog.CitySelecterDialog
import com.rice.dialog.OkDialog
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_pintuan.*
import pub.devrel.easypermissions.EasyPermissions

@SuppressLint("Registered")
class WelcomeActivity : RiceBaseActivity(), EasyPermissions.PermissionCallbacks {

    private var hasPermission = false
    lateinit var permissionDialog: OkDialog

    init {
        isContentInvade = true
        isWhiteNavigationBarIcon = false
        isWhiteStatusBarIcon = false
        navigationBarColorId = R.color.welcome_bottom
        viewTopId = R.id.viewTop
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_welcome
    }

    override fun initView() {
        permissionDialog = OkDialog(this)
        permissionDialog.setTitle("提示")
        permissionDialog.setInfo("您必须授予权限后才能正常使用本软件。")
        permissionDialog.setCancelable(false)
        permissionDialog.onOkClickListener = object : OkDialog.OnOkClickListener {
            override fun onOkClick() {
                initPermission(true)
                permissionDialog.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!EasyPermissions.hasPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            )
        ) {
            if (!permissionDialog.isShowing) {
                permissionDialog.show()
            }
        } else {
            initData()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        if (list.isNotEmpty()) {
            if (!permissionDialog.isShowing) {
                permissionDialog.show()
            }
        } else {
            initData()
        }
    }

    private fun initData() {
        Observable.create(ObservableOnSubscribe<String> { e ->
            Thread(Runnable {
                do {
                    Thread.sleep(1500)
                } while (MyApplication.instance.systemModel == null
                    || !AreaSelecterDialog.isComplete
                    || !CitySelecterDialog.isComplete
                //                    || TextUtils.isEmpty(MyApplication.instance.deviceToken)
                )
                if (MyApplication.instance.userInfo != null) {
                    MyApplication.instance.onUserInfoUpdateCompleteListener =
                        object : MyApplication.OnUserInfoUpdateCompleteListener {
                            override fun onUserInfoUpdateComplete() {
                                e.onComplete()
                            }
                        }
                    MyApplication.instance.getUserInfoFromWeb()
                } else {
                    e.onComplete()
                }
            }).start()
        }).subscribe(object : Observer<String?> {
            override fun onComplete() {
                runOnUiThread {
                    if (MyApplication.instance.isFirst) {
                        ActivityUtils.openActivity(mContext, NaviActivity::class.java)
                    } else {
                        ActivityUtils.openActivity(mContext, MainActivity::class.java)
                    }
                    finish()
                }
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
     * 初始化权限
     * 适配6.0+手机的运行时权限
     *
     * @param forceRequest 强制申请权限
     */
    fun initPermission(forceRequest: Boolean) {
        val TAG = "---申请权限---"
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )
        //检查权限
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //判断权限是否被拒绝过
            if (forceRequest) {
                //强制申请
                Log.d(TAG, "强制申请")
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS)
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.INTERNET
                )
            ) {
                //用户曾拒绝过权限
                Log.d(TAG, "用户曾拒绝过权限")
                runOnUiThread { permissionDialog.show() }
            } else {
                //用户没有拒绝过，首次申请
                Log.d(TAG, "首次申请")
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS)
            }
        } else {
            //已有权限
            hasPermission = true
            initData()
        }
    }

    override fun getIntentData() {
        /**
         * 解决APP打包第一次安装之后启动APP图标会出现多个程序的问题
         */
        if (!this.isTaskRoot) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果你就放在launcher Activity中话，这里可以直接return了
            val mainIntent = intent
            val action = mainIntent!!.action
            if (mainIntent != null) {
                if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action == Intent.ACTION_MAIN) {
                    finish()
                    return //finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
                }
            }
        }
    }

    override fun clear() {

    }

}