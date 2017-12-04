package com.yuangee.flower.customer.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.DepthPageTransformer;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.BrowserActivity;
import com.yuangee.flower.customer.activity.MainActivity;
import com.yuangee.flower.customer.activity.MessageActivity;
import com.yuangee.flower.customer.activity.SupplierActivity;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Shop;
import com.yuangee.flower.customer.fragment.ToSpecifiedFragmentListener;
import com.yuangee.flower.customer.util.GlideImageLoader;
import com.yuangee.flower.customer.widget.CustomEmptyView;
import com.yuangee.flower.customer.widget.SwipeRecyclerView;
import com.yuangee.flower.customer.widget.sectioned.SectionedRecyclerViewAdapter;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

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

    @BindView(R.id.swipe_recycler_view)
    SwipeRecyclerView swipeRecyclerView;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.banner)
    Banner banner;

    @BindView(R.id.shop_icon)
    ImageView shopIcon;

    @OnClick(R.id.notification_icon)
    void toMessage() {
        startActivity(new Intent(getActivity(), MessageActivity.class));
    }

    private HomePresenter presenter;

    private List<Genre> genreList = new ArrayList<>();

    private List<Recommend> recommends = new ArrayList<>();

    private SectionedRecyclerViewAdapter mSectionedAdapter;

    private ToSpecifiedFragmentListener toSpecifiedFragmentListener;

    public void setToSpecifiedFragmentListener(ToSpecifiedFragmentListener toSpecifiedFragmentListener) {
        this.toSpecifiedFragmentListener = toSpecifiedFragmentListener;
    }

    @OnClick(R.id.tv_av)
    void toSearch() {
        if (null != toSpecifiedFragmentListener) {
            toSpecifiedFragmentListener.toFragment(-1);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void finishCreateView(Bundle state) {
        presenter = new HomePresenter(getActivity());
        presenter.setMV(new HomeModel(getActivity()), this);
        isPrepared = true;
        lazyLoad();
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
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);

        swipeRecyclerView.getRecyclerView().setLayoutManager(mLayoutManager);

        swipeRecyclerView.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSectionedAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });

        swipeRecyclerView.setAdapter(mSectionedAdapter);
        swipeRecyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {

            }
        });
        swipeRecyclerView.onRefresh();
        swipeRecyclerView.setLoadMoreEnable(false);
    }


    /**
     * 网络加载任务完成
     */
    @Override
    protected void finishTask() {
        hideLoading();
        swipeRecyclerView.complete();
        if (recommends.size() == 0 && genreList.size() == 0) {
            showEmptyView(0);
        }

        mSectionedAdapter.addSection(new HomeBigGenreSelection(genreList, getActivity(), new StatelessSection.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (null != toSpecifiedFragmentListener) {
                    Genre genre = genreList.get(position);
                    toSpecifiedFragmentListener.toShoppingByParams(genre.genreName, null, null);
                }
            }
        }));

        mSectionedAdapter.addSection(new HomeRecommedSelection(recommends, getActivity(), new StatelessSection.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (null != toSpecifiedFragmentListener) {
                    Recommend recommend = recommends.get(position);
                    toSpecifiedFragmentListener.toShoppingByParams(null, null, recommend.keywords);
                }
            }
        }));

        mSectionedAdapter.notifyDataSetChanged();

    }


    @Override
    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        swipeRecyclerView.setRefreshing(false);
    }

    @Override
    public void showShopIcon(final Shop shop) {
        if (shop == null) {
            shopIcon.setVisibility(View.GONE);
        } else {
            shopIcon.setVisibility(View.VISIBLE);
            shopIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SupplierActivity.class);
                    intent.putExtra("shopId", shop.id);
                    intent.putExtra("shopName", shop.name);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void showGenre(List<Genre> genres) {
        genreList.addAll(genres);

        finishTask();

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
    public void showRecommendList(List<Recommend> recommends) {
        this.recommends.addAll(recommends);
        presenter.getGenreData();//推荐商品 商品种类  链式请求
    }

    @Override
    public void showEmptyView(int tag) {
        swipeRecyclerView.complete();
        hideLoading();
        swipeRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        if (tag == 0) {
            mCustomEmptyView.setEmptyImage(R.drawable.ic_filed);
            mCustomEmptyView.setEmptyText("未能获取到任何数据，\n点我重试");
            mCustomEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refresh();
                }
            });
        } else {
            mCustomEmptyView.setEmptyImage(R.drawable.ic_filed);
            mCustomEmptyView.setEmptyText("貌似出了点问题，\n点我重试");
            mCustomEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refresh();
                }
            });
        }
    }

    private void refresh() {
        showLoading(true, "", "请稍候..", null);
        clearData();
        presenter.getBannerData();
        presenter.getRecommendData();
        presenter.getShaop();
        swipeRecyclerView.setVisibility(View.VISIBLE);
        mCustomEmptyView.setVisibility(View.GONE);
    }

    private void clearData() {
        recommends.clear();
        genreList.clear();
        mSectionedAdapter.removeAllSections();
        mSectionedAdapter.notifyDataSetChanged();
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
    public void onDetach() {
        presenter.onDestroy();
        super.onDetach();
    }
}
