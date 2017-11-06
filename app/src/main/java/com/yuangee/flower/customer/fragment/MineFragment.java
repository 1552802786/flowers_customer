package com.yuangee.flower.customer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.MessageActivity;
import com.yuangee.flower.customer.activity.MyOrderActivity;
import com.yuangee.flower.customer.activity.PersonalCenterActivity;
import com.yuangee.flower.customer.activity.RegisterActivity;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.List;

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

    @BindView(R.id.person_photo)
    ImageView personPhoto;

    @BindView(R.id.person_name)
    TextView personName;

    @BindView(R.id.person_vip)
    TextView personVip;

    @BindView(R.id.person_phone)
    TextView personPhone;

    @OnClick(R.id.notification_icon)
    void toMessage() {
        startActivity(new Intent(getActivity(), MessageActivity.class));
    }

    @OnClick(R.id.mine_top)
    void toPersonal() {
        startActivity(new Intent(getActivity(), PersonalCenterActivity.class));
    }

    @OnClick(R.id.order_detail_con)
    void toDetail() {
        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
        intent.putExtra("status", -1);
        intent.putExtra("bespeak", false);
        startActivity(intent);
    }

    @OnClick(R.id.wait_receive)
    void waitReceive() {
        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
        intent.putExtra("status", 2);
        intent.putExtra("bespeak", false);
        startActivity(intent);
    }

    @OnClick(R.id.book)
    void book() {
        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
        intent.putExtra("status", -1);
        intent.putExtra("bespeak", true);
        startActivity(intent);
    }

    @OnClick(R.id.send_goods)
    void sendGoods() {
        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
        intent.putExtra("status", 0);
        intent.putExtra("bespeak", true);
        startActivity(intent);
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
        isPrepared = false;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void getConsumerInfo(long id) {
        Observable<Member> observable = ApiManager.getInstance().api
                .findById(id)
                .map(new HttpResultFunc<Member>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Member>() {
            @Override
            public void onNext(Member o) {
                SharedPreferences.Editor editor = App.me().getSharedPreferences().edit();
                List<Address> addressList = o.memberAddressList;
                if (null != addressList) {
                    DbHelper.getInstance().getAddressLongDBManager().insertOrReplaceInTx(addressList);
                } else {
                    DbHelper.getInstance().getAddressLongDBManager().deleteAll();
                }

                editor.putLong("id", o.id);
                editor.putString("name", o.name);
                editor.putString("userName", o.userName);
                editor.putString("passWord", o.passWord);
                editor.putString("phone", o.phone);
                editor.putString("email", o.email);
                editor.putString("photo", o.photo);
                editor.putBoolean("gender", o.gender);
                editor.putString("type", o.type);
                editor.putBoolean("inBlacklist", o.inBlacklist);
                editor.putBoolean("isRecycle", o.isRecycle);
                editor.putBoolean("inFirst", o.inFirst);
                editor.putFloat("balance", (float) o.balance);

                editor.putBoolean("login", true);

                editor.apply();

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_default_photo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(getActivity())
                        .load(Config.BASE_URL + o.photo)
                        .apply(options)
                        .into(personPhoto);

                personName.setText(o.name);
                personVip.setText("普通会员");
                if (StringUtils.isNotBlank(o.phone) && o.phone.length() == 11) {
                    personPhone.setText(o.phone.subSequence(0, 4) + "****" + o.phone.substring(7, 11));
                } else {
                    personPhone.setText(o.phone);
                }

            }

            @Override
            public void onError(int code) {

            }
        })));
    }

    private void initView() {
        getConsumerInfo(App.getPassengerId());
    }
}
