package com.rice.bohai.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.PDFActivity
import com.rice.bohai.custom.PaintView
import com.rice.tool.ActivityUtils
import com.rice.tool.ScreenUtils
import kotlinx.android.synthetic.main.paint_layout.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 签名Dialog
 */
class SignDialog(var mContext: Context) : Dialog(mContext, R.style.centerDialog) {

    var onOkClickListener: OnOkClickListener? = null
    var onCancelClickListener: OnCancelClickListener? = null
    private var fileName: String? = null
    var xieyi = MyApplication.instance.systemModel?.sign_agreement

    interface OnOkClickListener {
        fun onOkClick(filePath: String)
    }

    interface OnCancelClickListener {
        fun onCancelClick()
    }

    var view: View

    init {
        view = View.inflate(context, R.layout.paint_layout, null)
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

    fun setCancleText(text: String) {
        btn_cancel.text = text
    }

    fun setOkText(text: String) {
        btn_save.text = text
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView() {
        //获得系统当前时间，并以该时间作为文件名
        val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
        val curDate = Date(System.currentTimeMillis()) //获取当前时间
        fileName = MyApplication.instance.imageFilePath + "paint" + formatter.format(curDate) + ".png"
        btn_cancel.setOnClickListener {
            if (onCancelClickListener != null) {
                onCancelClickListener!!.onCancelClick()
            }
            dismiss()
        }
        btn_save.setOnClickListener {
            if (onOkClickListener != null) {
                paintViewPad?.saveToSDCard(fileName)
                onOkClickListener!!.onOkClick(fileName ?: "")
            }
            dismiss()
        }
        textXeiyi.setOnClickListener {
            //            var b = Bundle()
            //            b.putBoolean("textX", true)
            //            b.putString("url", xieyi)
            //            b.putString("title", mContext.resources.getString(R.string.sign_agreement))
            //            ActivityUtils.openActivity(mContext, BannerDetailActivity::class.java, b)
            var b = Bundle()
            b.putString("url", xieyi)
            ActivityUtils.openActivity(mContext, PDFActivity::class.java, b)
        }
        initData(null)
    }

    var paintViewPad: PaintView? = null

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

    private fun initData(fileName: String?) {
        //获取的是屏幕宽高，通过控制freamlayout来控制涂鸦板大小
        //        val defaultDisplay = context.getWindowManager().getDefaultDisplay()
        //        val screenWidth = defaultDisplay.getWidth()
        //        val screenHeight = defaultDisplay.getHeight() - 110
        paint_linear.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (paintViewPad == null) {
                val screenWidth = ScreenUtils.getRealScreenWidth(MyApplication.instance)
                val screenHeight = ScreenUtils.getRealScreenHeight(MyApplication.instance)
                paintViewPad = PaintView(context, screenWidth, screenHeight, fileName)
                paint_linear.addView(paintViewPad)
                paintViewPad?.requestFocus()
                paintViewPad?.selectPaintColor(Color.BLACK)
                paintViewPad?.selectPaintStyle(1)
                paintViewPad?.selectPaintSize(9)
                btn_clean_paint.setOnClickListener {
                    paintViewPad?.clean()
                }
                var lp = paintViewPad?.layoutParams
                lp?.width = ViewGroup.LayoutParams.MATCH_PARENT
                lp?.height = ViewGroup.LayoutParams.MATCH_PARENT
                paintViewPad?.layoutParams = lp
            }
        }
    }


}
