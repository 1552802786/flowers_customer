package com.yuangee.flower.customer.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.ShopOrderCount;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.util.TimeUtil;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by admin on 2018/3/21.
 */

public class OrderCountActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.saled_money)
    TextView saledMoney;

    @BindView(R.id.order_number)
    TextView orderNumber;

    @BindView(R.id.order_money)
    TextView orderMoney;

    @BindView(R.id.start_date)
    TextView startDate;

    @BindView(R.id.end_date)
    TextView endDate;

    @BindView(R.id.empty)
    CustomEmptyView emptyView;
    Long shopId;

    @OnClick(R.id.start_date)
    void chooseStartDate() {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String strMonth = String.valueOf(month);
                if (month < 10) {
                    strMonth = "0" + (month + 1);
                }
                startDate.setText(year + "-" + strMonth + "-" + day);
            }
        }, cl.get(Calendar.YEAR), cl.get(Calendar.MONTH), cl.get(Calendar.DAY_OF_MONTH));
        dp.getDatePicker().setMaxDate((new Date()).getTime());
        dp.show();
    }

    @OnClick(R.id.end_date)
    void chooseEndDate() {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String strMonth = String.valueOf(month);
                if (month < 10) {
                    strMonth = "0" + (month + 1);
                }
                endDate.setText(year + "-" + strMonth + "-" + day);
            }
        }, cl.get(Calendar.YEAR), cl.get(Calendar.MONTH), cl.get(Calendar.DAY_OF_MONTH));
        dp.getDatePicker().setMaxDate((new Date()).getTime());
        dp.show();
    }

    @OnClick(R.id.confirm_btn)
    void confirmDate() {
        getData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_count;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        shopId = App.me().getSharedPreferences().getLong("shopId", -1);
        if (shopId == -1) {
            shopId = null;
        }
        startDate.setText(TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis()));
        endDate.setText(TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis()));
        getData();
    }

    private void getData() {
        rx.Observable<ShopOrderCount> observable = ApiManager.getInstance().api
                .getShopOrderCount(shopId, startDate.getText().toString(), endDate.getText().toString())
                .map(new HttpResultFunc<ShopOrderCount>(OrderCountActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(OrderCountActivity.this, true, false, new HaveErrSubscriberListener<ShopOrderCount>() {
            @Override
            public void onNext(ShopOrderCount count) {
                hideEmpty();
                saledMoney.setText("¥ " + (TextUtils.isEmpty(count.shouldSettleMoney) ? "0" : count.shouldSettleMoney));
                orderNumber.setText(count.orderCount);
                orderMoney.setText("¥ " + (TextUtils.isEmpty(count.orderMoney) ? "0" : count.orderMoney));
            }

            @Override
            public void onError(int code) {
                showEmpty();
                ToastUtil.showMessage(OrderCountActivity.this, "获取数据失败");
            }
        })));
    }

    private void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setEmptyText("获取数据失败");
        emptyView.setEmptyImage(R.drawable.ic_filed);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
    }

    private void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("销量统计");
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
}
