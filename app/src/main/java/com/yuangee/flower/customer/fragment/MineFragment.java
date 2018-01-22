package com.yuangee.flower.customer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.CustomerAgreementActivity;
import com.yuangee.flower.customer.activity.CustomerOrderActivity;
import com.yuangee.flower.customer.activity.FeedbackActivity;
import com.yuangee.flower.customer.activity.MainActivity;
import com.yuangee.flower.customer.activity.MessageActivity;
import com.yuangee.flower.customer.activity.ShopOrderActivity;
import com.yuangee.flower.customer.activity.PersonalCenterActivity;
import com.yuangee.flower.customer.activity.RegisterActivity;
import com.yuangee.flower.customer.activity.SupplierActivity;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.CustomerOrder;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.result.SuppStatus;
import com.yuangee.flower.customer.util.GlideCircleTransform;
import com.yuangee.flower.customer.util.PersonUtil;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.StringUtils;
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

    @BindView(R.id.person_photo)
    ImageView personPhoto;

    @BindView(R.id.person_name)
    TextView personName;

    @BindView(R.id.person_vip)
    TextView personVip;

    @BindView(R.id.person_phone)
    TextView personPhone;

    @BindView(R.id.my_dingdan)
    LinearLayout myDingdan;

    @BindView(R.id.be_supplier)
    LinearLayout beSupplier;

    @BindView(R.id.supplier_text)
    TextView supplierText;

    @BindView(R.id.my_all_order)
    TextView myAllOrder;

    @OnClick(R.id.notification_icon)
    void toMessage() {
        startActivity(new Intent(getActivity(), MessageActivity.class));
    }

    @OnClick(R.id.mine_top)
    void toPersonal() {
        startActivity(new Intent(getActivity(), PersonalCenterActivity.class));
    }

    @OnClick(R.id.my_all_order)
    void toDetail() {
        Intent intent = new Intent(getActivity(), CustomerOrderActivity.class);
        intent.putExtra("status", -1);
        intent.putExtra("bespeak", false);
        startActivity(intent);
    }

    @OnClick(R.id.wait_receive)
    void waitReceive() {
        Intent intent = new Intent(getActivity(), CustomerOrderActivity.class);
        intent.putExtra("status", 2);
        intent.putExtra("bespeak", false);
        startActivity(intent);
    }

    @OnClick(R.id.book)
    void book() {
        Intent intent = new Intent(getActivity(), CustomerOrderActivity.class);
        intent.putExtra("status", -1);
        intent.putExtra("bespeak", true);
        startActivity(intent);
    }

    @OnClick(R.id.my_dingdan)
    void myAll() {
        Intent intent = new Intent(getActivity(), CustomerOrderActivity.class);
        intent.putExtra("status", -1);
        intent.putExtra("bespeak", false);
        startActivity(intent);
    }

    @OnClick(R.id.wait_pay)
    void waitPay() {
        Intent intent = new Intent(getActivity(), CustomerOrderActivity.class);
        intent.putExtra("status", 0);
        intent.putExtra("bespeak", false);
        startActivity(intent);
    }

    @OnClick(R.id.jingpai)
    void jingpai() {
        ToastUtil.showMessage(getActivity(), "竞拍");
    }

    @OnClick(R.id.to_agreement)
    void toAgreement() {
        gotoAgreementActivity("服务条款",((MainActivity)getActivity()).getConfig().agreement.serviceAgreement);
    }

    @OnClick(R.id.shouhou_rule)
    void shoufeiRule() {
        gotoAgreementActivity("售后规则",((MainActivity)getActivity()).getConfig().agreement.customerServiceAgreement);
    }

    @OnClick(R.id.yunfei_rule)
    void yunfeiRule() {
        gotoAgreementActivity("运费规则",((MainActivity)getActivity()).getConfig().agreement.freightAgreement);
    }

    @OnClick(R.id.feedback)
    void feedback() {
        startActivity(new Intent(getActivity(), FeedbackActivity.class));
    }
    private void gotoAgreementActivity(String title,String agreement){
        Intent it=new Intent(getActivity(), CustomerAgreementActivity.class);
        it.putExtra("title_agreement",title);
        it.putExtra("web_string",agreement);
        startActivity(it);
    }
    @OnClick(R.id.call_service)
    void callService() {
//        PhoneUtil.call(getActivity(), "15102875535");
        String phone = DbHelper.getInstance().getMemberLongDBManager().load(App.getPassengerId()).customServicePhone;
        if (StringUtils.isNotBlank(phone)) {
            PhoneUtil.call(getActivity(), phone);
        } else {
            ToastUtil.showMessage(getActivity(), "无效的电话号码");
        }
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
//        initView();
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

    private long shopId = -1;
    private String shopName = "";

    private void getConsumerInfo(long id) {
        PersonUtil.getMemberInfo(mRxManager, getActivity(), id, new PersonUtil.OnGetMember() {
            @Override
            public void onSuccess(Member o) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_default_photo)
                        .transform(new GlideCircleTransform())
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(getActivity())
                        .load(Config.BASE_URL + o.photo)
                        .apply(options)
                        .into(personPhoto);

                personName.setText(o.name);
                personVip.setText("普通会员");
                if (o.shop != null) {
                    personVip.setText("供货商");
                    shopId = o.shop.id;
                    App.me().getSharedPreferences().edit().putLong("shopId",shopId).commit();
                    shopName = o.shop.shopName;
                } else {
//                    supplierText.setText("申请成为供货商");
//                    beSupplier.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            startActivity(new Intent(getActivity(), RegisterActivity.class));
//                        }
//                    });
                }
                if (StringUtils.isNotBlank(o.phone) && o.phone.length() == 11) {
                    personPhone.setText(o.phone.subSequence(0, 4) + "****" + o.phone.substring(7, 11));
                } else {
                    personPhone.setText(o.phone);
                }
                if (o.shop == null) {
                    myAllOrder.setVisibility(View.GONE);
                } else {
                    myAllOrder.setVisibility(View.VISIBLE);
                    myAllOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CustomerOrderActivity.class);
                            intent.putExtra("status", -1);
                            intent.putExtra("bespeak", false);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailed() {

            }
        });

    }

    private void initView() {
        getConsumerInfo(App.getPassengerId());
        getSuppStatus();
    }

    private void getSuppStatus() {
        Observable<SuppStatus> observable = ApiManager.getInstance()
                .api.getSuppStatus(App.getPassengerId())
                .map(new HttpResultFunc<SuppStatus>(getActivity()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true,
                new NoErrSubscriberListener<SuppStatus>() {
                    @Override
                    public void onNext(SuppStatus status) {
                        if (status == null) {
                            supplierText.setText("申请供货商");
                            beSupplier.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(), RegisterActivity.class));
                                }
                            });
                        } else {
                            if (status.status == 0) {
                                supplierText.setText("申请审核中..");
                                beSupplier.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ToastUtil.showMessage(getActivity(), "您已经提交过申请，正在审核中..");
                                    }
                                });
                            } else if (status.status == 1) {
                                supplierText.setText("我的店铺");
                                beSupplier.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), SupplierActivity.class);
                                        intent.putExtra("shopId", shopId);
                                        intent.putExtra("shopName", shopName);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                supplierText.setText("申请供货商");
                                beSupplier.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getActivity(), RegisterActivity.class));
                                    }
                                });
                            }
                        }
                    }
                })));
    }
}
