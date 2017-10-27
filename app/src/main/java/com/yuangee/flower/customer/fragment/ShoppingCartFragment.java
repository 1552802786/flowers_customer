package com.yuangee.flower.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.SecAddressActivity;
import com.yuangee.flower.customer.adapter.ShoppingCartAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.CartItem;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.result.QueryCartResult;
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
 * Created by developerLzh on 2017/8/21 0021.
 */

public class ShoppingCartFragment extends RxLazyFragment implements ShoppingCartAdapter.OnMoneyChangedListener {

    @BindView(R.id.shopping_car_recycler)
    PullLoadMoreRecyclerView shoppingRecycle;

    @BindView(R.id.total)
    TextView totalText;

    @BindView(R.id.apply)
    TextView apply;

    @OnClick(R.id.apply)
    void apply() {
        startActivity(new Intent(getActivity(), SecAddressActivity.class));
    }

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyLayout;

    @BindView(R.id.content)
    LinearLayout content;

    private List<Goods> goodsList;

    private ShoppingCartAdapter adapter;

    private ToSpecifiedFragmentListener toSpecifiedFragmentListener;

    public void setToSpecifiedFragmentListener(ToSpecifiedFragmentListener toSpecifiedFragmentListener) {
        this.toSpecifiedFragmentListener = toSpecifiedFragmentListener;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_shopping_cart;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initRecyclerView();
        isPrepared = false;
    }

    @Override
    protected void initRecyclerView() {
        goodsList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new ShoppingCartAdapter(getActivity(), mRxManager);
        adapter.setOnMoneyChangedListener(this);
        shoppingRecycle.getRecyclerView().setLayoutManager(manager);

        shoppingRecycle.setAdapter(adapter);

        shoppingRecycle.setHasMore(false);

        shoppingRecycle.setRefreshing(true);

        queryCart(App.getPassengerId());
    }

    public void showEmptyView(int tag) {

        shoppingRecycle.setRefreshing(false);
        content.setVisibility(View.GONE);
        shoppingRecycle.setPullLoadMoreCompleted();
        emptyLayout.setVisibility(View.VISIBLE);
        if (tag == 0) {
            emptyLayout.setEmptyImage(R.drawable.ic_filed);
            emptyLayout.setEmptyText("购物车空空如也，点我去下单吧");
            emptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != toSpecifiedFragmentListener) {
                        toSpecifiedFragmentListener.toFragment(1);//跳转到购物界面
                    }
                }
            });
        } else {
            emptyLayout.setEmptyImage(R.drawable.ic_filed);
            emptyLayout.setEmptyText("貌似出了点问题，\n点我重试");
            emptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    queryCart(App.getPassengerId());
                }
            });
        }
    }

    /**
     * 查询购物车中所有商品
     *
     * @param memberId
     */
    private void queryCart(long memberId) {
        Observable<QueryCartResult> observable = ApiManager.getInstance().api
                .queryCart(memberId)
                .map(new HttpResultFunc<QueryCartResult>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), false, false, new HaveErrSubscriberListener<QueryCartResult>() {
            @Override
            public void onNext(QueryCartResult result) {
                adapter.setData(result.items);
                totalText.setText("合计：" + result.totalPrice);

                if (goodsList.size() == 0) {
                    showEmptyView(0);
                }
            }

            @Override
            public void onError(int code) {
                showEmptyView(code);
            }
        })));
    }

    private void booking(long memberId, String receiverName,
                         String receiverPhone, String receiverAddress, long expressId) {
        Observable<Object> observable = ApiManager.getInstance().api
                .confirmOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                //TODO 下单成功
            }

            @Override
            public void onError(int code) {
                //TODO 下单失败
            }
        })));
    }

    @Override
    public void onMoneyChange() {
        List<CartItem> items = adapter.getList();
        double totalPrice = 0.0;
        if (items.size() != 0) {
            for (CartItem item : items) {
                totalPrice += item.totalPrice;
            }
        } else {
            showEmptyView(0);
        }
        totalText.setText("合计：" + totalPrice);
    }
}
