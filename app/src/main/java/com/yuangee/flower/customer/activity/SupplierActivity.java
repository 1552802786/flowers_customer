package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.SupplierAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;

import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的店铺
 * Created by developerLzh on 2017/11/8 0008.
 */

public class SupplierActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_search)
    EditText editSearch;
    TabLayout tabLayout;

    @OnClick(R.id.add_text)
    void toAdd() {
        Intent intent = new Intent(SupplierActivity.this, GoodsActivity.class);
        intent.putExtra("shopId", shopId);
        intent.putExtra("shopName", shopName);
        intent.putExtra("change", true);
        startActivity(intent);
    }

    @BindView(R.id.good_recycler)
    PullLoadMoreRecyclerView goodRecycler;

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyView;

    private SupplierAdapter adapter;

    private int page = 0;
    private int limit = 10;

    private long shopId;
    private String shopName;
    private FloatingActionMenu actionMenu;
    private List<Goods> goodsList;
    private String waresNames;
    private boolean yuyue = false;
    private boolean dazong = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_supplier;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tabLayout = findViewById(R.id.fenlei);
        shopId = getIntent().getLongExtra("shopId", -1);
        shopName = getIntent().getStringExtra("shopName");

        goodsList = new ArrayList<>();
        adapter = new SupplierAdapter(this, mRxManager, shopId, shopName);
        adapter.setOnOrderingClickListener(new SupplierAdapter.OnOrderIngClick() {
            @Override
            public void onOrdering(Goods goods) {
                Intent intent = new Intent(SupplierActivity.this, WaresDetailActivity.class);
                intent.putExtra("goods", goods);
                startActivity(intent);
            }
        });
        goodRecycler.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        goodRecycler.setAdapter(adapter);
        goodRecycler.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                page = 0;
                goodsList.clear();
                goodRecycler.setRefreshing(true);
                getGoodsData();
            }

            @Override
            public void onLoadMore() {
                page++;
                getGoodsData();
            }
        });
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoft();
                    waresNames = editSearch.getText().toString();
                    yuyue = false;
                    dazong = false;
                    getGoodsData();
                    return true;
                }
                return false;
            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString().trim())) {
                    yuyue = false;
                    dazong = false;
                    getGoodsData();
                }
            }
        });
        initFloatPath();
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("预约"));
        tabLayout.addTab(tabLayout.newTab().setText("订购"));
        tabLayout.addTab(tabLayout.newTab().setText("竞拍"));
        tabLayout.addTab(tabLayout.newTab().setText("大宗"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                yuyue = false;
                dazong = false;
                if (tab.getPosition() == 0) {
                    getGoodsData();
                } else if (tab.getPosition() == 1) {
                    yuyue = true;
                    getGoodsData();
                } else if (tab.getPosition() == 2) {
                    getGoodsData();
                } else if (tab.getPosition() == 3) {
                    getGoodsData();
                } else if (tab.getPosition() == 4) {
                    dazong = true;
                    getGoodsData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void hideSoft() {
        // 先隐藏键盘
        ((InputMethodManager) editSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(SupplierActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        goodRecycler.setRefreshing(true);
        getGoodsData();
    }

    private void getGoodsData() {
        Observable<PageResult<Goods>> observable = ApiManager.getInstance().api
                .findWares(shopId, page * 10, waresNames, yuyue, dazong, null, limit)
                .map(new HttpResultFunc<PageResult<Goods>>(SupplierActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, new HaveErrSubscriberListener<PageResult<Goods>>() {
            @Override
            public void onNext(PageResult<Goods> pageResult) {
                if (page == 0) {
                    goodsList.clear();
                }
                goodsList.addAll(pageResult.rows);
                for (Goods goods : goodsList) {
                    goods.selectedNum = 1;//默认选中的个数为1
                }
                if (goodsList.size() == 0) {
                    showEmptyView(0);
                } else {
                    hideEmptyView();
                }
                goodRecycler.setVisibility(View.VISIBLE);
                goodRecycler.setRefreshing(false);
                goodRecycler.setPullLoadMoreCompleted();
                if (pageResult.total > (page + 1) * limit) {
                    goodRecycler.setHasMore(true);
                } else {
                    goodRecycler.setHasMore(false);
                }
                adapter.setData(goodsList);
            }

            @Override
            public void onError(int code) {
                goodsList.clear();
                adapter.setData(goodsList);
                showEmptyView(1);
            }
        })));
    }

    public void showEmptyView(int tag) {
        goodRecycler.setRefreshing(false);
        goodRecycler.setVisibility(View.GONE);
        goodRecycler.setPullLoadMoreCompleted();
        emptyView.setVisibility(View.VISIBLE);
        if (tag == 0) {
            emptyView.setEmptyImage(R.drawable.ic_filed);
            emptyView.setEmptyText("没能获取到任何数据");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 0;
                    getGoodsData();
                }
            });
        } else {
            emptyView.setEmptyImage(R.drawable.ic_filed);
            emptyView.setEmptyText("貌似出了点问题，\n点我重试");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 0;
                    getGoodsData();
                }
            });
        }
    }

    public void hideEmptyView() {
        goodRecycler.setRefreshing(false);
        goodRecycler.setVisibility(View.VISIBLE);
        goodRecycler.setPullLoadMoreCompleted();
        emptyView.setVisibility(View.GONE);
    }

    public void back(View view) {
        onBackPressed();
    }

    private void initFloatPath() {
        final ImageView main_float_icon = new ImageView(this);
        main_float_icon.setImageDrawable(getResources().getDrawable(R.drawable.my_shop_order));
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(main_float_icon)
                .build();
        // repeat many times:
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView orderall = new ImageView(this);

        orderall.setImageDrawable(getResources().getDrawable(R.drawable.my_ordre_all));
        SubActionButton orderallBtn = itemBuilder.setContentView(orderall).build();
        orderallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupplierActivity.this, ShopOrderActivity.class);
                intent.putExtra("status", -1);
                intent.putExtra("bespeak", false);
                intent.putExtra("isShop", true);
                startActivity(intent);
                actionMenu.close(true);
            }
        });
// repeat many times:
        SubActionButton.Builder itemBuilder1 = new SubActionButton.Builder(this);
        ImageView orderWaitReceive = new ImageView(this);
        orderWaitReceive.setImageDrawable(getResources().getDrawable(R.drawable.my_order_wait_recevi));
        SubActionButton orderWaitReceiveBtn = itemBuilder1.setContentView(orderWaitReceive).build();
        orderWaitReceiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupplierActivity.this, ShopOrderActivity.class);
                intent.putExtra("status", 1);
                intent.putExtra("bespeak", false);
                intent.putExtra("isShop", true);
                startActivity(intent);
                actionMenu.close(true);
            }
        });
        // repeat many times:
        SubActionButton.Builder itemBuilder2 = new SubActionButton.Builder(this);
        ImageView orderReserve = new ImageView(this);
        orderReserve.setImageDrawable(getResources().getDrawable(R.drawable.my_order_reserve));
        SubActionButton orderReserveBtn = itemBuilder2.setContentView(orderReserve).build();
        orderReserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupplierActivity.this, ShopOrderActivity.class);
                intent.putExtra("bespeak", true);
                intent.putExtra("isShop", true);
                startActivity(intent);
                actionMenu.close(true);
            }
        });
        // repeat many times:
        SubActionButton.Builder itemBuilder3 = new SubActionButton.Builder(this);
        ImageView orderAdd = new ImageView(this);
        orderAdd.setImageDrawable(getResources().getDrawable(R.drawable.my_order_add));
        SubActionButton orderAddBtn = itemBuilder3.setContentView(orderAdd).build();
        orderAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupplierActivity.this, CustomerAddLocalOrderActivity.class);
                intent.putExtra("shopId", shopId);
                startActivity(intent);
                actionMenu.close(true);
            }
        });
        // 添加菜单项
        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(orderAddBtn)
                .addSubActionView(orderReserveBtn)
                .addSubActionView(orderWaitReceiveBtn)
                .addSubActionView(orderallBtn)
                // ...
                .attachTo(actionButton)
                .build();
        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                main_float_icon.setImageDrawable(getResources().getDrawable(R.drawable.my_shop_order_grey));
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                main_float_icon.setImageDrawable(getResources().getDrawable(R.drawable.my_shop_order));
            }
        });
    }
}
