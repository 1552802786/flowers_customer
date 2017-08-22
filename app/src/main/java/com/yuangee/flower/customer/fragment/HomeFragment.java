package com.yuangee.flower.customer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.network.RetrofitHelper;
import com.yuangee.flower.customer.widget.CustomEmptyView;
import com.yuangee.flower.customer.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * 首页番剧界面
 */
public class HomeFragment extends RxLazyFragment {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    private int season;
    private boolean mIsRefreshing = false;
//    private List<BannerEntity> bannerList = new ArrayList<>();
    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;
//    private List<BangumiRecommendInfo.ResultBean> bangumiRecommends = new ArrayList<>();
//    private List<BangumiAppIndexInfo.ResultBean.AdBean.HeadBean> banners = new ArrayList<>();
//    private List<BangumiAppIndexInfo.ResultBean.AdBean.BodyBean> bangumibobys = new ArrayList<>();
//    private List<BangumiAppIndexInfo.ResultBean.PreviousBean.ListBean> seasonNewBangumis = new ArrayList<>();
//    private List<BangumiAppIndexInfo.ResultBean.SerializingBean> newBangumiSerials = new ArrayList<>();
//
//    public static HomeBangumiFragment newInstance() {
//        return new HomeBangumiFragment();
//    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
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
        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }


    @Override
    protected void initRecyclerView() {
        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSectionedRecyclerViewAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);
        setRecycleNoScroll();
    }


    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mIsRefreshing = true;
                loadData();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearData();
                loadData();
            }
        });
    }

    private void clearData() {
        mIsRefreshing = true;
//        banners.clear();
//        bannerList.clear();
//        bangumibobys.clear();
//        bangumiRecommends.clear();
//        newBangumiSerials.clear();
//        seasonNewBangumis.clear();
        mSectionedRecyclerViewAdapter.removeAllSections();
    }


    @Override
    protected void loadData() {

        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mIsRefreshing = false;
                    }
                });
//        RetrofitHelper.getBangumiAPI()
//                .getBangumiAppIndex()
//                .compose(bindToLifecycle())
//                .flatMap(new Func1<BangumiAppIndexInfo, Observable<BangumiRecommendInfo>>() {
//                    @Override
//                    public Observable<BangumiRecommendInfo> call(BangumiAppIndexInfo bangumiAppIndexInfo) {
//                        banners.addAll(bangumiAppIndexInfo.getResult().getAd().getHead());
//                        bangumibobys.addAll(bangumiAppIndexInfo.getResult().getAd().getBody());
//                        seasonNewBangumis.addAll(bangumiAppIndexInfo.getResult().getPrevious().getList());
//                        season = bangumiAppIndexInfo.getResult().getPrevious().getSeason();
//                        newBangumiSerials.addAll(bangumiAppIndexInfo.getResult().getSerializing());
//                        return RetrofitHelper.getBangumiAPI().getBangumiRecommended();
//                    }
//                })
//                .compose(bindToLifecycle())
//                .map(BangumiRecommendInfo::getResult)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(resultBeans -> {
//                    bangumiRecommends.addAll(resultBeans);
//                    finishTask();
//                }, throwable -> initEmptyView());
    }


    @Override
    protected void finishTask() {
//        mSwipeRefreshLayout.setRefreshing(false);
//        mIsRefreshing = false;
//        hideEmptyView();
//        Observable.from(banners)
//                .compose(bindToLifecycle())
//                .forEach(bannersBean -> bannerList.add(new BannerEntity(
//                        bannersBean.getLink(), bannersBean.getTitle(), bannersBean.getImg())));
//        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiBannerSection(bannerList));
//        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiItemSection(getActivity()));
//        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiNewSerialSection(getActivity(), newBangumiSerials));
//        if (!bangumibobys.isEmpty()) {
//            mSectionedRecyclerViewAdapter.addSection(new HomeBangumiBobySection(getActivity(), bangumibobys));
//        }
//        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiSeasonNewSection(getActivity(), season, seasonNewBangumis));
//        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiRecommendSection(getActivity(), bangumiRecommends));
//        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }


    public void initEmptyView() {
//        mSwipeRefreshLayout.setRefreshing(false);
//        mCustomEmptyView.setVisibility(View.VISIBLE);
//        mRecyclerView.setVisibility(View.GONE);
//        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
//        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
//        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mIsRefreshing;
            }
        });
    }
}
