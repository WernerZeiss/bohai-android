package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.os.Environment
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.rice.base.RiceBaseActivity
import com.rice.bohai.R
import com.rice.tool.TextUtils
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File


@SuppressLint("Registered")
class PDFActivity : RiceBaseActivity() {

    var url = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_pdf
    }

    override fun initView() {
        download()
    }

    private fun download() {
        var fileStr = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/pdfcache/" + TextUtils.getFileName(url)
        var file = File(fileStr)
        if (file.exists()) {
            pdfView.fromFile(file).load()
            return
        }
        FileDownloader.setup(mContext)
        FileDownloader.getImpl().create(url)
                .setPath(fileStr)
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun connected(task: BaseDownloadTask, etag: String, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun blockComplete(task: BaseDownloadTask) {}

                    override fun retry(task: BaseDownloadTask, ex: Throwable, retryingTimes: Int, soFarBytes: Int) {

                    }

                    override fun completed(task: BaseDownloadTask) {
                        pdfView.fromFile(file).load()
                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                    }

                    override fun warn(task: BaseDownloadTask) {}
                }).start()
    }

    override fun getIntentData() {
        url = intent.extras?.getString("url") ?: ""
    }

    override fun clear() {

    }

}