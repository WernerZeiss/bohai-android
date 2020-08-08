package com.rice.bohai

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.rice.bohai.model.PasswordModel
import com.rice.bohai.model.SystemModel
import com.rice.bohai.model.UploadModel
import com.rice.bohai.model.UserModel
import com.rice.dialog.AreaSelecterDialog
import com.rice.dialog.CitySelecterDialog
import com.rice.model.AreaListModel
import com.rice.model.BankCityModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import com.rice.tool.VersionUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tencent.bugly.Bugly
import me.jessyan.autosize.AutoSizeConfig
import java.io.*

class MyApplication : MultiDexApplication() {

    @kotlin.jvm.JvmField
    var isFirst = true //是否是首次启动
    lateinit var settings: SharedPreferences //设置
    val PREFS_NAME = "config" //设置文件名称
    var systemModel: SystemModel? = null //服务器配置参数
    val USERINFO = "userinfo" //设置文件名
    val PASSWORD = "password" //设置文件名
    val BASETYPE = "BASETYPE" //设置文件名
    var passwordList: MutableList<PasswordModel>? = null
    var userInfo: UserModel? = null
    var onUserInfoUpdateCompleteListener: OnUserInfoUpdateCompleteListener? = null
    var onUploadCompleteListner: OnUploadCompleteListner? = null

    @kotlin.jvm.JvmField
    var imageFilePath = "" //签名图片保存位置

    interface OnUserInfoUpdateCompleteListener {
        fun onUserInfoUpdateComplete()
    }

    interface OnUploadCompleteListner {
        fun onUploadComplete(src: String)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        imageFilePath =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/paint/"
        if (!File(imageFilePath).exists()) {
            File(imageFilePath).mkdirs()
        }
        initLogger()
        settings = getSharedPreferences(PREFS_NAME, 0) //初始化设置对象
        userInfo = getUserInfoFromLocal() //获取本地用户数据对象
        passwordList = getPassword() //获取本地保存的密码
        ToastUtil.init(instance) //初始化Toast组件
        getBaseTypeFromLocal()
        RiceHttpK.init(Constant.getBaseUrl()) //初始化HTTP解析框架
        Http.init(instance) //初始化HTTP组件
        getSystemConfig() //获取服务器配置参数
        getAreaData() //获取省市区信息
        getCityData() //获取绑卡省市信息
        getFirst() //获取是否是首次启动
        Bugly.init(applicationContext, Constant.BUGLY_APPID, true) //注册bugly更新
    }

    fun setBaseTypeFromLocal() {
        val editor = settings.edit()
        editor.putInt(BASETYPE, Constant.baseType)
        editor.apply()
    }

    private fun getBaseTypeFromLocal() {
        if (VersionUtils.getVersionName(instance).endsWith("alpha")) {
            Constant.baseType = settings.getInt(BASETYPE, Constant.BETA) //测试版本从本地获取域名
            return
        } else if (VersionUtils.getVersionName(instance).endsWith("sita")) {
            Constant.baseType = Constant.ALPHA //用户测试版本固定域名
            return
        }
        //正式版固定域名
        Constant.baseType = settings.getInt(BASETYPE, Constant.BETA)
    }

    @SuppressLint("CommitPrefEdits")
    fun getFirst() {
        isFirst = settings.getBoolean("isFirst", true)
    }

    fun setIsFirst(b: Boolean) {
        this.isFirst = b
        settings.edit().putBoolean("isFirst", isFirst).apply()
    }

    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .methodCount(5) //方法行数
            .methodOffset(7) //隐藏内部方法调用到偏移量。默认值5
            //                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag("BoHai") //TAG
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }

    /**
     * 获取服务器配置参数
     */
    fun getSystemConfig() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.SYSTEM_CONFIG)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(applicationContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    systemModel = StringNullAdapter.gson.fromJson(result)
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
     * 获取省市区参数
     */
    fun getAreaData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.AREA_LIST)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(applicationContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    var model: AreaListModel = StringNullAdapter.gson.fromJson(result)
                    AreaSelecterDialog.setData(model)
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
     * 获取绑定银行卡省市参数
     */
    fun getCityData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.CITY_LIST)
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(applicationContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    var model: BankCityModel = StringNullAdapter.gson.fromJson(result)
                    CitySelecterDialog.setData(model)
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
     * 从服务器获取用户信息
     */
    fun getUserInfoFromWeb() {
        if (instance.userInfo == null) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.GET_USER)
            params {
                "access_token" - instance.userInfo!!.access_token
                "android_or_ios" - "1"
                //                "push_token" - instance.deviceToken
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(applicationContext, byts)
                if (TextUtils.isNotEmpty(result)) {
                    val user: UserModel = StringNullAdapter.gson.fromJson(result)
                    instance.saveUserInfo(user)
                    onUserInfoUpdateCompleteListener?.onUserInfoUpdateComplete()
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
     * 上传文件
     */
    fun uploadImage(path: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPLOAD_IMAGE)
            files {
                "file" - path
            }
            onSuccess { bytes ->
                val result = RiceHttpK.getResult(applicationContext, bytes)
                if (TextUtils.isNotEmpty(result)) {
                    val model: UploadModel = StringNullAdapter.gson.fromJson(result)
                    onUploadCompleteListner?.onUploadComplete(model.src)
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

    companion object {
        //           var  file: /*@@fxegna@@*/java.io.File? = this.mActivity.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        //          var  pictureDirOri: /*@@fxegna@@*/java.io.File? = java.io.File(Intrinsics.stringPlus(if (file != null)file.getAbsolutePath() else null, "/OriPicture"))
        //
        lateinit var instance: MyApplication

        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(R.color.white, R.color.black3) //全局设置主题颜色
                ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setDrawableSize(20f)
            }
            AutoSizeConfig.getInstance().setCustomFragment(true)
        }
    }

    /**
     * 保存用户信息到手机
     */
    fun saveUserInfo(userInfo: UserModel) {
        this.userInfo = userInfo
        saveUserInfo()
    }

    /**
     * 保存用户信息到手机
     */
    fun saveUserInfo() {
        if (userInfo is Serializable) {
            val editor = settings.edit()
            val baos = ByteArrayOutputStream()
            try {
                val oos = ObjectOutputStream(baos)
                oos.writeObject(userInfo) //把对象写到流里
                val temp = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
                editor.putString(USERINFO, temp)
                editor.apply()
            } catch (e: IOException) {
                Logger.e("保存用户对象失败" + e.message)
            }

        } else {
            try {
                throw Exception("User must implements Serializable")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 从手机获取用户数据对象
     *
     * @return 手机保存的用户信息对象
     */
    fun getPassword(): MutableList<PasswordModel>? {
        val temp = settings.getString(PASSWORD, "")
        val bais = ByteArrayInputStream(Base64.decode(temp!!.toByteArray(), Base64.DEFAULT))
        var user: MutableList<PasswordModel> = ArrayList()
        try {
            val ois = ObjectInputStream(bais)
            user = ois.readObject() as MutableList<PasswordModel>
        } catch (e: Exception) {
            Logger.e("读取密码失败" + e.message)
        }
        return user
    }

    /**
     * 保存账号密码到手机
     */
    fun savePassword(model: PasswordModel) {
        //        if (passwordModel is Serializable) {
        if (passwordList == null) {
            passwordList = ArrayList()
        }
        var hasThisUser = false
        for (item in passwordList!!) {
            if (item.username == model.username) {
                hasThisUser = true
                item.password = model.password
            }
        }
        if (!hasThisUser) {
            passwordList?.add(model)
        }
        val editor = settings.edit()
        val baos = ByteArrayOutputStream()
        try {
            val oos = ObjectOutputStream(baos)
            oos.writeObject(passwordList) //把对象写到流里
            val temp = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            editor.putString(PASSWORD, temp)
            editor.apply()
        } catch (e: IOException) {
            Logger.e("保存密码对象失败" + e.message)
        }

        //        } else {
        //            try {
        //                throw Exception("User must implements Serializable")
        //            } catch (e: Exception) {
        //                e.printStackTrace()
        //            }
    }

    /**
     * 从手机获取用户数据对象
     *
     * @return 手机保存的用户信息对象
     */
    fun getUserInfoFromLocal(): UserModel? {
        val temp = settings.getString(USERINFO, "")
        val bais = ByteArrayInputStream(Base64.decode(temp!!.toByteArray(), Base64.DEFAULT))
        var user: UserModel? = null
        try {
            val ois = ObjectInputStream(bais)
            user = ois.readObject() as UserModel
        } catch (e: Exception) {
            Logger.e("读取用户信息失败" + e.message)
        }
        return user
    }

    /**
     * 清空本地数据
     * 登出按钮执行此操作
     */
    fun clear() {
        userInfo = null
        settings.edit().clear().apply()
        val baos = ByteArrayOutputStream()
        try {
            val oos = ObjectOutputStream(baos)
            oos.writeObject(UserModel()) //把对象写到流里
            val temp = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            settings.edit().putString(USERINFO, temp)
            settings.edit().apply()
        } catch (e: IOException) {
            Log.e("---data---", e.toString())
        }
    }

}