package com.yuangee.flower.customer.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.LoginActivity;
import com.yuangee.flower.customer.activity.PersonalCenterActivity;
import com.yuangee.flower.customer.activity.RegisterActivity;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class MineFragment extends RxLazyFragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_av)
    TextView tvAv;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appbarLayout;

    @BindView(R.id.notification_icon)
    ImageView notificationIcon;

    @OnClick(R.id.notification_icon)
    void toMessage() {

    }

    @OnClick(R.id.mine_top)
    void toPersonal() {
        startActivity(new Intent(getActivity(), PersonalCenterActivity.class));
    }

    @OnClick(R.id.order_detail_con)
    void toDetail() {
        ToastUtil.showMessage(getActivity(), "订单详情");
    }

    @OnClick(R.id.wait_receive)
    void waitReceive() {
        ToastUtil.showMessage(getActivity(), "待收货");
    }

    @OnClick(R.id.book)
    void book() {
        ToastUtil.showMessage(getActivity(), "预约");
    }

    @OnClick(R.id.send_goods)
    void sendGoods() {
        ToastUtil.showMessage(getActivity(), "待我发货");
    }

    @OnClick(R.id.to_agreement)
    void toAgreement() {
        ToastUtil.showMessage(getActivity(), "服务条款");
    }

    @OnClick(R.id.shouhou_rule)
    void shoufeiRule() {
        ToastUtil.showMessage(getActivity(), "售后规则");
    }

    @OnClick(R.id.yunfei_rule)
    void yunfeiRule() {
        ToastUtil.showMessage(getActivity(), "运费规则");
    }

    @OnClick(R.id.be_supplier)
    void beSupplier() {
        startActivity(new Intent(getActivity(), RegisterActivity.class));
    }

    @OnClick(R.id.feedback)
    void feedback() {
        ToastUtil.showMessage(getActivity(), "意见反馈");
    }

    @OnClick(R.id.call_service)
    void callService() {
        PhoneUtil.call(getActivity(), "15102875535");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initView();
        isPrepared = false;
    }

    private void geConsumerInfo(long id) {
        Observable<Object> observable = ApiManager.getInstance().api
                .findById(id)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<Object>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(int code) {

            }
        })));
    }

    private void initView() {
//        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//                if (i == 0) {
//                    //展开状态
//                    tvAv.setTextColor(getResources().getColor(R.color.colorPrimary));
//                    notificationIcon.setImageResource(R.drawable.ic_notifications_primary);
//                } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
//                    //折叠状态
//                    tvAv.setTextColor(getResources().getColor(R.color.white));
//                    notificationIcon.setImageResource(R.drawable.ic_notifications_white);
//                }
//            }
//        });
    }
}
