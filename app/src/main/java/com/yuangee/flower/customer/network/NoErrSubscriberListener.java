package com.yuangee.flower.customer.network;

/**
 * Created by Administrator on 2016/9/26.
 */

public interface NoErrSubscriberListener<T> {
    void onNext(T t);
}
