package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        radioAll.setOnCheckedChangeListener(this);
        radioAppoint.setOnCheckedChangeListener(this);
        radioNotPay.setOnCheckedChangeListener(this);
        radioWaitReceiving.setOnCheckedChangeListener(this);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.radio_all:
                break;
            case R.id.radio_appoint:
                break;
            case R.id.radio_not_pay:
                break;
            case R.id.radio_wait_receiving:
                break;
        }
    }

    private int page = 0;
    private int limlit = 10;

    private void queryOrders(){
//        Observable<List<Goods>> observable = ApiManager.getInstance().api
//                .findById(id)
//                .map(new HttpResultFunc<Member>(SecAddressActivity.this))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
    }
}
