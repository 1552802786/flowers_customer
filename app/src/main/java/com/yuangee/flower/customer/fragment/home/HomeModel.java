package com.yuangee.flower.customer.fragment.home;

import android.content.Context;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Shop;
import com.yuangee.flower.customer.entity.Type;
import com.yuangee.flower.customer.network.HttpResultFunc;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/9/4 0004.
 */

public class HomeModel implements HomeContract.Model {

    private Context context;

    public HomeModel(Context context) {
        this.context = context;
    }

    @Override
    public Observable<List<BannerBean>> getBannerData() {
        return ApiManager.getInstance().api.getBannerData()
                .map(new HttpResultFunc<List<BannerBean>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Genre>> getAllGenre() {
        return ApiManager.getInstance().api
                .findAllGenre()
                .map(new HttpResultFunc<List<Genre>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Recommend>> getRecommend() {
        return ApiManager.getInstance().api.findAllRecommend()
                .map(new HttpResultFunc<List<Recommend>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Shop> getShop(long memberId) {
        return ApiManager.getInstance().api.findShopByMemberId(memberId)
                .map(new HttpResultFunc<Shop>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
