package com.rice.bohai.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.custom.PaintView
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.dialog_xieyi.*
import java.io.File

/**
 * 协议Dialog
 */
class XieyiDialog(var mContext: Context) : Dialog(mContext, R.style.centerDialog) {

    var onOkClickListener: OnOkClickListener? = null
    var onCancelClickListener: OnCancelClickListener? = null
    var xieyi = MyApplication.instance.systemModel?.certificate_pdf

    interface OnOkClickListener {
        fun onOkClick()
    }

    interface OnCancelClickListener {
        fun onCancelClick()
    }

    var view: View

    init {
        view = View.inflate(context, R.layout.dialog_xieyi, null)
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
        btn_cancel.setOnClickListener {
            if (onCancelClickListener != null) {
                onCancelClickListener?.onCancelClick()
            }
            dismiss()
        }
        btn_save.setOnClickListener {
            if (!checkbox.isChecked) {
                ToastUtil.showShort("您必须同意数字证书协议才能继续")
                return@setOnClickListener
            }
            if (onOkClickListener != null) {
                onOkClickListener?.onOkClick()
            }
            dismiss()
        }
        download()
    }

    private fun download() {
        var fileStr = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/pdfcache/" + TextUtils.getFileName(xieyi)
        var file = File(fileStr)
        if (file.exists()) {
            pdfView.fromFile(file)
                    .pageFitPolicy(FitPolicy.WIDTH) //模式，以适应视图中的页面
//                    .pageSnap(true) //将页面捕捉到屏幕边界
                    .load()
            return
        }
        FileDownloader.setup(mContext)
        FileDownloader.getImpl().create(xieyi)
                .setPath(fileStr)
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun connected(task: BaseDownloadTask, etag: String, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        progress.visibility = View.VISIBLE
                    }

                    override fun blockComplete(task: BaseDownloadTask) {}

                    override fun retry(task: BaseDownloadTask, ex: Throwable, retryingTimes: Int, soFarBytes: Int) {

                    }

                    override fun completed(task: BaseDownloadTask) {
                        progress.visibility = View.GONE
                        pdfView.fromFile(file)
                                .pageFitPolicy(FitPolicy.WIDTH) //模式，以适应视图中的页面
//                                .pageSnap(true) //将页面捕捉到屏幕边界
                                .load()
                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                    }

                    override fun warn(task: BaseDownloadTask) {}
                }).start()
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

}
