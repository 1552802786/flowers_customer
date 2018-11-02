package com.yuangee.flower.customer.fragment.shopping;

import android.content.Context;

import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.BasePresenter;
import com.yuangee.flower.customer.fragment.home.HomeContract;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.util.RxManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public class ShoppingPresenter extends ShoppingContract.Presenter {

    private Context context;

    private ShoppingContract.Model mModel;
    private ShoppingContract.View mView;

    public void setMV(ShoppingContract.Model djMode, ShoppingContract.View view) {
        this.mModel = djMode;
        this.mView = view;
    }

    public ShoppingPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void getGoodsData(String genreName, String genreSubName, String params,
                             final int page, final int limit, String bigopen, Long shopId,Long sort,Long updown) {
        mRxManager.add(mModel.getGoodsData(genreName, genreSubName, params, page, limit,bigopen, shopId,sort,updown)
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

    @Override
    public void searchGoodsData(String wareName,String generSubs,String params,final int page, final int limit,String bespeak) {
        mRxManager.add(mModel.searchGoodsData(wareName,generSubs,params, page, limit,bespeak)
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
