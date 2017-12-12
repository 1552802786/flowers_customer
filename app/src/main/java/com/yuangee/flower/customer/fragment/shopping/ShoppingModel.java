package com.yuangee.flower.customer.fragment.shopping;

import android.content.Context;

import com.yuangee.flower.customer.ApiManager;
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
    public Observable<PageResult<Goods>> getGoodsData(String genreName,String genreSubName,String params,long page, long limit,Long shopId) {
        return ApiManager.getInstance().api.findWares(genreName,genreSubName,params,page, limit,shopId)
                .map(new HttpResultFunc<PageResult<Goods>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
