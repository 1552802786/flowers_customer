package com.yuangee.flower.customer.fragment.home;

import android.content.Context;

import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Type;
import com.yuangee.flower.customer.util.RxManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/9/4 0004.
 */

public class HomePresenter implements HomeContract.Presenter {

    private Context context;

    private HomeContract.Model mModel;
    private HomeContract.View mView;

    private RxManager mRxManager = new RxManager();

    public void setMV(HomeContract.Model djMode, HomeContract.View view) {
        this.mModel = djMode;
        this.mView = view;
    }

    public HomePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void getBannerData() {
        mView.showBanner(createBanner());
//        mRxManager.add(mModel.getBannerData().subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<List<BannerBean>>() {
//            @Override
//            public void onNext(List<BannerBean> bannerBeanList) {
//                mView.showBanner(bannerBeanList);
//            }
//
//            @Override
//            public void onError(int code) {
//            }
//        })));
    }

    @Override
    public void getRecommendData(long customerId) {
        mView.showRecommendList(createOrder());
        mView.hideEmptyView();
//        mRxManager.add(mModel.getOrdersData(customerId).subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<List<Recommend>>() {
//            @Override
//            public void onNext(List<Recommend> orderList) {
//                mView.showOrderList(orderList);
//                mView.hideEmptyView();
//            }
//
//            @Override
//            public void onError(int code) {
//                mView.showEmptyView();
//            }
//        })));
    }

    @Override
    public void getTypeData() {
        mView.showTypeList(createType());
//        mRxManager.add(mModel.getOrdersData(customerId).subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<List<Recommend>>() {
//            @Override
//            public void onNext(List<Recommend> orderList) {
//                mView.showOrderList(orderList);
//                mView.hideEmptyView();
//            }
//
//            @Override
//            public void onError(int code) {
//                mView.showEmptyView();
//            }
//        })));
    }

    @Override
    public void onDestroy() {
        mRxManager.clear();
    }

    private List<BannerBean> createBanner() {
        List<BannerBean> bannerBeanList = new ArrayList<>();
        BannerBean bean1 = new BannerBean();
        bean1.imageUrl = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg";
        bean1.linkUrl = "http://www.baidu.com";
        bean1.title = "开学大酬宾";
        bannerBeanList.add(bean1);

        BannerBean bean2 = new BannerBean();
        bean2.imageUrl = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg";
        bean2.linkUrl = "http://www.baidu.com";
        bean2.title = "劲爆优惠，打一张送一张";
        bannerBeanList.add(bean2);

        BannerBean bean3 = new BannerBean();
        bean3.imageUrl = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg";
        bean3.linkUrl = "http://www.baidu.com";
        bean3.title = "注册立送二十张打印量";
        bannerBeanList.add(bean3);

        BannerBean bean4 = new BannerBean();
        bean4.imageUrl = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg";
        bean4.linkUrl = "http://www.baidu.com";
        bean4.title = "推荐有奖，好友打印立得现金";
        bannerBeanList.add(bean4);

        BannerBean bean5 = new BannerBean();
        bean5.imageUrl = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2e7vsaj30ci08cglz.jpg";
        bean5.linkUrl = "http://www.baidu.com";
        bean5.title = "专属周日，全场5折";
        bannerBeanList.add(bean5);
        return bannerBeanList;
    }

    private List<Recommend> createOrder() {
        List<Recommend> orderList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Recommend order1 = new Recommend();
            order1.status = (int) (System.currentTimeMillis() % 3);
            order1.createdTime = System.currentTimeMillis();
            order1.money = 22.5;
            order1.orderNum = "1101100178";
            order1.imgPath = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2e7vsaj30ci08cglz.jpg";

            orderList.add(order1);
        }

        return orderList;
    }

    private List<Type> createType() {
        List<Type> typeList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Type type = new Type();
            type.typeName = "种类" + i;
            typeList.add(type);
        }

        return typeList;
    }
}
