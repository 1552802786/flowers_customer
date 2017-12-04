package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonElement;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.OrderWareAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Order;
import com.yuangee.flower.customer.entity.OrderWare;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.SwipeRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/12/4.
 */

public class OrderDetailActivity extends RxBaseActivity {

    @BindView(R.id.good_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.total_fee)
    TextView totalFee;

    @BindView(R.id.shouxu_fee)
    TextView shouxuFei;

    @BindView(R.id.yun_fee)
    TextView yunFei;

    @BindView(R.id.coupon_fee)
    TextView couponFee;

    @BindView(R.id.left_btn)
    TextView leftBtn;

    @BindView(R.id.right_btn)
    TextView rightBtn;

    private Order order;
    OrderWareAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    Handler handler;

    @Override
    public void initViews(Bundle savedInstanceState) {
        order = (Order) getIntent().getSerializableExtra("order");
        if (null == order) {
            finish();
            return;
        }
        initHandler();
        adapter = new OrderWareAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        double totalMoney = 0.0;
        for (OrderWare orderWare : order.orderWaresList) {
            totalMoney += orderWare.total;
        }
        totalFee.setText("¥" + totalMoney);
        shouxuFei.setText("¥" + order.customerBrokerage);
        yunFei.setText("¥" + order.expressDeliveryMoney);
        couponFee.setText("¥" + order.couponMoney);

        if (order.status == 0) {
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.VISIBLE);
            leftBtn.setText("去支付");
            rightBtn.setText("取消订单");
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payOrder(order);
                }
            });
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(order);
                }
            });
        } else if (order.status == 1) {
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.GONE);
            leftBtn.setText("提醒发货");
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showMessage(OrderDetailActivity.this, "提醒卖家发货成功，商品将很快送到你手中");
                }
            });
        } else if (order.status == 2) {
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.GONE);
            leftBtn.setText("确认收货");
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmOrder(order);
                }
            });
        } else if (order.status == 3) {
            leftBtn.setVisibility(View.GONE);
            rightBtn.setVisibility(View.GONE);
        } else if (order.status == 4) {
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.VISIBLE);
            leftBtn.setText("去支付");
            rightBtn.setText("取消订单");
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payOrder(order);
                }
            });
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(order);
                }
            });
        } else if (order.status == 5) {
            leftBtn.setVisibility(View.GONE);
            rightBtn.setVisibility(View.GONE);
        }
    }

    private void initHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case 0:
                        Context context = OrderDetailActivity.this;
                        PayResult result = new PayResult((String) message.obj);
                        if (result.resultStatus.equals("9000")) {
                            Toast.makeText(context, getString(R.string.pay_succeed),
                                    Toast.LENGTH_SHORT).show();
                            PayCentreActivity.this.finish();

                        } else if (result.resultStatus.equals("4000")) {

                            Toast.makeText(context, getString(R.string.system_exception),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("4001")) {

                            Toast.makeText(context, getString(R.string.data_format_error),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("4003")) {

                            Toast.makeText(context, getString(R.string.can_not_use_zfb),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("4004")) {

                            Toast.makeText(context, getString(R.string.remove_bind),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("4005")) {

                            Toast.makeText(context, getString(R.string.bind_fail),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("4006")) {

                            Toast.makeText(context, getString(R.string.pay_fail),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("4010")) {

                            Toast.makeText(context, getString(R.string.re_bind),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("6000")) {

                            Toast.makeText(context, getString(R.string.upgrade),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("6001")) {

                            Toast.makeText(context, getString(R.string.cancel_pay),
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.resultStatus.equals("7001")) {

                            Toast.makeText(context, getString(R.string.web_pay_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void cancelOrder(final Order order) {
        AlertDialog dialog = new AlertDialog.Builder(OrderDetailActivity.this)
                .setTitle("温馨提示")
                .setMessage("您确定要取消订单吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateOrderStatus(order.id, Order.ORDER_STATUS_CANCEL);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void confirmOrder(Order order) {
        updateOrderStatus(order.id, Order.ORDER_STATUS_FINISH);
    }

    private void payJishiWx(Long orderId) {
        Observable<JsonElement> observable = ApiManager.getInstance().api
                .payJishiSingleWx(orderId)
                .map(new HttpResultFunc<JsonElement>(OrderDetailActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<JsonElement>(OrderDetailActivity.this, true,
                false, new NoErrSubscriberListener<JsonElement>() {
            @Override
            public void onNext(JsonElement jsonElement) {
                detailWxPay(jsonElement);
            }
        })));
    }

    private void payYuyueWx(Long orderId) {
        Observable<JsonElement> observable = ApiManager.getInstance().api
                .payYuyueSingleWx(orderId)
                .map(new HttpResultFunc<JsonElement>(OrderDetailActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(OrderDetailActivity.this, true,
                false, new NoErrSubscriberListener<JsonElement>() {
            @Override
            public void onNext(JsonElement jsonElement) {
                detailWxPay(jsonElement);
            }
        })));
    }

    private void detailWxPay(JsonElement jsonElement) {
        try {
            JSONObject json = new JSONObject(jsonElement.toString());
            if (null != json && !json.has("retcode")) {
                PayReq req = new PayReq();
                req.appId = json.getString("appid");
                req.partnerId = json.getString("partnerid");
                req.prepayId = json.getString("prepayid");
                req.nonceStr = json.getString("noncestr");
                req.timeStamp = json.getString("timestamp");
                req.packageValue = json.getString("package");
                req.sign = json.getString("sign");
                req.extData = "app data"; // optional
                Log.e("wxPay", "正常调起支付");

                IWXAPI api = WXAPIFactory.createWXAPI(OrderDetailActivity.this, req.appId);

                api.sendReq(req);
            } else {
                Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                Toast.makeText(OrderDetailActivity.this, "返回错误：" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void payJishiZfb(Long orderId) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payJishiSingleZfb(orderId)
                .map(new HttpResultFunc<ZfbResult>(OrderDetailActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(OrderDetailActivity.this, true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    private void payYuyueZfb(Long orderId) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payYuyueSingleZfb(orderId)
                .map(new HttpResultFunc<ZfbResult>(OrderDetailActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(OrderDetailActivity.this, true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    private void detailZfb(final String s) {
        new Thread() {
            public void run() {

                PayTask alipay = new PayTask(OrderDetailActivity.this);
                String result = alipay
                        .pay(s, true);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void updateOrderStatus(long orderId, int status) {
        Observable<Object> observable = ApiManager.getInstance().api
                .updateOrder(orderId, status)
                .map(new HttpResultFunc<>(OrderDetailActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<Object>(OrderDetailActivity.this, true,
                true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(OrderDetailActivity.this,"状态更新成功");
                finish();
            }
        })));


    }

    private void payOrder(final Order order) {
        AlertDialog alertDialog = new AlertDialog.Builder(OrderDetailActivity.this)
                .setMessage("请选择支付方式")
                .setPositiveButton("支付宝支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (order.bespeak) {
                            payYuyueZfb(order.id);
                        } else {
                            payJishiZfb(order.id);
                        }
                    }
                })
                .setNegativeButton("微信支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (order.bespeak) {
                            payYuyueWx(order.id);
                        } else {
                            payJishiWx(order.id);
                        }
                    }
                }).create();
        alertDialog.show();
    }
}
