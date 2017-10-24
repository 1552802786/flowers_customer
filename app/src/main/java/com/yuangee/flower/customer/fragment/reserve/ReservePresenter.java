package com.yuangee.flower.customer.fragment.reserve;

import android.content.Context;

import com.yuangee.flower.customer.entity.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public class ReservePresenter extends ReserveContract.Presenter {

    private Context context;

    private ReserveContract.Model mModel;
    private ReserveContract.View mView;

    public void setMV(ReserveContract.Model djMode, ReserveContract.View view) {
        this.mModel = djMode;
        this.mView = view;
    }

    public ReservePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void getGoodsData(int page, int limit) {
//        mView.showGoods(createGoods());
//        mRxManager.add(mModel.getGoodsData(page, limit).subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<List<Goods>>() {
//            @Override
//            public void onNext(List<Goods> bannerBeanList) {
//                mView.showGoods(bannerBeanList);
//            }
//
//            @Override
//            public void onError(int code) {
//            }
//        })));
    }

//    private List<Goods> createGoods() {
//        List<Goods> goodsList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            Goods goods = new Goods();
//            goods.selectedNum = 1;
//            goods.isAddToCar = false;
//            String color = "红";
//            if (System.currentTimeMillis() % 7 == 0) {
//                color = "红";
//            } else if (System.currentTimeMillis() % 7 == 1) {
//                color = "橙";
//            } else if (System.currentTimeMillis() % 7 == 2) {
//                color = "黄";
//            } else if (System.currentTimeMillis() % 7 == 3) {
//                color = "绿";
//            } else if (System.currentTimeMillis() % 7 == 4) {
//                color = "蓝";
//            } else if (System.currentTimeMillis() % 7 == 5) {
//                color = "青";
//            } else if (System.currentTimeMillis() % 7 == 6) {
//                color = "紫";
//            }
//            goods.goodsColor = color;
//            String grade = "A";
//            if (System.currentTimeMillis() % 3 == 0) {
//                grade = "A";
//            } else if (System.currentTimeMillis() % 3 == 1) {
//                grade = "B";
//            } else if (System.currentTimeMillis() % 3 == 1) {
//                grade = "C";
//            }
//            goods.goodsGrade = grade;
//            goods.goodsLeft = "" + 200;
//            goods.goodsMoney = "¥200/束";
//            goods.goodsName = "鲜花名" + i;
//            goods.goodsSpec = i + "00";
//            goods.imgPath = "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg";
//            goodsList.add(goods);
//        }
//        return goodsList;
//    }
}
