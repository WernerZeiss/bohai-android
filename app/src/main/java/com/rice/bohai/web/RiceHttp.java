package com.rice.bohai.web;

import android.text.TextUtils;
import android.util.Log;

/**
 * 网络请求封装
 */
public class RiceHttp {

    private static String base_url = "";//服务器地址
    private static String token = "";//用户token
    private static final String TAG = "---RiceHttpK---";
    private static final int SUCCESS = 200;//操作成功的状态码
    private static final int FAIL = -1;//请求成功但操作失败的状态码
    private static final int NO_TOKEN = -1001;//请求成功但Token失效的状态码
    private OnSuccessListener onSuccessListener = null;
    private OnFaildListener onFaildListener = null;
    private OnNoTokenListener onNoTokenListener = null;

    public static String getBase_url() {
        return base_url;
    }

    public static String getToken() {
        return token;
    }

    public static void setBase_url(String base_url) {
        RiceHttp.base_url = base_url;
    }

    public static void setToken(String token) {
        RiceHttp.token = token;
    }

    /**
     * 在Application初始化时调用，设置服务器地址
     */
    public static void init(String base) {
        if (TextUtils.isEmpty(base)) {
            Log.e(TAG, "警告，服务器地址为空");
        }
        RiceHttp.base_url = base;
    }

    /**
     * 拼接URL
     */
    public static String getUrl(String api) {
        if (TextUtils.isEmpty(api)) {
            Log.e(TAG, "警告，api地址为空");
        }
        return base_url + api;
    }

    /**
     * 拼接URL和token
     */
    public static String getUrlWithToken(String api) {
        if (TextUtils.isEmpty(api)) {
            Log.e(TAG, "警告，api地址为空");
        }
        if (TextUtils.isEmpty(token)) {
            Log.e(TAG, "警告，token为空");
        }
        return base_url + api + "?token=" + token;
    }

    public void getResult() {

    }

    public interface OnSuccessListener<T> {
        void onSuccess(T model);
    }

    public interface OnFaildListener {
        void onFaild(String msg);
    }

    public interface OnNoTokenListener {
        void onNoToken(String msg);
    }

}
