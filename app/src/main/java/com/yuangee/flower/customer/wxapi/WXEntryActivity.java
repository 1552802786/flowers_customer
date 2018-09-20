package com.yuangee.flower.customer.wxapi;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.AppBus;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.WechatEvent;

import static com.tencent.mm.sdk.modelbase.BaseResp.ErrCode.ERR_AUTH_DENIED;
import static com.tencent.mm.sdk.modelbase.BaseResp.ErrCode.ERR_USER_CANCEL;

public class WXEntryActivity extends RxBaseActivity implements IWXAPIEventHandler {
    private int errcode;
    private TextView payResult;
    private IWXAPI api;
    private TextView title;

    @Override
    public int getLayoutId() {
        return R.layout.pay_result_activity;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        payResult = findViewById(R.id.pay_result);

//        fromResource = App.me().getSharedPreferences().getString("wxPayFrom", "");

        api = WXAPIFactory.createWXAPI(this, Config.wxJKAppId);
        api.handleIntent(getIntent(), this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = findViewById(R.id.title_str);
        title.setText("授权结果");
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        errcode = resp.errCode;
        Log.e("datadata", "resp.code--->" + resp.errCode);
        if (errcode == 0) {
            AppBus.getInstance().post(true);
            payResult.setText("授权成功");
            WXEntryActivity.this.finish();
        } else if (errcode == ERR_AUTH_DENIED) {
            payResult.setTextColor(getResources().getColor(R.color.black_alpha_45));
            payResult.setText("用户拒绝授权");
        } else if (errcode == ERR_USER_CANCEL) {
            payResult.setTextColor(getResources().getColor(R.color.black_alpha_45));
            payResult.setText("用户取消授权");
        }
    }
}
