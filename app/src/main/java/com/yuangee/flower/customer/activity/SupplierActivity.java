package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.SupplierAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.WaresNumber;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.http.Field;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/8 0008.
 */

public class SupplierActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_search)
    EditText editSearch;

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

    private List<Goods> goodsList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_supplier;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        shopId = getIntent().getLongExtra("shopId", -1);
        shopName = getIntent().getStringExtra("shopName");

        goodsList = new ArrayList<>();
        adapter = new SupplierAdapter(this, mRxManager, shopId, shopName);
        adapter.setOnOrderingClickListener(new SupplierAdapter.OnOrderIngClick() {
            @Override
            public void onOrdering(Goods goods) {
                showCusDialog(goods);
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
    }

    private int qua;

    AlertDialog dialog;

    private void showCusDialog(final Goods goods) {
        View view = LayoutInflater.from(this).inflate(R.layout.change_num_dialog, null);
        final ImageView sub = view.findViewById(R.id.num_sub);
        final ImageView add = view.findViewById(R.id.num_add);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        final TextView num = view.findViewById(R.id.goods_num);

        qua = 1;
        sub.setEnabled(false);
        if (qua < goods.salesVolume) {
            add.setEnabled(true);
        }

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = num.getText().toString();
                if (StringUtils.isNotBlank(str)) {
                    qua = Integer.parseInt(str);
                } else {
                    qua = 0;
                }
                qua--;
                num.setText(String.valueOf(qua));
                if (qua <= 1) {
                    sub.setEnabled(false);
                } else {
                    sub.setEnabled(true);
                }
                if (qua >= goods.salesVolume) {
                    add.setEnabled(false);
                } else {
                    add.setEnabled(true);
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = num.getText().toString();
                if (StringUtils.isNotBlank(str)) {
                    qua = Integer.parseInt(str);
                } else {
                    qua = 0;
                }
                qua++;
                num.setText(String.valueOf(qua));
                if (qua <= 1) {
                    sub.setEnabled(false);
                } else {
                    sub.setEnabled(true);
                }
                if (qua >= goods.salesVolume) {
                    add.setEnabled(false);
                } else {
                    add.setEnabled(true);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = num.getText().toString();
                if (StringUtils.isNotBlank(str)) {
                    qua = Integer.parseInt(str);
                } else {
                    qua = 0;
                }
                if (qua != 0 && qua <= goods.salesVolume) {
                    dialog.dismiss();
                    orderIng(goods.id, qua);
                } else if (qua == 0) {
                    ToastUtil.showMessage(SupplierActivity.this, "数量必须大于0");
                } else {
                    ToastUtil.showMessage(SupplierActivity.this, "数量必须小于最大可售量" + goods.salesVolume);
                }
            }
        });
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        goodRecycler.setRefreshing(true);
        getGoodsData();
    }

    private void getGoodsData() {
        Observable<PageResult<Goods>> observable = ApiManager.getInstance().api
                .findWares(shopId, page * 10, limit)
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

    private void orderIng(long waresId, int num) {

        WaresNumber number = new WaresNumber();
        number.waresId = waresId;
        number.quantity = num;
        Gson gson = new Gson();

        List<WaresNumber> waresNumbers = new ArrayList<>();
        waresNumbers.add(number);

        Member member = DbHelper.getInstance().getMemberLongDBManager().load(App.getPassengerId());
        Observable<Object> observable = ApiManager.getInstance().api
                .cusOrder(member.name, member.phone, member.id, gson.toJson(waresNumbers), shopId)
                .map(new HttpResultFunc<>(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                goodRecycler.setRefreshing(true);
                getGoodsData();
            }
        })));
    }
}
