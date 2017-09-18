package com.yuangee.flower.customer.fragment.shopping;

import android.content.Context;

import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.RetrofitHelper;

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
    public Observable<List<Goods>> getGoodsData(int page, int limit) {
        return RetrofitHelper.getFlowerApi().getGoodsData(page, limit)
                .map(new HttpResultFunc<List<Goods>>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
