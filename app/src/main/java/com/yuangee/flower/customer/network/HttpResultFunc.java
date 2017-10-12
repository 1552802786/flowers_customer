package com.yuangee.flower.customer.network;

import android.content.Context;

import com.yuangee.flower.customer.result.BaseResult;

import rx.functions.Func1;

/**
 * Created by Administrator on 2016/9/26.
 */


/**
 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
public class HttpResultFunc<T> implements Func1<BaseResult<T>, T> {
    private Context context;

    public HttpResultFunc(Context context) {
        this.context = context;
    }

    @Override
    public T call(BaseResult<T> httpResult) {
        if (httpResult.isSuccess()) {
            throw new ApiException(httpResult.getMsg());
        }
        return httpResult.getData();
    }
}
