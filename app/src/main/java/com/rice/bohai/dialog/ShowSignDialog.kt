package com.rice.bohai.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.rice.activity.BannerDetailActivity
import com.rice.activity.WebViewActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.custom.PaintView
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.ActivityUtils
import com.rice.tool.ScreenUtils
import com.rice.tool.TextUtils
import kotlinx.android.synthetic.main.dialog_show_sign.*
import kotlinx.android.synthetic.main.paint_layout.*
import me.jessyan.autosize.AutoSize
import java.text.SimpleDateFormat
import java.util.*

/**
 * 展示签名Dialog
 */
class ShowSignDialog(var mContext: Context) : Dialog(mContext, R.style.centerDialog) {

    var onOkClickListener: OnOkClickListener? = null
    var onCancelClickListener: OnCancelClickListener? = null
    var src = ""

    interface OnOkClickListener {
        fun onOkClick(filePath: String)
    }

    interface OnCancelClickListener {
        fun onCancelClick()
    }

    var view: View

    init {
        view = View.inflate(context, R.layout.dialog_show_sign, null)
        setContentView(view)
        val window = this.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window.setGravity(Gravity.CENTER)
        val lp = window.attributes
        //设置宽
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        //设置高
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(0))
        initView()
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    fun setImg(src: String) {
        this@ShowSignDialog.src = src
        initView()
    }

    private fun initView() {
        imgClose.setOnClickListener { dismiss() }
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), src), img)
    }

    override fun show() {
        //        AutoSize.autoConvertDensity()
        //        @Override
        //        public Resources getResources() {
        //            //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
        //            AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources());//如果没有自定义需求用这个方法
        //            AutoSizeCompat.autoConvertDensity((super.getResources(), 667, false);//如果有自定义需求就用这个方法
        //            return super.getResources();
        //        }
        super.show()
    }

}
