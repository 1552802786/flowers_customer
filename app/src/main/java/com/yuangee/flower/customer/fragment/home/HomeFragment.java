package com.yuangee.flower.customer.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.DepthPageTransformer;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.AppBus;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.MainActivity;
import com.yuangee.flower.customer.activity.MessageActivity;
import com.yuangee.flower.customer.activity.SearchAcitvity;
import com.yuangee.flower.customer.activity.SupplierActivity;
import com.yuangee.flower.customer.activity.WebActivity;
import com.yuangee.flower.customer.adapter.GoodsAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.HadOpenArea;
import com.yuangee.flower.customer.entity.InformationEntity;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Shop;
import com.yuangee.flower.customer.fragment.ToSpecifiedFragmentListener;
import com.yuangee.flower.customer.fragment.shopping.ShoppingPresenter;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.util.GlideImageLoader;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;
import com.yuangee.flower.customer.widget.SwipeRecyclerView;
import com.yuangee.flower.customer.widget.sectioned.SectionedRecyclerViewAdapter;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    public PageResult<Goods> result;
    private List<String> infoStr;

    @OnClick(R.id.notification_icon)
    void toMessage() {
        startActivity(new Intent(getActivity(), MessageActivity.class));
    }

    @OnClick(R.id.location_icon)
    void chooseArea() {
        ((MainActivity)getActivity()).showAreaList();
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
        Intent intent = new Intent(getActivity(), SearchAcitvity.class);
        startActivity(intent);
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

    List<Recommend> moreGoodWares = new ArrayList<>();
    List<Recommend> buyAllpeople = new ArrayList<>();
    List<Recommend> newWares = new ArrayList<>();
    List<Recommend> sellWares = new ArrayList<>();
    List<Recommend> shop = new ArrayList<>();

    /**
     * 网络加载任务完成
     */
    @Override
    protected void finishTask() {
        hideLoading();
        moreGoodWares.clear();
        buyAllpeople.clear();
        newWares.clear();
        sellWares.clear();
        shop.clear();
        swipeRecyclerView.complete();
        if (recommends.size() == 0 && genreList.size() == 0) {
            showEmptyView(0);
        }

        mSectionedAdapter.addSection(new HomeBigGenreSelection(infoStr, getActivity()));
        for (Recommend re : recommends) {
            if ("今日推荐".equalsIgnoreCase(re.module)) {
                moreGoodWares.add(re);
            } else if ("聚划算".equalsIgnoreCase(re.module)) {
                buyAllpeople.add(re);
            } else if ("新货尝鲜".equalsIgnoreCase(re.module)) {
                newWares.add(re);
            } else if ("超低价".equalsIgnoreCase(re.module)) {
                sellWares.add(re);
            } else if ("优秀供货商".equalsIgnoreCase(re.module)) {
                shop.add(re);
            }
        }
        if (moreGoodWares.size() > 0) {
            mSectionedAdapter.addSection(new HomeRecommedSelection(moreGoodWares, getActivity(), new StatelessSection.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Recommend recommend = moreGoodWares.get(position);
                    Intent intent = new Intent(getActivity(), SearchAcitvity.class);
                    intent.putExtra("params", recommend.keywords);
                    startActivity(intent);
                }
            }));
        }
        if (buyAllpeople.size() > 0) {
            mSectionedAdapter.addSection(new HomeRecommedSelection(buyAllpeople, getActivity(), new StatelessSection.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Recommend recommend = buyAllpeople.get(position);
                    Intent intent = new Intent(getActivity(), SearchAcitvity.class);
                    intent.putExtra("params", recommend.keywords);
                    startActivity(intent);
                }
            }));
        }
        if (newWares.size() > 0) {
            mSectionedAdapter.addSection(new HomeRecommedSelection(newWares, getActivity(), new StatelessSection.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Recommend recommend = newWares.get(position);
                    Intent intent = new Intent(getActivity(), SearchAcitvity.class);
                    intent.putExtra("params", recommend.keywords);
                    startActivity(intent);
                }
            }));
        }
        if (sellWares.size() > 0) {
            mSectionedAdapter.addSection(new HomeRecommedSelection(sellWares, getActivity(), new StatelessSection.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Recommend recommend = sellWares.get(position);
                    Intent intent = new Intent(getActivity(), SearchAcitvity.class);
                    intent.putExtra("params", recommend.keywords);
                    startActivity(intent);
                }
            }));
        }
        if (result.rows.size() > 0) {
            GoodsAdapter adapter = new GoodsAdapter(getActivity(), 0, mRxManager);
            adapter.setData(result.rows);
            mSectionedAdapter.addSection(new HomeBottomSelection(getActivity(), adapter));
        }
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
            urls.add(Config.BASE_URL + bannerBean.imageUrl);
            titles.add(bannerBean.title);
        }
        banner.setBannerAnimation(DepthPageTransformer.class);
        banner.setImages(urls)
                .setImageLoader(new GlideImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setIndicatorGravity(BannerConfig.CENTER)
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
        Observable<PageResult<InformationEntity>> obs = ApiManager.getInstance().api.queryMessageInfo()
                .map(new HttpResultFunc<PageResult<InformationEntity>>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(obs.subscribe(new MySubscriber<>(getActivity(), false, false, new HaveErrSubscriberListener<PageResult<InformationEntity>>() {
            @Override
            public void onNext(PageResult<InformationEntity> result) {
                infoStr = new ArrayList<>();
                for (InformationEntity entity : result.rows) {
                    infoStr.add(entity.name);
                }
            }

            @Override
            public void onError(int code) {

            }
        })));
        presenter.getBannerData();
        presenter.getRecommendData();
        presenter.getShaop();
        swipeRecyclerView.setVisibility(View.VISIBLE);
        mCustomEmptyView.setVisibility(View.GONE);
        Observable<PageResult<Goods>> observable = ApiManager.getInstance().api.
                findWares("", "", "", 0L, null, 10L, null, App.me().getMemberInfo().areaId, null, null, null)
                .map(new HttpResultFunc<PageResult<Goods>>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<PageResult<Goods>>() {
            @Override
            public void onNext(PageResult<Goods> pageResult) {
                result = pageResult;
            }

            @Override
            public void onError(int code) {

            }
        })));

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
        Intent intent = new Intent(getActivity(), WebActivity.class);
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
