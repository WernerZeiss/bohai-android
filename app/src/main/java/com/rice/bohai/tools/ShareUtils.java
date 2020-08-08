package com.rice.bohai.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import com.rice.bohai.R;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;

public class ShareUtils {

    /**
     * 分享Url
     *
     * @param activity 调用的Activity
     * @param title    分享链接标题
     * @param content  分享链接描述
     * @param url      分享链接
     * @param imgUrl   分享图片链接
     * @param shareTo  分享渠道 在ShareConstant中的静态变量
     */
    public static void shareUrl(AppCompatActivity activity, String title, String content, String url, String imgUrl, int shareTo) {
        ShareEntity testBean = new ShareEntity(title, content);
        testBean.setUrl(url);
        testBean.setImgUrl(imgUrl);
        ShareUtil.startShare(activity, shareTo, testBean, ShareConstant.REQUEST_CODE);
    }

    /**
     * 分享图片
     *
     * @param activity 调用的Activity
     * @param title    分享链接标题
     * @param content  分享链接描述
     * @param img      分享的图片
     * @param isBigImg 分享大图，仅微信支持
     * @param shareTo  分享渠道 在ShareConstant中的静态变量
     */
    public static void shareImg(AppCompatActivity activity, String title, String content, Bitmap img, boolean isBigImg, int shareTo) {
        ShareEntity testBean = new ShareEntity(title, content);
        String filePath = ShareUtil.saveBitmapToSDCard(activity, img);
        testBean.setImgUrl(filePath);
        if (isBigImg) {
            testBean.setShareBigImg(true);
        }
        ShareUtil.startShare(activity, shareTo, testBean, ShareConstant.REQUEST_CODE);
    }

    /**
     * 默认以小图模式分享图片
     */
    public static void shareImg(AppCompatActivity activity, String title, String content, Bitmap img, int shareTo) {
        shareImg(activity, title, content, img, false, shareTo);
    }

}
