package com.yuangee.flower.customer.fragment.reserve;

import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.BasePresenter;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public interface ReserveContract {
    interface View {
        void showGoods(List<Goods> goodsList);

        /**
         * @param tag 0代表正常返回但没数据  1代表请求异常
         */
        void showEmptyView(int tag);

        void hideEmptyView();

    }

    abstract class Presenter extends BasePresenter {
        abstract void getGoodsData(int page, int limit);
    }

    interface Model {
        /**
         * 获取banner数据
         */
        Observable<List<Goods>> getGoodsData(int page, int limit);
    }
}
