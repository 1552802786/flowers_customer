package com.yuangee.flower.customer.fragment.reserve;

import android.content.Context;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HttpResultFunc;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public class ReserveModel implements ReserveContract.Model {

    private Context context;

    public ReserveModel(Context context) {
        this.context = context;
    }

    @Override
    public Observable<List<Goods>> getGoodsData(int page, int limit) {
        return ApiManager.getInstance().api.getGoodsData(page, limit)
                .map(new HttpResultFunc<List<Goods>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
