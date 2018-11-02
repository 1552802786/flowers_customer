package com.yuangee.flower.customer.fragment.shopping;

import android.content.Context;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.result.PageResult;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public class ShoppingModel implements ShoppingContract.Model {

    private Context context;

    public ShoppingModel(Context context) {
        this.context = context;
    }

    @Override
    public Observable<PageResult<Goods>> getGoodsData(String genreName, String genreSubName, String params, long page, long limit,String bigOpen,Long shopId,Long sort,Long updown) {
        return ApiManager.getInstance().api.findWares(genreName, genreSubName, params, page * 10, null,limit,bigOpen,App.me().getMemberInfo().areaId, shopId,sort,updown)
                .map(new HttpResultFunc<PageResult<Goods>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PageResult<Goods>> searchGoodsData(String wareName,String generSubs,String params, long page, long limit,String bespeak) {
        return ApiManager.getInstance().api.searchWares(wareName,generSubs,params, page * 10, limit,bespeak)
                .map(new HttpResultFunc<PageResult<Goods>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
