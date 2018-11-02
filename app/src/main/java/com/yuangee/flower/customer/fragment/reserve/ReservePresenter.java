package com.yuangee.flower.customer.fragment.reserve;

import android.content.Context;

import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.shopping.ShoppingContract;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.result.PageResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public class ReservePresenter extends ReserveContract.Presenter {

    private Context context;

    private ReserveContract.Model mModel;
    private ReserveContract.View mView;

    public void setMV(ReserveContract.Model djMode, ReserveContract.View view) {
        this.mModel = djMode;
        this.mView = view;
    }

    public ReservePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void getGoodsData(String genreName, String genreSubName, String params, final int page, final int limit, Long shopId,Long sort,Long updown) {
        mRxManager.add(mModel.getGoodsData(genreName, genreSubName, params, page, limit, shopId,sort,updown)
                .subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<PageResult<Goods>>() {
                    @Override
                    public void onNext(PageResult<Goods> pageResult) {
                        mView.showGoods(page, limit, pageResult);
                    }

                    @Override
                    public void onError(int code) {
                        mView.showEmptyView(code);
                    }
                })));
    }
}
