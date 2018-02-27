package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonElement;
import com.squareup.otto.Subscribe;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.OrderWareAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.CustomerOrder;
import com.yuangee.flower.customer.entity.Express;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.OrderWare;
import com.yuangee.flower.customer.entity.PayResult;
import com.yuangee.flower.customer.entity.ShopOrder;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.TimeUtil;
import com.yuangee.flower.customer.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/12/4.
 */

public class CusOrderDetailActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.goods_recycler)
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

    @BindView(R.id.total)
    TextView hejiFee;

    @BindView(R.id.peihuo_fee)
    TextView peihuoFee;

    @BindView(R.id.baozhuang_fee)
    TextView baozhuangFee;

    @BindView(R.id.order_no)
    TextView orderNo;

    @BindView(R.id.created_time)
    TextView createdTime;

    @BindView(R.id.bespeak_time)
    TextView bespeakTime;

    @BindView(R.id.bespeak_money)
    TextView bespeakMoney;

    @BindView(R.id.bespeak_time_con)
    LinearLayout bespeakTimeCon;

    @BindView(R.id.bespeak_money_con)
    LinearLayout bespeakMoneyCon;

    @BindView(R.id.customer_name)
    TextView customerName;

    @BindView(R.id.customer_phone)
    TextView customerPhone;

    @BindView(R.id.receiver_name)
    TextView receiverName;

    @BindView(R.id.receiver_phone)
    TextView receiverPhone;

    @BindView(R.id.receiver_place)
    TextView receiverPlace;

    @BindView(R.id.receiver_kuaidi)
    TextView receiverKuaidi;

    @BindView(R.id.user_message)
    LinearLayout memoCon;

    @BindView(R.id.user_message_txt)
    TextView memoText;

    @BindView(R.id.order_status)
    TextView orderStatus;
    @BindView(R.id.order_type)
    TextView orderType;

    @BindView(R.id.price_layout)
    View shopOrderPrice;
    @BindView(R.id.cutdown_text)
    TextView cutdownTime;

    @BindView(R.id.cutdown_layout)
    LinearLayout cutdownLayout;
    private CustomerOrder cusOrder;
    OrderWareAdapter adapter;

    private boolean isShop = false;
    private Timer timer = new Timer();

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    Handler handler;

    @Override
    public void initToolBar() {
        mToolbar.setTitle(cusOrder.getStatusStr());
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
    public void initViews(Bundle savedInstanceState) {
        cusOrder = (CustomerOrder) getIntent().getSerializableExtra("cusOrder");
        isShop = getIntent().getBooleanExtra("isShop", false);
        if (null == cusOrder) {
            finish();
            return;
        }
        initHandler();
        adapter = new OrderWareAdapter(this);
        adapter.setOnItemClickListener(new OrderWareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Goods good) {
                Intent intent = new Intent(CusOrderDetailActivity.this, WaresDetailActivity.class);
                intent.putExtra("goods", good);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        List<OrderWare> orderWaresList = new ArrayList<>();
        for (ShopOrder shopOrder : cusOrder.orderList) {
            orderWaresList.addAll(shopOrder.orderWaresList);
        }
        adapter.setOrderWares(orderWaresList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        BigDecimal totalMoney = new BigDecimal(0.0);
        for (OrderWare orderWare : orderWaresList) {
            if (null != orderWare.total) {
                totalMoney = totalMoney.add(orderWare.total);
            }
        }
        totalFee.setText("¥" + totalMoney);
        shouxuFei.setText("¥" + cusOrder.customerBrokerage);
        yunFei.setText("¥" + cusOrder.expressDeliveryMoney);

        peihuoFee.setText("¥" + cusOrder.peihuoFee);
        baozhuangFee.setText("¥" + cusOrder.baozhuangFee);

        if (StringUtils.isNotBlank(String.valueOf(cusOrder.couponMoney))) {
            if (String.valueOf(cusOrder.couponMoney).equals("null")) {
                couponFee.setText("¥" + "0");
            } else {
                couponFee.setText("¥" + cusOrder.couponMoney);
            }
        } else {
            couponFee.setText("¥" + "0");
        }

        if (null != cusOrder.peihuoFee) {
            totalMoney = totalMoney.add(cusOrder.peihuoFee);
        }
        if (null != cusOrder.baozhuangFee) {
            totalMoney = totalMoney.add(cusOrder.baozhuangFee);
        }
        if (null != cusOrder.customerBrokerage) {
            totalMoney = totalMoney.add(cusOrder.customerBrokerage);
        }
        if (null != cusOrder.expressDeliveryMoney) {
            totalMoney = totalMoney.add(cusOrder.expressDeliveryMoney);
        }
        if (null != cusOrder.couponMoney) {
            totalMoney = totalMoney.subtract(cusOrder.couponMoney);
        }

        hejiFee.setText("" + cusOrder.realPay + "元");

        //订单基本信息
        orderNo.setText(cusOrder.orderNo);
        createdTime.setText(cusOrder.created);
        orderStatus.setText(cusOrder.getStatusStr());
        if (!cusOrder.bespeak) {
            orderType.setText("普通订单");
            bespeakMoneyCon.setVisibility(View.GONE);
            bespeakTimeCon.setVisibility(View.GONE);

        } else {
            orderType.setText("预约订单");
            ((TextView) bespeakTimeCon.getChildAt(0)).setText("预约日期");
            ((TextView) bespeakMoneyCon.getChildAt(0)).setText("预约金");
            bespeakTime.setText(TimeUtil.getTime(TimeUtil.YMD_2, cusOrder.bespeakDate));
            bespeakMoney.setText("¥" + cusOrder.bespeakMoney);
        }
        memoCon.setVisibility(View.VISIBLE);
        if (StringUtils.isBlank(cusOrder.memo)) {
            memoText.setText("<暂无>");
        } else {

            memoText.setText(cusOrder.memo);
        }

        //客户基本信息
        customerName.setText(cusOrder.memberName);
        customerPhone.setText(cusOrder.memberPhone);

        //收货信息
        receiverName.setText(cusOrder.receiverName);
        receiverPhone.setText(cusOrder.receiverPhone);
        receiverPlace.setText(cusOrder.receiverAddress);
        String s = "";
        Express express = DbHelper.getInstance().getExpressLongDBManager().load(cusOrder.expressId);
        if (express != null) {
            s = express.expressDeliveryName;
        }
        receiverKuaidi.setText(s);

        initBtn();
    }

    private void initBtn() {
        if (!isShop) {
            if (cusOrder.status == CustomerOrder.ORDER_STATUS_NOTPAY) {
                leftBtn.setVisibility(View.VISIBLE);
                rightBtn.setVisibility(View.VISIBLE);
                if (!cusOrder.bespeak) {
                    leftBtn.setText("付款");
                } else {
                    leftBtn.setText("支付预约金");
                }
                rightBtn.setText("取消订单");
                leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payOrder(cusOrder);
                    }
                });
                rightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(cusOrder);
                    }
                });
            } else if (cusOrder.status == CustomerOrder.ORDER_STATUS_WAIT) {
                leftBtn.setVisibility(View.VISIBLE);
                rightBtn.setVisibility(View.GONE);
                leftBtn.setText("提醒发货");
                leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showMessage(CusOrderDetailActivity.this, "提醒卖家发货成功，商品将很快送到你手中");
                    }
                });
            } else if (cusOrder.status == CustomerOrder.ORDER_STATUS_CONSIGN) {
                leftBtn.setVisibility(View.VISIBLE);
                rightBtn.setVisibility(View.GONE);
                leftBtn.setText("确认收货");
                leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmOrder(cusOrder);
                    }
                });
            } else if (cusOrder.status == CustomerOrder.ORDER_STATUS_FINISH) {
                leftBtn.setVisibility(View.GONE);
                rightBtn.setVisibility(View.GONE);
            } else if (cusOrder.status == CustomerOrder.ORDER_STATUS_BE_BACK) {
                leftBtn.setVisibility(View.VISIBLE);
                rightBtn.setVisibility(View.VISIBLE);
                leftBtn.setText("支付尾款");
                rightBtn.setText("取消订单");
                leftBtn.setEnabled(false);
                leftBtn.setBackgroundColor(Color.GRAY);
                shopOrderPrice.setVisibility(View.INVISIBLE);
                leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payOrder(cusOrder);
                    }
                });
                cutdownLayout.setVisibility(View.VISIBLE);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(runnable);
                    }
                }, 1000, 1000);
                rightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(cusOrder);
                    }
                });
            } else if (cusOrder.status == CustomerOrder.ORDER_STATUS_CANCEL) {
                leftBtn.setVisibility(View.GONE);
                rightBtn.setVisibility(View.GONE);
            }
        }
    }

    private void initHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        Context context = CusOrderDetailActivity.this;
                        PayResult result = new PayResult((String) message.obj);
                        if (result.resultStatus.equals("9000")) {
                            Toast.makeText(context, getString(R.string.pay_succeed),
                                    Toast.LENGTH_SHORT).show();

                            cusOrder.status = ShopOrder.ORDER_STATUS_WAIT;
                            initBtn();

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

    private void cancelOrder(final CustomerOrder cusOrder) {
        AlertDialog dialog = new AlertDialog.Builder(CusOrderDetailActivity.this)
                .setTitle("温馨提示")
                .setMessage("您确定要取消订单吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateOrderStatus(cusOrder.id, ShopOrder.ORDER_STATUS_CANCEL);
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

    private void fahuo(final CustomerOrder cusOrder) {
        AlertDialog dialog = new AlertDialog.Builder(CusOrderDetailActivity.this)
                .setTitle("温馨提示")
                .setMessage("您确定要确认发货吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateOrderStatus(cusOrder.id, 3);
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

    private void confirmOrder(CustomerOrder cusOrder) {
        updateOrderStatus(cusOrder.id, ShopOrder.ORDER_STATUS_FINISH);
    }

//    private void payJishiWx(Long orderId, Integer type) {
//        Observable<JsonElement> observable = ApiManager.getInstance().api
//                .payJishiSingleWx(orderId, type)
//                .map(new HttpResultFunc<JsonElement>(CusOrderDetailActivity.this))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        mRxManager.add(observable.subscribe(new MySubscriber<JsonElement>(CusOrderDetailActivity.this, true,
//                false, new NoErrSubscriberListener<JsonElement>() {
//            @Override
//            public void onNext(JsonElement jsonElement) {
//                detailWxPay(jsonElement);
//            }
//        })));
//    }
//
//    private void payYuyueWx(Long orderId) {
//        Observable<JsonElement> observable = ApiManager.getInstance().api
//                .payYuyueSingleWx(orderId)
//                .map(new HttpResultFunc<JsonElement>(CusOrderDetailActivity.this))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        mRxManager.add(observable.subscribe(new MySubscriber<>(CusOrderDetailActivity.this, true,
//                false, new NoErrSubscriberListener<JsonElement>() {
//            @Override
//            public void onNext(JsonElement jsonElement) {
//                detailWxPay(jsonElement);
//            }
//        })));
//    }
//
//    private void detailWxPay(JsonElement jsonElement) {
//        try {
//            JSONObject json = new JSONObject(jsonElement.toString());
//            if (!json.has("retcode")) {
//                PayReq req = new PayReq();
//                req.appId = json.getString("appid");
//                req.partnerId = json.getString("partnerid");
//                req.prepayId = json.getString("prepayid");
//                req.nonceStr = json.getString("noncestr");
//                req.timeStamp = json.getString("timestamp");
//                req.packageValue = json.getString("package");
//                req.sign = json.getString("sign");
//                req.extData = "app data"; // optional
//                Log.e("wxPay", "正常调起支付");
//
//                IWXAPI api = WXAPIFactory.createWXAPI(CusOrderDetailActivity.this, req.appId);
//
//                api.sendReq(req);
//            } else {
//                Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
//                Toast.makeText(CusOrderDetailActivity.this, "返回错误：" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void payJishiZfb(Long orderId, Integer type) {
//        Observable<ZfbResult> observable = ApiManager.getInstance().api
//                .payJishiSingleZfb(orderId, type)
//                .map(new HttpResultFunc<ZfbResult>(CusOrderDetailActivity.this))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        mRxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(CusOrderDetailActivity.this, true,
//                false, new NoErrSubscriberListener<ZfbResult>() {
//            @Override
//            public void onNext(ZfbResult s) {
//                detailZfb(s.orderInfo);
//            }
//        })));
//    }
//
//    private void payYuyueZfb(Long orderId) {
//        Observable<ZfbResult> observable = ApiManager.getInstance().api
//                .payYuyueSingleZfb(orderId)
//                .map(new HttpResultFunc<ZfbResult>(CusOrderDetailActivity.this))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        mRxManager.add(observable.subscribe(new MySubscriber<>(CusOrderDetailActivity.this, true,
//                false, new NoErrSubscriberListener<ZfbResult>() {
//            @Override
//            public void onNext(ZfbResult s) {
//                detailZfb(s.orderInfo);
//            }
//        })));
//    }
//
//    private void detailZfb(final String s) {
//        new Thread() {
//            public void run() {
//
//                PayTask alipay = new PayTask(CusOrderDetailActivity.this);
//                String result = alipay
//                        .pay(s, true);
//
//                Message msg = new Message();
//                msg.what = 0;
//                msg.obj = result;
//                handler.sendMessage(msg);
//            }
//        }.start();
//    }

    private void updateOrderStatus(long orderId, int status) {
        Observable<Object> observable = ApiManager.getInstance().api
                .updateOrder(orderId, status)
                .map(new HttpResultFunc<>(CusOrderDetailActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<Object>(CusOrderDetailActivity.this, true,
                true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(CusOrderDetailActivity.this, "操作成功");
                finish();
            }
        })));


    }

    private void payOrder(final CustomerOrder cusOrder) {

        Intent it = new Intent(CusOrderDetailActivity.this, CustomerOrderPayActivity.class);
        it.putExtra("order", cusOrder);
        startActivity(it);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            cusOrder.status = ShopOrder.ORDER_STATUS_WAIT;
            initBtn();
            ToastUtil.showMessage(CusOrderDetailActivity.this, "支付成功");

// 结果result_data为成功时，去商户后台查询一下再展示成功
        } else if (str.equalsIgnoreCase("fail")) {
            ToastUtil.showMessage(CusOrderDetailActivity.this, "支付失败！");
        } else if (str.equalsIgnoreCase("cancel")) {
            ToastUtil.showMessage(CusOrderDetailActivity.this, "你已取消了本次订单的支付！");
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            cutdownTime.setText(getPayLeftTime(cusOrder.bespeakDate));
        }
    };

    /**
     * 支付剩余时间计算
     */
    private String getPayLeftTime(long deadLine) {
        long leftMillis = deadLine - System.currentTimeMillis();

        if (leftMillis < 0) {
            return "支付已超时";
        } else {
            long day = leftMillis / (1000 * 60 * 60 * 24);
            long h = leftMillis / (1000 * 60 * 60) % 24;
            long m = leftMillis / (1000 * 60) % 60;
            long s = leftMillis / 1000 % 60;
            StringBuilder sb = new StringBuilder();
            sb.append("距离预约时间: ");
            if (day > 0) {
                sb.append(day).append("天 ");
            }
            if (h > 0) {
                sb.append(h).append(":");
            }
            if (m > 0) {
                sb.append(m).append(":");
            }
            if (s > 0) {
                sb.append(s);
            }
            return sb.toString();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        handler.removeCallbacksAndMessages(null);
    }

    @Subscribe
    public void paySuccess(Boolean success) {
        if (success) {
            cusOrder.status = ShopOrder.ORDER_STATUS_WAIT;
            initBtn();
            ToastUtil.showMessage(CusOrderDetailActivity.this, "支付成功");
            // 结果result_data为成功时，去商户后台查询一下再展示成功
        } else {
            ToastUtil.showMessage(CusOrderDetailActivity.this, "支付失败！");
        }
    }
}
