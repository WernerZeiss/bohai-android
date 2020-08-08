package com.rice.bohai.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.rice.bohai.Constant;
import com.rice.bohai.MyApplication;
import com.rice.tool.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
    private String payType = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constant.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    public void onResp(BaseResp resp) {
        Logger.d("errCode:" + resp.errCode);
        PayResp payResp = (PayResp) resp;
        if (payResp.extData != null) {
            payType = payResp.extData;
        }
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    ToastUtil.showShort("支付成功");
                    MyApplication.instance.getUserInfoFromWeb();
                    break;
                case -1:
                    ToastUtil.showShort("支付失败");
                    break;
                case -2:
                    ToastUtil.showShort("支付取消");
                    break;
            }
            finish();
            overridePendingTransition(0, 0);//去掉动画避免闪烁
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }
}