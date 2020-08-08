package com.rice.bohai.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.rice.bohai.R
import com.rice.tool.PermissionUtils
import com.rice.tool.ProcessUtils
import kotlinx.android.synthetic.main.dialog_sell_success.*

/**
 * 卖出成功Dialog
 */
class SellSuccessDialog(var mContext: Context, var infoStr: String = "") :
        Dialog(mContext, R.style.centerDialog) {

    var onOkClickListener: OnOkClickListener? = null
    var onCancelClickListener: OnCancelClickListener? = null

    interface OnOkClickListener {
        fun onOkClick(filePath: String)
    }

    interface OnCancelClickListener {
        fun onCancelClick()
    }

    var view: View

    init {
        view = View.inflate(context, R.layout.dialog_sell_success, null)
        setContentView(view)
        val window = this.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window.setGravity(Gravity.CENTER)
        val lp = window.attributes
        //设置宽
        lp.width = mContext.resources.getDimensionPixelOffset(R.dimen.width_ok_cancel_dialog)
        //设置高
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(0))
        initView()
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    fun setTitleText(title: String): SellSuccessDialog {
        textTitle.text = title
        return this@SellSuccessDialog
    }

    fun setInfo(info: String): SellSuccessDialog {
        infoStr = info
        textInfo.text = infoStr
        return this@SellSuccessDialog
    }

    fun setBigText(bigText:String):SellSuccessDialog{
        textBigText.text = bigText
        return this@SellSuccessDialog
    }

    private fun initView() {
        textInfo.text = infoStr
        imgClose.setOnClickListener {
            dismiss()
        }
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
        if (ProcessUtils.isAppForeground(mContext, "com.rice.bohai") && PermissionUtils.checkFloatPermission(mContext)) {
            //        if (BackgroundUtil.getApplicationValue(mContext,"com.rice.bohai")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window?.setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY))
            } else {
                window?.setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT))
            }
            super.show()
        }
    }

}
