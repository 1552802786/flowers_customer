package com.yuangee.flower.customer.fragment.shopping;

import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.BasePresenter;
import com.yuangee.flower.customer.result.PageResult;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public interface ShoppingContract {
    interface View {
        void showGoods(int page,int limit, PageResult<Goods> goodsList);

        /**
         * @param tag 0代表正常返回但没数据  1代表请求异常
         */
        void showEmptyView(int tag);

        void hideEmptyView();

    }

    abstract class Presenter extends BasePresenter {
        abstract void getGoodsData(String genreName, String genreSubName,
                                   String params, int page, int limit
                ,String bigopen,Long shopId,Long sort,Long updown);
        abstract void searchGoodsData(String wareName,String generSubs,String params, int page, int limit,String bespeak);
    }

    interface Model {
        /**
         * 获取banner数据
         */
        Observable<PageResult<Goods>> getGoodsData(String genreName, String genreSubName,
                                                   String params, long page, long limit,String bigOpen,Long shopId,Long sort,Long updown);
        /**
         * 获取banner数据
         */
        Observable<PageResult<Goods>> searchGoodsData(String wareName,String generSubs,String params, long page, long limit,String bespeak);
    }
}
