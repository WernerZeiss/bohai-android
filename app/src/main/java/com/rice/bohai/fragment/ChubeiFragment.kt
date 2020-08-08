package com.rice.bohai.fragment

import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.fangtao.ftlibrary.gson.StringNullAdapter
import com.github.salomonbrys.kotson.fromJson
import com.nineoldandroids.animation.AnimatorSet
import com.nineoldandroids.animation.ObjectAnimator
import com.nineoldandroids.animation.ValueAnimator
import com.ohmerhe.kolley.request.Http
import com.orhanobut.logger.Logger
import com.rice.aobo.model.ChubeiModel
import com.rice.base.BaseImmersionFragment
import com.rice.bohai.Constant
import com.rice.bohai.MyApplication
import com.rice.bohai.R
import com.rice.bohai.activity.ChubeiRankActivity
import com.rice.bohai.activity.ChubeiRuleActivity
import com.rice.bohai.activity.LoginActivity
import com.rice.racar.web.RiceHttpK
import com.rice.tool.ActivityUtils
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil
import com.rice.tool.UnitUtils
import kotlinx.android.synthetic.main.fragment_chubei.*
import java.util.*
import kotlin.collections.ArrayList


class ChubeiFragment : BaseImmersionFragment() {

    private val random = Random()
    private var isloaded = false
    var money: String = ""
    var money0: Float = 0.00f
    var money1: Float = 0.00f
    var speed: Int = 10000
    var isPlayed0 = false
    var isPlayed1 = false
    var halfHeight: Int = 0
    var halfWidth: Int = 0
    var imageViews: ArrayList<ImageView> = ArrayList<ImageView>()
    var curIndex: Int = 0
    val NUM: Int = 4

    override val contentViewLayoutID: Int
        get() = R.layout.fragment_chubei

    override fun initView() {
        halfHeight = UnitUtils.getScreenHeight(mContext) / 2
        halfWidth = UnitUtils.getScreenWidth(mContext) / 2
        genImageView()
        textviewGuize.setOnClickListener() {
            ActivityUtils.openActivity(mContext, ChubeiRuleActivity::class.java)
        }
        textviewRank.setOnClickListener() {
            ActivityUtils.openActivity(mContext, ChubeiRankActivity::class.java)
        }
//        initData()
    }

    private fun genImageView() {
        var randomx = intArrayOf(
            UnitUtils.dp2px(mContext, 30f),
            UnitUtils.dp2px(mContext, 60f),
            UnitUtils.dp2px(mContext, 160f),
            UnitUtils.dp2px(mContext, 260f)
        )
        var randomy = intArrayOf(
            UnitUtils.dp2px(mContext, 100f),
            UnitUtils.dp2px(mContext, 90f),
            UnitUtils.dp2px(mContext, 80f),
            UnitUtils.dp2px(mContext, 70f)
        )
        var with = UnitUtils.dp2px(mContext, 50f)
        for (i in 0 until NUM) {
            var imageView = ImageView(mContext)
            imageViews.add(imageView)
            imageView.tag = i + 1
            imageView.setBackgroundResource(R.drawable.coin)
            var params = FrameLayout.LayoutParams(
                with,
                with
            )
            params.topMargin = randomy[i]
            params.leftMargin = randomx[i]
            imageView.layoutParams = params
            rootlayout.addView(imageView)
        }
    }

    fun initData() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            ToastUtil.showShort("请先登录")
            ActivityUtils.openActivity(mContext, LoginActivity::class.java)
            return
        }
        if (!isloaded) {
            isloaded = true
            initChubeiMoney()
        }
//        for (i in 0 until NUM) {
//            Log.i("hel->AAA", "init:${i},${imageViews[i].x},${imageViews[i].y}");
//        }
        if (!isPlayed1) {
            isPlayed1 = true
            handler.sendEmptyMessageDelayed(1, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isPlayed0) {
            isPlayed0 = true
            curIndex = 0
            handler.sendEmptyMessage(2)
        }
    }

    private fun initChubeiMoney() {
        if (MyApplication.instance.userInfo == null || TextUtils.isEmpty(MyApplication.instance.userInfo!!.access_token)) {
            return
        }
        Http.post {
            url = RiceHttpK.getUrl(Constant.CHUBEI_MONEY)
            params {
                "access_token" - MyApplication.instance.userInfo!!.access_token
            }
            onSuccess { byts ->
                Log.i("hel->", url)
                val result = RiceHttpK.getResult(mContext, byts)
                Log.i("hel->", "$result")
                if (TextUtils.isNotEmpty(result)) {
                    var model: ChubeiModel = StringNullAdapter.gson.fromJson(result)
                    money = model.show_money
                    money0 = money.toFloat()
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

    val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    speed++
                    money1 += speed
                    if (money1 < money0) {
                        Thread.sleep(10)
                        textview_money.text = "￥${money1}"
                        sendEmptyMessage(1)
                    }
                }
                2 -> {
                    playAnimation()
                }
            }
        }
    }

    private fun playAnimation() {
//        Log.i("hel->AAA", "${curIndex},${imageViews[curIndex].x},${imageViews[curIndex].y}");

        val alphaAnimation1 = ObjectAnimator.ofFloat(imageViews[curIndex], "alpha", 1f, 0.4f)
        alphaAnimation1.interpolator = LinearInterpolator()
        alphaAnimation1.repeatMode = ValueAnimator.RESTART
        alphaAnimation1.repeatCount = ValueAnimator.INFINITE

        val scaleAnimationx = ObjectAnimator.ofFloat(imageViews[curIndex], "scaleX", 1f, 0.7f)
        scaleAnimationx.interpolator = LinearInterpolator()
        scaleAnimationx.repeatMode = ValueAnimator.RESTART
        scaleAnimationx.repeatCount = ValueAnimator.INFINITE

        val scaleAnimationy = ObjectAnimator.ofFloat(imageViews[curIndex], "scaleY", 1f, 0.7f)
        scaleAnimationy.interpolator = LinearInterpolator()
        scaleAnimationy.repeatMode = ValueAnimator.RESTART
        scaleAnimationy.repeatCount = ValueAnimator.INFINITE

        val translateAnimationx =
            ObjectAnimator.ofFloat(
                imageViews[curIndex],
                "translationX",
                0f,
                halfWidth - imageViews[curIndex].x
            )
        translateAnimationx.interpolator = LinearInterpolator()
        translateAnimationx.repeatMode = ValueAnimator.RESTART
        translateAnimationx.repeatCount = ValueAnimator.INFINITE

        val translateAnimationy =
            ObjectAnimator.ofFloat(
                imageViews[curIndex],
                "translationY",
                0f,
                halfHeight - imageViews[curIndex].y
            )
        translateAnimationy.interpolator = LinearInterpolator()
        translateAnimationy.repeatMode = ValueAnimator.RESTART
        translateAnimationy.repeatCount = ValueAnimator.INFINITE

        var animatorSet = AnimatorSet()
        animatorSet.playTogether(
            alphaAnimation1,
            scaleAnimationx,
            scaleAnimationy,
            translateAnimationx,
            translateAnimationy
        )
        animatorSet.duration = 1500
        animatorSet.start()

        curIndex++
        if (curIndex >= NUM) {
            curIndex = 0
        }
        handler.sendEmptyMessageDelayed(2, 2000L)
    }


}