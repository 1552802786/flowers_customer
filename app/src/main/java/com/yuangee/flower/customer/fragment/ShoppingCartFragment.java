package com.yuangee.flower.customer.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.ShoppingAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.home.HomeModel;
import com.yuangee.flower.customer.fragment.home.HomePresenter;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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

        createGoods();

        adapter.setData(goodsList);

        if (goodsList.size() == 0) {
            showEmptyView();
        }
    }

    private void createGoods() {
        for (int i = 0; i < 20; i++) {
            Goods goods = new Goods();
            goods.selectedNum = 1;
            goods.isAddToCar = false;
            String color = "红";
            if (System.currentTimeMillis() % 7 == 0) {
                color = "红";
            } else if (System.currentTimeMillis() % 7 == 1) {
                color = "橙";
            } else if (System.currentTimeMillis() % 7 == 2) {
                color = "黄";
            } else if (System.currentTimeMillis() % 7 == 3) {
                color = "绿";
            } else if (System.currentTimeMillis() % 7 == 4) {
                color = "蓝";
            } else if (System.currentTimeMillis() % 7 == 5) {
                color = "青";
            } else if (System.currentTimeMillis() % 7 == 6) {
                color = "紫";
            }
            goods.goodsColor = color;
            String grade = "A";
            if (System.currentTimeMillis() % 3 == 0) {
                grade = "A";
            } else if (System.currentTimeMillis() % 3 == 1) {
                grade = "B";
            } else if (System.currentTimeMillis() % 3 == 1) {
                grade = "C";
            }
            goods.goodsGrade = grade;
            goods.goodsLeft = "" + 200;
            goods.goodsMoney = "¥200/束";
            goods.goodsName = "鲜花名" + i;
            goods.goodsSpec = i + "00";
            goods.goodsPrice = 200;
            goods.imgPath = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg";
            goodsList.add(goods);
        }
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

}
