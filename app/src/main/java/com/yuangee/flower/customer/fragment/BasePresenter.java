package com.yuangee.flower.customer.fragment;

import com.yuangee.flower.customer.util.RxManager;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public abstract class BasePresenter {

    /**
     * rx请求管理者
     */
    protected RxManager mRxManager = new RxManager();

    /**
     * 结束该presenter相关的rxjava代码的生命周期.
     */
    public void onDestroy() {
        mRxManager.clear();
    }

    /**
     * 一些初始化的操作
     */
    protected void onStart() {

    }
}
