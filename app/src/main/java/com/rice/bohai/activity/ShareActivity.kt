package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Environment
import com.rice.base.RiceBaseActivity
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_share.*
import java.io.File
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.net.Uri
import com.rice.bohai.tools.ShareCallBack
import com.rice.bohai.tools.ShareUtils
import com.xyzlf.share.library.interfaces.ShareConstant
import java.io.FileOutputStream
import java.util.*


/**
 * 邀请好友
 */
@SuppressLint("Registered")
class ShareActivity : RiceBaseActivity() {

    var qrcode_src = "" //二维码图片
    var bg_src = "" //背景图片
    var bitmap: Bitmap? = null

    init {
        navigationBarColorId = R.color.white
        statusBarColorId = R.color.white
        isWhiteStatusBarIcon = false
        isWhiteNavigationBarIcon = false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_share
    }

    override fun initView() {
        textPhone.text = TextUtils.hindPhoneNumber(MyApplication.instance.userInfo?.user_phone ?: "")
        textShareCode.text = MyApplication.instance.userInfo?.invite_code
        if (TextUtils.isNotEmpty(qrcode_src)) {
            GlideLoadUtils.getInstance().glideLoad(mContext, qrcode_src, imageView3)
        }
        if (TextUtils.isNotEmpty(bg_src)) {
            GlideLoadUtils.getInstance().glideLoad(mContext, bg_src, imgBg)
        }
        textShare.setOnClickListener {
            ShareUtils.shareImg(this@ShareActivity, "渤海交易所",
                    "您的好友${MyApplication.instance.userInfo?.user_phone}邀请你共享渤海现货交易红利",
                    getBitmapInstance(), true, ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND)
        }
        textSave.setOnClickListener {
            var dir = Environment.getExternalStorageDirectory().absolutePath + "/bohai/share/"
            if (!File(dir).exists()) {
                File(dir).mkdirs()
            }
            textShare.visibility = View.INVISIBLE
            textSave.visibility = View.INVISIBLE
            try {
                val file = File("$dir${Random().nextInt(Integer.MAX_VALUE)}.jpg")
                val out = FileOutputStream(file)
                getBitmapInstance().compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                val saveAs = file.path
                val contentUri = Uri.fromFile(File(saveAs))
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri)
                sendBroadcast(mediaScanIntent)
                ToastUtil.showShort("保存成功")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtil.showShort("保存失败，请检查是否授予了文件存储权限")
            }finally {
                textShare.visibility = View.VISIBLE
                textSave.visibility = View.VISIBLE
            }
        }
    }

    private fun getBitmapInstance(): Bitmap {
        if (bitmap == null) {
            frame.setDrawingCacheEnabled(true)
            frame.buildDrawingCache()  //启用DrawingCache并创建位图
            bitmap = Bitmap.createBitmap(frame.getDrawingCache()) //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
            frame.setDrawingCacheEnabled(false)  //禁用DrawingCahce否则会影响性能
        }
        return bitmap!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * 分享回调处理
         */
        if (requestCode == ShareConstant.REQUEST_CODE) {
            if (data != null) {
                val channel = data.getIntExtra(ShareConstant.EXTRA_SHARE_CHANNEL, -1)
                val status = data.getIntExtra(ShareConstant.EXTRA_SHARE_STATUS, -1)
                onShareCallback(channel, status)
            }
        }
    }

    /**
     * 分享回调处理
     * @param channel
     * @param status
     */
    private fun onShareCallback(channel: Int, status: Int) {
        ShareCallBack().onShareCallback(channel, status)
    }

    private fun loadBitmapFromView(v: View): Bitmap {
        val w = v.getWidth()
        val h = v.getHeight()
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)

        c.drawColor(Color.WHITE)
        /** 如果不设置canvas画布为白色，则生成透明  */

        v.layout(0, 0, w, h)
        v.draw(c)

        return bmp
    }

    override fun getIntentData() {
        qrcode_src = intent.extras?.getString("qrcode_src", "") ?: ""
        bg_src = intent.extras?.getString("bg_src", "") ?: ""
    }

    override fun clear() {

    }

}