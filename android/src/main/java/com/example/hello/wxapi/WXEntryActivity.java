package com.example.hello.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, "wx850d21e8fc2f523f", false);

        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                break;
            default:
                break;
        }

        switch (resp.getType()){
            case ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE: //一次性订阅信息
                SubscribeMessage.Resp subscribeMsgResp = (SubscribeMessage.Resp) resp;
                break;
            case ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM://小程序
                WXLaunchMiniProgram.Resp launchMiniProgramResp = (WXLaunchMiniProgram.Resp) resp;
                break;
            case ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW://签约
                WXOpenBusinessView.Resp openBusinessView = (WXOpenBusinessView.Resp) resp;
                break;
            case ConstantsAPI.COMMAND_OPEN_BUSINESS_WEBVIEW://签约
                WXOpenBusinessWebview.Resp response = (WXOpenBusinessWebview.Resp) resp;
                break;
            case ConstantsAPI.COMMAND_SENDAUTH: //认证
                SendAuth.Resp authResp = (SendAuth.Resp)resp;
                final String code = authResp.code;
                //CCM_WECHAT_ACTION
                Intent intent = new Intent("CCM_WECHAT_ACTION");
                intent.putExtra("code", code);
                sendBroadcast(intent);
                break;
            case ConstantsAPI.COMMAND_PAY_BY_WX://微信支付
                //0成功,-1错误,-2用户取消
                //resp.errCode
                break;
            default:
                break;
        }

        finish();
    }
}
