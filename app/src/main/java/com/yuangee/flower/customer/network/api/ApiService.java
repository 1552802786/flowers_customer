package com.yuangee.flower.customer.network.api;

import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Type;
import com.yuangee.flower.customer.result.BaseResult;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public interface ApiService {
    /**
     * 获取banner
     */
    @GET("driver/api/rest/v4/acceptTask")
    Observable<BaseResult<List<BannerBean>>> getBannerData();

    /**
     * 获取订单信息
     */
    @GET("driver/api/rest/v4/acceptTask")
    Observable<BaseResult<List<Recommend>>> getOrderData(@Query("customerId") long customerId);
    /**
     * 获取订单信息
     */
    @GET("driver/api/rest/v4/acceptTask")
    Observable<BaseResult<List<Type>>> getTypeData();
}
