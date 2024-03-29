package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.ShopOrderAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.ShopOrder;
import com.yuangee.flower.customer.entity.PayResult;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/1 0001.
 */

public class ShopOrderActivity extends RxBaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.radio_all)
    RadioButton radioAll;

    @BindView(R.id.radio_appoint)
    RadioButton radioAppoint;

    @BindView(R.id.radio_wait_receiving)
    RadioButton radioWaitReceiving;

    @BindView(R.id.order_recycler_view)
    PullLoadMoreRecyclerView recyclerView;

    @BindView(R.id.empty)
    CustomEmptyView emptyView;

    private ShopOrderAdapter adapter;

    private List<ShopOrder> shopOrders;

    private Integer status;
    private Boolean bespeak;//是否预约
    private Long memberId = App.getPassengerId();
    private Long shopId = null;
    @OnClick(R.id.back_btn)
    void onBackBtn() {
        finish();
    }
    @OnClick(R.id.order_count_btn)
    void gotoOrderCount(){
        Intent it=new Intent(this,OrderCountActivity.class);
        startActivity(it);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_my_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        shopOrders = new ArrayList<>();
        status = getIntent().getIntExtra("status", -1);
        bespeak = getIntent().getBooleanExtra("bespeak", false);
        shopId = App.me().getSharedPreferences().getLong("shopId", -1);
        if (shopId == -1) {
            shopId = null;
        }
        radioAll.setOnCheckedChangeListener(this);
        radioAppoint.setOnCheckedChangeListener(this);
        radioWaitReceiving.setOnCheckedChangeListener(this);

        if (bespeak) {
            radioAppoint.setChecked(true);
        } else if (status == -1) {
            status = null;
            radioAll.setChecked(true);
        } else if (status == 1) {
            radioWaitReceiving.setChecked(true);
        }

//        if (isShop) {
//            radioNotPay.setVisibility(View.GONE);
//            radioAppoint.setVisibility(View.GONE);
//            radioAppoint.setVisibility(View.GONE);
//        }
        initRecyclerView();
    }

    Handler handler;

    @Override
    public void initRecyclerView() {

        initHandler();
        shopOrders = new ArrayList<>();
        adapter = new ShopOrderAdapter(this, mRxManager, true);
        adapter.setOnRefresh(new ShopOrderAdapter.OnRefresh() {
            @Override
            public void onRefresh() {
                recyclerView.setRefreshing(true);
                queryOrders(status, bespeak, shopId);
            }
        });
        adapter.setZfbPay(new ShopOrderAdapter.OnStartZfbPay() {
            @Override
            public void pay(final String s) {
                new Thread() {
                    public void run() {

                        PayTask alipay = new PayTask(ShopOrderActivity.this);
                        String result = alipay
                                .pay(s, true);

                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });
        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                queryOrders(status, bespeak, shopId);
            }

            @Override
            public void onLoadMore() {
                page++;
                queryOrders(status, bespeak, shopId);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryOrders(status, bespeak, shopId);
        recyclerView.setRefreshing(true);
    }

    private void initHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        Context context = ShopOrderActivity.this;
                        PayResult result = new PayResult((String) message.obj);
                        if (result.resultStatus.equals("9000")) {
                            Toast.makeText(context, getString(R.string.pay_succeed),
                                    Toast.LENGTH_SHORT).show();

                            queryOrders(status, bespeak, shopId);

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radio_all:
                    status = null;
                    bespeak = false;
                    page = 0;
                    queryOrders(status, bespeak, shopId);
                    break;
                case R.id.radio_appoint:
                    bespeak = true;
                    status = null;
                    page = 0;
                    queryOrders(status, bespeak, shopId);
                    break;
                case R.id.radio_wait_receiving:
                    bespeak = false;
                    status = 1;
                    page = 0;
                    queryOrders(status, bespeak, shopId);
                    break;
            }
        }
    }

    private int page = 0;
    private int limit = 10;

    /**
     * 查询店铺订单（小订单）
     *
     * @param bespeak
     * @param shopId
     */
    private void queryOrders(Integer status, Boolean bespeak, Long shopId) {
        if (!bespeak) {//非预约传Null
            bespeak = null;//
        }
        Observable<PageResult<ShopOrder>> observable = ApiManager.getInstance().api
                .findByParams(status, bespeak, shopId, (long) page * 10, (long) limit)
                .map(new HttpResultFunc<PageResult<ShopOrder>>(ShopOrderActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(ShopOrderActivity.this, true, false, new HaveErrSubscriberListener<PageResult<ShopOrder>>() {
            @Override
            public void onNext(PageResult<ShopOrder> result) {
                if (result.total > (page + 1) * limit) {
                    recyclerView.setHasMore(true);
                } else {
                    recyclerView.setHasMore(false);
                }
                recyclerView.setPullLoadMoreCompleted();
                if (page == 0) {
                    shopOrders.clear();
                }
                shopOrders.addAll(result.rows);
                adapter.setData(shopOrders);
                if (result.total == 0) {
                    showEmpty();
                } else {
                    hideEmpty();
                }
            }

            @Override
            public void onError(int code) {
                recyclerView.setPullLoadMoreCompleted();
            }
        })));
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setEmptyText("没有任何订单数据\n快去下单吧");
        emptyView.setEmptyImage(R.drawable.ic_filed);
    }

    private void hideEmpty() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            queryOrders(status, bespeak, shopId);
            ToastUtil.showMessage(ShopOrderActivity.this, "支付成功");

// 结果result_data为成功时，去商户后台查询一下再展示成功
        } else if (str.equalsIgnoreCase("fail")) {
            ToastUtil.showMessage(ShopOrderActivity.this, "支付失败！");
        } else if (str.equalsIgnoreCase("cancel")) {
            ToastUtil.showMessage(ShopOrderActivity.this, "你已取消了本次订单的支付！");
        }
    }
}
