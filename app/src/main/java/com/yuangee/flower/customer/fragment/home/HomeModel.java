package com.yuangee.flower.customer.fragment.home;

import android.content.Context;

import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Type;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.RetrofitHelper;

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
        return RetrofitHelper.getFlowerApi().getBannerData()
                .map(new HttpResultFunc<List<BannerBean>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Recommend>> getRecommendData(long customerId) {
        return RetrofitHelper.getFlowerApi().getOrderData(customerId)
                .map(new HttpResultFunc<List<Recommend>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Type>> getTypeData() {
        return RetrofitHelper.getFlowerApi().getTypeData()
                .map(new HttpResultFunc<List<Type>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
