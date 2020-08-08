package com.rice.bohai.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.activity.WebViewInAppActivity
import com.rice.base.RiceBaseActivity
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.adapter.XieyiAdapter
import com.rice.bohai.dialog.ShowSignDialog
import com.rice.bohai.model.XieyiListModel
import com.rice.bohai.model.XieyiModel
import com.rice.dialog.OkCancelDialog
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import kotlinx.android.synthetic.main.activity_smr_recycler_match_line.*
import kotlinx.android.synthetic.main.include_smr_recycler_match.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@SuppressLint("Registered")
class MyXieyiActivity : RiceBaseActivity() {

    var list: MutableList<XieyiModel> = ArrayList()
    lateinit var xieyiAdapter: XieyiAdapter
    var page = 1
    val filePath = Environment.getExternalStorageDirectory().absolutePath + "/bohai/protocol/"
    lateinit var overRideDialog: OkCancelDialog
    lateinit var showSignDialog: ShowSignDialog

    override fun getLayoutId(): Int {
        return R.layout.activity_smr_recycler_match_line
    }

    override fun initView() {
        overRideDialog = OkCancelDialog(mContext)
        showSignDialog = ShowSignDialog(mContext)
        toolbar.setTitle("我的协议")
        recycler.layoutManager = LinearLayoutManager(mContext)
        xieyiAdapter = XieyiAdapter(mContext, list)
        xieyiAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.textBtnSee -> {
                    //查看图片
                    //                    showSignDialog.setImg(list[position].image)
                    //                    if (!showSignDialog.isShowing) {
                    //                        showSignDialog.show()
                    //                    }
                    var b = Bundle()
                    b.putString("url", list[position].sign_url)
                    ActivityUtils.openActivity(mContext, WebViewInAppActivity::class.java, b)
                }
                R.id.textBtnDownload -> {
                    //下载协议
                    download(list[position].id.toString(), list[position].name)
                }
            }
        }
        xieyiAdapter.bindToRecyclerView(recycler)
        xieyiAdapter.setEmptyView(R.layout.loading_dialog_gray2)
        recycler.adapter = xieyiAdapter
        refresh.setOnLoadMoreListener {
            page++
            initData()
        }
        refresh.setOnRefreshListener {
            page = 1
            initData()
        }
        initData()
    }

    /***
     * 加载协议列表
     */
    private fun initData() {
        Http.post {
            url = RiceHttpK.getUrl(Constant.MY_PROTOCOL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "page" - page.toString()
            }
            onFinish {
                refresh.finishLoadMore()
                refresh.finishRefresh()
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                xieyiAdapter.setEmptyView(R.layout.include_no_data)
                val result = RiceHttpK.getResult(mContext,byts)
                if (TextUtils.isNotEmpty(result)) {
                    val model: XieyiListModel = StringNullAdapter.gson.fromJson(result)
                    if (page == 1) {
                        list.clear()
                    }
                    list.addAll(model.lists)
                    xieyiAdapter.notifyDataSetChanged()
                }
            }
            onFail { error ->
                xieyiAdapter.setEmptyView(R.layout.include_fail)
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
     * 下载协议pdf
     */
    private fun download(id: String, name: String) {
        Http.get {
            url = RiceHttpK.getUrl(Constant.DOWNLOAD_PROTOCOL)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
                "id" - id
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                var file = File("$filePath$id.$name.pdf")
                if (file.exists()) {
                    overRideDialog.setInfo("此协议已经下载过，要重新下载吗？")
                    overRideDialog.onOkClickListener = object : OkCancelDialog.OnOkClickListener {
                        override fun onOkClick() {
                            byte2File(byts, "$id.$name.pdf")
                        }
                    }
                    if (!overRideDialog.isShowing) {
                        overRideDialog.show()
                    }
                } else {
                    byte2File(byts, "$id.$name.pdf")
                }
            }
            onFail { error ->
                xieyiAdapter.setEmptyView(R.layout.include_fail)
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
     * 辅助方法，byte数组保存为本地文件
     * @param buf
     * @param filePath
     * @param fileName
     */
    private fun byte2File(buf: ByteArray, fileName: String) {
        var bos: BufferedOutputStream? = null
        var fos: FileOutputStream? = null

        try {
            val dir = File(filePath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            var file = File(filePath + File.separator + fileName)
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)
            bos.write(buf)
            ToastUtil.showShort("下载成功，文件保存在$filePath")
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showShort("下载出错，请确认授予了文件存储权限")
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getIntentData() {

    }

    override fun clear() {

    }

}