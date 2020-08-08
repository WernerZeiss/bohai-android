package com.rice.bohai.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.rice.bohai.Constant
import com.rice.tool.ToastUtil
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    var result = ""

    // IWXAPI �ǵ�����app��΢��ͨ�ŵ�openapi�ӿ�
    private var api: IWXAPI? = null
    private var mContext = this

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_welcome)
        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
        api = WXAPIFactory.createWXAPI(this, Constant.WECHAT_APP_ID, false)
        api!!.handleIntent(intent, this)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this)
        finish()
    }

    override fun finish() {
        overridePendingTransition(0,0)
        super.finish()
    }

    // ΢�ŷ������󵽵�����Ӧ��ʱ����ص����÷���
    override fun onReq(req: BaseReq) {
        Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show()
    }


    // ������Ӧ�÷��͵�΢�ŵ�����������Ӧ�������ص����÷���
    override fun onResp(resp: BaseResp) {
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_USER_CANCEL -> result = "����ȡ��"
            BaseResp.ErrCode.ERR_AUTH_DENIED -> result = "���󱻾ܾ�"
            else -> {
                result = "δ֪����"
            }
        }
        when (resp.type) {
            1 -> {
                if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
//                    //                    sendThird(resp.openId,resp.)
//                    val code = (resp as SendAuth.Resp).code
//                    result = "��¼�ɹ�"
//                    if (TextUtils.isEmpty(MyApplication.instance.userInfo?.access_token)) {
//                        //��¼�ɹ�
//                        getAccessToken(code)
//                    } else {
                    //����ɹ�
//                    result = "����ɹ�"
                    finish()
//                    }
                }
            }
            2 -> {
                if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                    //                    sendThird(resp.openId,resp.)
//                    result = "����ɹ�"
                    finish()
                }
            }
        }
//        ToastUtil.showShort(result)
        Logger.d(result)
    }

    companion object {

        private val TIMELINE_SUPPORTED_VERSION = 0x21020001
    }

    //	private void goToGetMsg() {
    //		Intent intent = new Intent(this, GetFromWXActivity.class);
    //		intent.putExtras(getIntent());
    //		startActivity(intent);
    //		finish();
}

//	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//		WXMediaMessage wxMsg = showReq.message;
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//		StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
//
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();
//	}

