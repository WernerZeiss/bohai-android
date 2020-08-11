package com.rice.bohai.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.rice.base.RiceBaseActivity
import com.rice.bohai.R
import com.rice.imageloader.GlideLoadUtils
import kotlinx.android.synthetic.main.activity_picture.*

/**
 * @author CWQ
 * @date 2020/8/11
 */
class PictureActivity : RiceBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_picture
    }

    override fun initView() {
    }

    override fun getIntentData() {
        val imgUrl = intent.getStringExtra("imgUrl")
        val imgRes = intent.getIntExtra("imgRes", 0)
        if (!TextUtils.isEmpty(imgUrl)) {
            GlideLoadUtils.getInstance().glideLoad(this, imgUrl, iv_pic)
        } else if (imgRes != 0) {
            iv_pic.setImageResource(imgRes)
        } else {
            finish()
        }
    }

    override fun clear() {
    }


    companion object {
        fun into(context: Context, imgUrl: String = "", @DrawableRes imgRes: Int = 0) {
            val intent = Intent(context, PictureActivity::class.java)
            intent.putExtra("imgUrl", imgUrl)
            intent.putExtra("imgRes", imgRes)
            context.startActivity(intent)
        }
    }
}