package com.yuangee.flower.customer.fragment.shopping;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.GoodsAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.BackPressedHandler;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class ShoppingFragment extends RxLazyFragment implements ShoppingContract.View, PullLoadMoreRecyclerView.PullLoadMoreListener, BackPressedHandler {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.menu_icon)
    ImageView menuIcon;

    @BindView(R.id.tv_av)
    TextView tvSearch;

    @BindView(R.id.notification_icon)
    ImageView noIcon;

    @BindView(R.id.good_recycler)
    PullLoadMoreRecyclerView plRecycler;

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyView;

    @BindView(R.id.id_drawerLayout)
    DrawerLayout myDrawerLayout;

    @OnClick(R.id.menu_icon)
    void openDawer() {
        myDrawerLayout.openDrawer(Gravity.LEFT);
    }

    ShoppingPresenter presenter;

    GoodsAdapter adapter;

    public List<Goods> goodsList;

    private int page = 0;
    private int limit = 10;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_shopping;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
        presenter = new ShoppingPresenter(getActivity());
        presenter.setMV(new ShoppingModel(getActivity()), this);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        myDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        initRecyclerView();
        isPrepared = false;
    }

    @Override
    protected void initRecyclerView() {

        plRecycler.setFooterViewText("加载中..");

        adapter = new GoodsAdapter(getActivity());
        adapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtil.showMessage(getActivity(), "您点击了" + position);
            }
        });


        plRecycler.setAdapter(adapter);
        plRecycler.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        goodsList = new ArrayList<>();

        adapter.setData(goodsList);

        plRecycler.setOnPullLoadMoreListener(this);

        plRecycler.setRefreshing(true);

        plRecycler.setVerticalScrollBarEnabled(true);

        presenter.getGoodsData(page, limit);
    }

    @Override
    public void onDetach() {
        presenter.onDestroy();
        super.onDetach();
    }

    @Override
    public void showGoods(final List<Goods> list) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        plRecycler.setVisibility(View.VISIBLE);
                        plRecycler.setRefreshing(false);
                        plRecycler.setPullLoadMoreCompleted();
                        if (page == 0) {
                            goodsList = list;
                        } else {
                            goodsList.addAll(list);
                        }
                        adapter.setData(goodsList);
                        if (goodsList.size() > 43) {
                            plRecycler.setHasMore(false);
                        } else {
                            plRecycler.setHasMore(true);
                        }
                    }
                });
            }
        }, 1500);
    }

    @Override
    public void showEmptyView(int tag) {
        plRecycler.setRefreshing(false);
        plRecycler.setVisibility(View.GONE);
        plRecycler.setPullLoadMoreCompleted();
        if (tag == 0) {
            emptyView.setEmptyImage(R.drawable.ic_filed);
            emptyView.setEmptyText("没能获取到任何数据");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
//                    startActivity(intent);
                }
            });
        } else {
            emptyView.setEmptyImage(R.drawable.ic_filed);
            emptyView.setEmptyText("貌似出了点问题，\n点我重试");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 0;
                    presenter.getGoodsData(page, limit);
                }
            });
        }
    }

    @Override
    public void hideEmptyView() {

    }

    @Override
    public void onRefresh() {
        page = 0;
        goodsList.clear();
        plRecycler.setRefreshing(true);
        presenter.getGoodsData(page, limit);
    }

    @Override
    public void onLoadMore() {
        page++;
        presenter.getGoodsData(page, limit);
    }

    @Override
    public boolean onBackPressed() {
        if (myDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            myDrawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        } else {
            return false;
        }
    }

}