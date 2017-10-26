package com.yuangee.flower.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.LoginActivity;
import com.yuangee.flower.customer.activity.RegisterActivity;
import com.yuangee.flower.customer.adapter.ShoppingAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.CartItem;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.home.HomeModel;
import com.yuangee.flower.customer.fragment.home.HomePresenter;
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

public class ShoppingCartFragment extends RxLazyFragment {

    @BindView(R.id.shopping_car_recycler)
    RecyclerView shoppingRecycle;

    @BindView(R.id.total)
    TextView totalText;

    @BindView(R.id.apply)
    TextView apply;

    @OnClick(R.id.apply)
    void apply() {
        ToastUtil.showMessage(getActivity(), "点击了提交订单");
    }

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyLayout;

    @BindView(R.id.content)
    LinearLayout content;

    private List<Goods> goodsList;

    private ShoppingAdapter adapter;

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
        adapter = new ShoppingAdapter(getActivity());

        shoppingRecycle.setLayoutManager(manager);

        shoppingRecycle.setAdapter(adapter);

        queryCart(App.getPassengerId());
    }

    public void showEmptyView() {
        emptyLayout.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
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

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<QueryCartResult>() {
            @Override
            public void onNext(QueryCartResult result) {
                //TODO 获取购物车数据成功


                adapter.setData(goodsList);

                if (goodsList.size() == 0) {
                    showEmptyView();
                }
            }

            @Override
            public void onError(int code) {
                //TODO 获取购物车数据失败
            }
        })));
    }

    private void cartItemAdd(long itemId, long cartId, int num) {
        Observable<CartItem> observable = ApiManager.getInstance().api
                .cartItemAdd(itemId, cartId, num)
                .map(new HttpResultFunc<CartItem>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<CartItem>() {
            @Override
            public void onNext(CartItem o) {
                //TODO 添加商品数量成功
            }

            @Override
            public void onError(int code) {
                //TODO 添加商品数量失败
            }
        })));
    }

    private void cartItemSub(long memberId, long waresId, int num) {
        Observable<Object> observable = ApiManager.getInstance().api
                .cartItemSub(memberId, waresId, num)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                //TODO 减少商品数量成功
            }

            @Override
            public void onError(int code) {
                //TODO 减少商品数量失败
            }
        })));
    }

    private void deleteCartItem(long itemId, long cartId) {
        Observable<Object> observable = ApiManager.getInstance().api
                .deleteCartItem(itemId, cartId)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                //TODO 删除购车中的一栏商品成功
            }

            @Override
            public void onError(int code) {
                //TODO 删除购车中的一栏商品失败
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

}
