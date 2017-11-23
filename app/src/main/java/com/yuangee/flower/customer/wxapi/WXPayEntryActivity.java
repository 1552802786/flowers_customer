package com.yuangee.flower.customer.wxapi;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.util.ToastUtil;

public class WXPayEntryActivity extends RxBaseActivity implements IWXAPIEventHandler {

    private int errcode;
    private TextView payResult;
    private IWXAPI api;

    ProgressDialog progressHUD;

    @Override
    public int getLayoutId() {
        return R.layout.pay_result_activity;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        payResult = (TextView) findViewById(R.id.pay_result);

//        fromResource = App.me().getSharedPreferences().getString("wxPayFrom", "");

        api = WXAPIFactory.createWXAPI(this, Config.wxJKAppId);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 5:
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    ToastUtil.showMessage(WXPayEntryActivity.this, (String) message.obj);
                    break;

                case 11:
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    WXPayEntryActivity.this.finish();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onResp(BaseResp resp) {
        errcode = resp.errCode;
        Log.e("datadata", "resp.code--->" + resp.errCode);
        if (errcode == 0) {
            payResult.setText("支付成功");
            WXPayEntryActivity.this.finish();
        } else {
            payResult.setTextColor(getResources().getColor(R.color.black_alpha_45));
            payResult.setText("支付失败");
        }
    }
}