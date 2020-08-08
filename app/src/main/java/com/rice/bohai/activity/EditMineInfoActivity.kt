package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.dubhe.imageselector.ClipImageActivity
import com.dubhe.imageselector.ImageSelector
import com.dubhe.imageselector.ImageSelector.OnProcessFinishListener
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.dialog.InputDialog
import com.rice.imageloader.GlideLoadUtils
import com.rice.racar.web.PublicModel
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_edit_mine_info.*
import java.nio.charset.Charset

@SuppressLint("Registered")
class EditMineInfoActivity : RiceBaseActivity() {

    lateinit var inputDialog: InputDialog

    override fun getLayoutId(): Int {
        return R.layout.activity_edit_mine_info
    }

    override fun initView() {
        inputDialog = InputDialog(mContext, "")
        ImageSelector.getInstance(this@EditMineInfoActivity) //初始化图片选择器对象，参数是Activity
            .setEnableClip(true) //是否裁剪图片
            .setClipMode(ClipImageActivity.TYPE_CIRCLE).onProcessFinishListener =
            OnProcessFinishListener { path ->
                MyApplication.instance.onUploadCompleteListner =
                    object : MyApplication.OnUploadCompleteListner {
                        override fun onUploadComplete(src: String) {
                            updateHeader(src)
                        }
                    }
                MyApplication.instance.uploadImage(path)
            }
        llChangeHeader.setOnClickListener {
            //改头像
            ImageSelector.getInstance(this@EditMineInfoActivity).showImageSelectMenu()
        }
        textName.setOnClickListener {
            //改昵称
            inputDialog.setTitle("请输入新昵称")
            inputDialog.setInputHint("请输入新昵称")
            inputDialog.onOkClickListener = object : InputDialog.OnOkClickListener {
                override fun onOkClick(str: String) {
                    updateName(str)
                }
            }
            if (!inputDialog.isShowing) {
                inputDialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageSelector.getInstance(this@EditMineInfoActivity)
            .onHeaderResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        ImageSelector.getInstance(this@EditMineInfoActivity).clear()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance.onUserInfoUpdateCompleteListener =
            object : MyApplication.OnUserInfoUpdateCompleteListener {
                override fun onUserInfoUpdateComplete() {
                    GlideLoadUtils.getInstance().glideLoad(
                        mContext,
                        MyApplication.instance.userInfo?.avatar,
                        imgHeader,
                        true
                    )
                    textName.text = MyApplication.instance.userInfo?.display_name
                }
            }
        MyApplication.instance.getUserInfoFromWeb()
    }

    /**
     * 修改昵称
     */
    private fun updateName(nickname: String) {
        Http.post {
            url = RiceHttpK.getUrl(Constant.UPDATE_USER)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "nickname" - nickname
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

    override fun getIntentData() {

    }

    override fun clear() {

    }

}