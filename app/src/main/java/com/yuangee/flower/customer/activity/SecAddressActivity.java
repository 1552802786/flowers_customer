package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.AddressAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/10/27 0027.
 */

public class SecAddressActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.address_recycler)
    PullLoadMoreRecyclerView recyclerView;

    @BindView(R.id.add_place)
    LinearLayout addPlace;

    @BindView(R.id.empty_layout)
    CustomEmptyView customEmptyView;

    @OnClick(R.id.add_place)
    void addPlace() {
        startActivity(new Intent(SecAddressActivity.this, EditAddressActivity.class));
    }

    private AddressAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sec_address;
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryById(App.getPassengerId());
    }

    private Address selectedAddr;

    @Override
    public void initViews(Bundle savedInstanceState) {

        selectedAddr = (Address) getIntent().getSerializableExtra("address");

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AddressAdapter(this);

        recyclerView.setHasMore(false);
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                queryById(App.getPassengerId());
            }

            @Override
            public void onLoadMore() {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showEmptyLayout(int tag) {
        recyclerView.setVisibility(View.GONE);
        customEmptyView.setVisibility(View.VISIBLE);
        if (tag == 0) {
            customEmptyView.setEmptyImage(R.drawable.ic_filed);
            customEmptyView.setEmptyText("您还没有添加过收货地址，\n点我创建地址");
            customEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(SecAddressActivity.this, EditAddressActivity.class));
                }
            });
        } else {
            customEmptyView.setEmptyImage(R.drawable.ic_filed);
            customEmptyView.setEmptyText("貌似出了点问题，\n点我重试");
            customEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    queryById(App.getPassengerId());
                }
            });
        }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("个人中心");
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
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.i_manage) {
                    adapter.setManage(true);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_addr, menu);
        return true;
    }

    private void queryById(long id) {
        recyclerView.setRefreshing(true);

        Observable<Member> observable = ApiManager.getInstance().api
                .findById(id)
                .map(new HttpResultFunc<Member>(SecAddressActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(SecAddressActivity.this, false, false, new HaveErrSubscriberListener<Member>() {
            @Override
            public void onNext(Member o) {
                SharedPreferences.Editor editor = App.me().getSharedPreferences().edit();

                List<Address> addressList = o.memberAddressList;

                if (null != addressList) {
                    DbHelper.getInstance().getAddressLongDBManager().insertOrReplaceInTx(addressList);
                    for (Address address : addressList) {
                        if (address.id == selectedAddr.id) {
                            address.isSelected = true;
                        }
                    }
                } else {
                    DbHelper.getInstance().getAddressLongDBManager().deleteAll();
                }

                editor.putLong("id", o.id);
                editor.putString("name", o.name);
                editor.putString("userName", o.userName);
                editor.putString("passWord", o.passWord);
                editor.putString("phone", o.phone);
                editor.putString("email", o.email);
                editor.putString("photo", o.photo);
                editor.putBoolean("gender", o.gender);
                editor.putString("type", o.type);
                editor.putBoolean("inBlacklist", o.inBlacklist);
                editor.putBoolean("isRecycle", o.isRecycle);
                editor.putBoolean("inFirst", o.inFirst);
                editor.putFloat("balance", (float) o.balance);
                editor.putLong("deathDate", o.memberToken.deathDate);
                editor.putString("token", o.memberToken.token);

                editor.putBoolean("login", true);

                editor.apply();

                recyclerView.setPullLoadMoreCompleted();
                if (o.memberAddressList == null || o.memberAddressList.size() == 0) {
                    showEmptyLayout(0);
                } else {
                    adapter.setAddressList(o.memberAddressList);
                }
            }

            @Override
            public void onError(int code) {
                recyclerView.setPullLoadMoreCompleted();
                showEmptyLayout(code);
            }
        })));
    }

    @Override
    public void onBackPressed() {
        if (adapter.getIsManage()) {
            adapter.setManage(false);
        } else {
            if (null != adapter.getAddressList()) {
                Address selectedAddr = null;
                for (Address address : adapter.getAddressList()) {
                    if (address.isSelected) {
                        selectedAddr = address;
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("address", selectedAddr);
                setResult(RESULT_OK, intent);
            }
            super.onBackPressed();
        }
    }
}
