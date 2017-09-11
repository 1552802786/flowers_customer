package com.yuangee.flower.customer.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.DepthPageTransformer;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.BrowserActivity;
import com.yuangee.flower.customer.adapter.OrderAdapter;
import com.yuangee.flower.customer.adapter.TypeAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Type;
import com.yuangee.flower.customer.util.GlideImageLoader;
import com.yuangee.flower.customer.widget.CustomEmptyView;
import com.yuangee.flower.customer.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * 首页番剧界面
 */
public class HomeFragment extends RxLazyFragment implements HomeContract.View, OnBannerListener {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.banner)
    Banner banner;

    @BindView(R.id.type_recycler)
    RecyclerView typeRecycler;

    @BindView(R.id.left_row)
    ImageView leftRow;

    @BindView(R.id.right_row)
    ImageView rightRow;

    private OrderAdapter recyclerAdapter;
    private TypeAdapter typeAdapter;

    private HomePresenter presenter;

    private long customerId = 0;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
        presenter = new HomePresenter(getActivity());
        presenter.setMV(new HomeModel(getActivity()), this);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }


    @Override
    protected void initRecyclerView() {
        recyclerAdapter = new OrderAdapter(getActivity());
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(recyclerAdapter);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(30));

        typeAdapter = new TypeAdapter(getActivity());
        RecyclerView.LayoutManager horManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        typeRecycler.setLayoutManager(horManager);
        typeRecycler.setAdapter(typeAdapter);
        typeRecycler.addItemDecoration(new SpaceItemDecoration(0, 0, 30, 30, LinearLayout.HORIZONTAL));

        typeRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                showOrHideRow(recyclerView);
            }
        });
    }


    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getRecommendData(customerId);
            }
        });
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                presenter.getBannerData();
                presenter.getRecommendData(customerId);
                presenter.getTypeData();
            }
        }, 1000);
    }

    @Override
    protected void finishTask() {

    }


    @Override
    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showTypeList(List<Type> types) {
        typeAdapter.setData(types);
        showOrHideRow(typeRecycler);
    }

    @Override
    public void showOrHideRow(RecyclerView recyclerView) {
        boolean isBottom = !recyclerView.canScrollHorizontally(1);//返回false不能往右滑动，即代表到最右边了
        boolean isTop = !recyclerView.canScrollHorizontally(-1);//返回false不能往左滑动，即代表到最左边了
        if (isBottom) {
            rightRow.setVisibility(View.INVISIBLE);
        } else {
            rightRow.setVisibility(View.VISIBLE);
        }
        if (isTop) {
            leftRow.setVisibility(View.INVISIBLE);
        } else {
            leftRow.setVisibility(View.VISIBLE);
        }
    }

    private List<BannerBean> bannerBeanList;

    @Override
    public void showBanner(List<BannerBean> bannerBeanList) {
        this.bannerBeanList = bannerBeanList;
        List<String> urls = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (BannerBean bannerBean : bannerBeanList) {
            urls.add(bannerBean.imageUrl);
            titles.add(bannerBean.title);
        }
        banner.setBannerAnimation(DepthPageTransformer.class);
        banner.setImages(urls)
                .setBannerTitles(titles)
                .setImageLoader(new GlideImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setOnBannerListener(this)
                .start();
    }

    @Override
    public void showRecommendList(List<Recommend> orders) {
        recyclerAdapter.setData(orders);
    }

    @Override
    public void showEmptyView(int tag) {
        mSwipeRefreshLayout.setRefreshing(false);
        mRecyclerView.setVisibility(View.GONE);
        if (tag == 0) {
            mCustomEmptyView.setEmptyImage(R.drawable.ic_filed);
            mCustomEmptyView.setEmptyText("您没有任何订单信息，\n点我去下单");
            mCustomEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
//                    startActivity(intent);
                }
            });
        } else {
            mCustomEmptyView.setEmptyImage(R.drawable.ic_filed);
            mCustomEmptyView.setEmptyText("貌似出了点问题，\n点我重试");
            mCustomEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.getRecommendData(customerId);
                }
            });
        }

    }

    @Override
    public void OnBannerClick(int position) {
        String url = bannerBeanList.get(position).linkUrl;
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "活动详情");
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
