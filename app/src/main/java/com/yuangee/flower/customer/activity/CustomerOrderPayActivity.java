package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonElement;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.AppBus;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.CustomerOrder;
import com.yuangee.flower.customer.entity.PayResult;
import com.yuangee.flower.customer.entity.ShopOrder;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by admin on 2018/2/5.
 */

public class CustomerOrderPayActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.alipay_button)
    RadioButton alipayBtn;
    @BindView(R.id.weixinpay_button)
    RadioButton weixinPayBtn;
    @BindView(R.id.pay_tips)
    TextView payTips;

    private Context mContext;
    private CustomerOrder order;

    @OnClick(R.id.start_pay)
    void startPay() {
        if (alipayBtn.isChecked()) {
            if (order.bespeak) {
                if (order.status == ShopOrder.ORDER_STATUS_NOTPAY) {
                    payYuyueZfb(order.id);//预约单支付预约金
                } else if (order.status == ShopOrder.ORDER_STATUS_BE_BACK) {
                    payJishiZfb(order.id, 1);//预约单支付尾款
                }
            } else {
                payJishiZfb(order.id, 0);//即时单支付全款
            }
        } else {
            if (order.bespeak) {
                if (order.status == ShopOrder.ORDER_STATUS_NOTPAY) {
                    payYuyueWx(order.id);//预约单支付预约金
                } else if (order.status == ShopOrder.ORDER_STATUS_BE_BACK) {
                    payJishiWx(order.id, 1);//预约单支付尾款
                }
            } else {
                payJishiWx(order.id, 0);//即时单支付全款
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.customer_order_pay_activity;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mContext = this;
        double payNumber;
        order = (CustomerOrder) getIntent().getSerializableExtra("order");
        if (order.status == CustomerOrder.ORDER_STATUS_BE_BACK) {
            payNumber = order.bespeakMoney;
        } else {
            payNumber = order.realPay;
        }

        payTips.setText(Html.fromHtml("您本次共需支付 <font color=\"#ff0000\">" + payNumber + "</font>元,请选择您的付款方式:"));
        alipayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weixinPayBtn.setChecked(false);
            }
        });
        weixinPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alipayBtn.setChecked(false);
            }
        });
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("支付");
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getBooleanExtra("shoppingCart", false)) {
            startActivity(new Intent(mContext, CustomerOrderActivity.class));
        }
    }

    private void payYuyueZfb(Long orderId) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payYuyueSingleZfb(orderId)
                .map(new HttpResultFunc<ZfbResult>(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(mContext, true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    private void payJishiZfb(Long orderId, Integer type) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payJishiSingleZfb(orderId, type)
                .map(new HttpResultFunc<ZfbResult>(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(mContext, true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    private void payYuyueWx(Long orderId) {
        Observable<JsonElement> observable = ApiManager.getInstance().api
                .payYuyueSingleWx(orderId)
                .map(new HttpResultFunc<JsonElement>(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(mContext, true,
                false, new NoErrSubscriberListener<JsonElement>() {
            @Override
            public void onNext(JsonElement jsonElement) {

                detailWxPay(jsonElement);
            }
        })));
    }

    private void payJishiWx(Long orderId, Integer type) {
        Observable<JsonElement> observable = ApiManager.getInstance().api
                .payJishiSingleWx(orderId, type)
                .map(new HttpResultFunc<JsonElement>(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<JsonElement>(mContext, true,
                false, new NoErrSubscriberListener<JsonElement>() {
            @Override
            public void onNext(JsonElement jsonElement) {
                detailWxPay(jsonElement);
            }
        })));
    }

    private void detailZfb(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(CustomerOrderPayActivity.this);
                String result = alipay
                        .pay(s, true);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void detailWxPay(JsonElement jsonElement) {
        try {
            JSONObject json = new JSONObject(jsonElement.toString());
            if (!json.has("retcode")) {
                PayReq req = new PayReq();
                req.appId = json.getString("appid");
                req.partnerId = json.getString("mch_id");
                req.prepayId = json.getString("prepay_id");
                req.nonceStr = json.getString("nonce_str");
                req.timeStamp = System.currentTimeMillis() / 1000 + "";
                req.packageValue = "Sign=WXPay";
                req.sign = genAppSign(req, json.getString("apiKey"));
                Log.e("wxPay", genAppSign(req, json.getString("apiKey")));

                IWXAPI api = WXAPIFactory.createWXAPI(mContext, null);
                api.registerApp(req.appId);
                api.sendReq(req);
            } else {
                Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                Toast.makeText(mContext, "返回错误：" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String genAppSign(PayReq req, String apiKey) {
        StringBuilder sb = new StringBuilder();

        sb.append("appid=");
        sb.append(req.appId);
        sb.append('&');

        sb.append("noncestr=");
        sb.append(req.nonceStr);
        sb.append('&');

        sb.append("package=");
        sb.append(req.packageValue);
        sb.append('&');

        sb.append("partnerid=");
        sb.append(req.partnerId);
        sb.append('&');

        sb.append("prepayid=");
        sb.append(req.prepayId);
        sb.append('&');

        sb.append("timestamp=");
        sb.append(req.timeStamp);
        sb.append('&');


        sb.append("key=");
        sb.append(apiKey);

        String appSign = getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }

    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Context context = mContext;
                    PayResult result = new PayResult((String) message.obj);
                    if (result.resultStatus.equals("9000")) {
                        AppBus.getInstance().post(true);
                        Toast.makeText(context, getString(R.string.pay_succeed),
                                Toast.LENGTH_SHORT).show();
                        if (getIntent().getBooleanExtra("shoppingCart", false)) {
                            startActivity(new Intent(mContext, CustomerOrderActivity.class));
                        } else {
                            finish();
                        }

                    } else {
                        Toast.makeText(context, getString(R.string.pay_fail),
                                Toast.LENGTH_SHORT).show();

                        if (getIntent().getBooleanExtra("shoppingCart", false)) {
                            startActivity(new Intent(mContext, CustomerOrderActivity.class));
                        }
                    }
                    break;
            }
            return true;
        }
    });
}
