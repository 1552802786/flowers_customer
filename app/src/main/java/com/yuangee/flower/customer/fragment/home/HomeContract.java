package com.yuangee.flower.customer.fragment.home;

import android.support.v7.widget.RecyclerView;

import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Type;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/9/4 0004.
 */

public interface HomeContract {
    interface View {
        void showBanner(List<BannerBean> bannerBeen);

        void showRecommendList(List<Recommend> orders);

        /**
         *
         * @param tag 0代表正常返回但没数据  1代表请求异常
         */
        void showEmptyView(int tag);

        void hideEmptyView();

        void showTypeList(List<Type> types);

        void showOrHideRow(RecyclerView recyclerView);
    }

    interface Presenter {
        void getBannerData();

        void getRecommendData(long customerId);

        void getTypeData();

        void onDestroy();
    }

    interface Model {
        /**
         * 获取banner数据
         */
        Observable<List<BannerBean>> getBannerData();

        /**
         * 获取首页数据
         */
        Observable<List<Recommend>> getRecommendData(long customerId);
        /**
         * 获取种类数据
         */
        Observable<List<Type>> getTypeData();
    }
}
