package com.yuangee.flower.customer.fragment.home;

import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Shop;
import com.yuangee.flower.customer.fragment.BasePresenter;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/9/4 0004.
 */

public interface HomeContract {
    interface View {
        void showBanner(List<BannerBean> bannerBeen);

        void showGenre(List<Genre> orders);

        void showRecommendList(List<Recommend> orders);

        /**
         * @param tag 0代表正常返回但没数据  1代表请求异常
         */
        void showEmptyView(int tag);

        void hideEmptyView();

        void showShopIcon(Shop shop);

//        void showOrHideRow(RecyclerView recyclerView);
    }

    abstract class Presenter extends BasePresenter {
        abstract void getBannerData();

        abstract void getRecommendData();

        abstract void getGenreData();

        abstract void getShaop();
    }

    interface Model {
        /**
         * 获取banner数据
         */
        Observable<List<BannerBean>> getBannerData();

        /**
         * 获取首页数据
         */
        Observable<List<Genre>> getAllGenre();

        /**
         * 获取种类数据
         */
        Observable<List<Recommend>> getRecommend();

        Observable<Shop> getShop(long memberId);
    }
}
