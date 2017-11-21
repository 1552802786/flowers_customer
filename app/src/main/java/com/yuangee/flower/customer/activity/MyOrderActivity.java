package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.OrderAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Order;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/1 0001.
 */

public class MyOrderActivity extends RxBaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.radio_all)
    RadioButton radioAll;

    @BindView(R.id.radio_appoint)
    RadioButton radioAppoint;

    @BindView(R.id.radio_not_pay)
    RadioButton radioNotPay;

    @BindView(R.id.radio_wait_receiving)
    RadioButton radioWaitReceiving;

    @BindView(R.id.recycler_view)
    PullLoadMoreRecyclerView recyclerView;

    @BindView(R.id.empty)
    CustomEmptyView emptyView;

    private OrderAdapter adapter;

    private List<Order> orders;

    private Integer status;
    private Boolean bespeak;//是否预约
    private boolean isShop = false;
    private Long memberId = App.getPassengerId();
    private Long shopId = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        orders = new ArrayList<>();
        status = getIntent().getIntExtra("status", -1);
        bespeak = getIntent().getBooleanExtra("bespeak", false);
        isShop = getIntent().getBooleanExtra("isShop", false);
        if (isShop) {
            shopId = App.me().getSharedPreferences().getLong("shopId", -1);
            if (shopId == -1) {
                shopId = null;
            }
        }
        radioAll.setOnCheckedChangeListener(this);
        radioAppoint.setOnCheckedChangeListener(this);
        radioNotPay.setOnCheckedChangeListener(this);
        radioWaitReceiving.setOnCheckedChangeListener(this);

        if (bespeak) {
            radioAppoint.setChecked(true);
        } else if (status == -1) {
            status = null;
            radioAll.setChecked(true);
        } else if (status == 0) {
            radioNotPay.setChecked(true);
        } else if (status == 2) {
            radioWaitReceiving.setChecked(true);
        }
        initRecyclerView();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("我的订单");
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
    public void initRecyclerView() {
        orders = new ArrayList<>();
        adapter = new OrderAdapter(this, mRxManager);
        adapter.setOnRefresh(new OrderAdapter.OnRefresh() {
            @Override
            public void onRefresh() {
                recyclerView.setRefreshing(true);
                queryOrders(status, bespeak, memberId, shopId);
            }
        });
        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                queryOrders(status, bespeak, memberId, shopId);
            }

            @Override
            public void onLoadMore() {
                page++;
                queryOrders(status, bespeak, memberId, shopId);
            }
        });

        queryOrders(status, bespeak, memberId, shopId);
        recyclerView.setRefreshing(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radio_all:
                    status = null;
                    bespeak = false;
                    page = 0;
                    queryOrders(status, bespeak, memberId, shopId);
                    break;
                case R.id.radio_appoint:
                    bespeak = true;
                    status = null;
                    page = 0;
                    queryOrders(status, bespeak, memberId, shopId);
                    break;
                case R.id.radio_not_pay:
                    bespeak = false;
                    status = 0;
                    page = 0;
                    queryOrders(status, bespeak, memberId, shopId);
                    break;
                case R.id.radio_wait_receiving:
                    bespeak = false;
                    status = 2;
                    page = 0;
                    queryOrders(status, bespeak, memberId, shopId);
                    break;
            }
        }
    }

    private int page = 0;
    private int limit = 10;

    private void queryOrders(Integer status, Boolean bespeak, Long memberId, Long shopId) {
        Observable<PageResult<Order>> observable = ApiManager.getInstance().api
                .findByParams(status, bespeak, memberId, shopId, (long) page, (long) limit)
                .map(new HttpResultFunc<PageResult<Order>>(MyOrderActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(MyOrderActivity.this, true, false, new HaveErrSubscriberListener<PageResult<Order>>() {
            @Override
            public void onNext(PageResult<Order> result) {
                if (result.total <= (page + 1) * limit) {
                    recyclerView.setHasMore(true);
                } else {
                    recyclerView.setHasMore(false);
                }
                recyclerView.setPullLoadMoreCompleted();
                if (page == 0) {
                    orders.clear();
                }
                orders.addAll(result.rows);
                adapter.setData(orders);
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
}
