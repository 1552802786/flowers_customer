package com.yuangee.flower.customer.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.EditAddressActivity;
import com.yuangee.flower.customer.activity.MainActivity;
import com.yuangee.flower.customer.activity.MyOrderActivity;
import com.yuangee.flower.customer.activity.SecAddressActivity;
import com.yuangee.flower.customer.adapter.CouponAdapter;
import com.yuangee.flower.customer.adapter.KuaidiAdapter;
import com.yuangee.flower.customer.adapter.ShoppingCartAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.CartItem;
import com.yuangee.flower.customer.entity.Coupon;
import com.yuangee.flower.customer.entity.Express;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.result.QueryCartResult;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class ShoppingCartFragment extends RxLazyFragment implements ShoppingCartAdapter.OnMoneyChangedListener {

    @BindView(R.id.shopping_car_recycler)
    PullLoadMoreRecyclerView shoppingRecycle;

    @BindView(R.id.total)
    TextView totalText;

    @BindView(R.id.apply)
    TextView apply;

    @OnClick(R.id.apply)
    void apply() {
        if (!expandableLayout.isExpanded()) {
            expandableLayout.expand();
            apply.setText("确认订单");
        } else {
            Express express = kuaidiAdapter.getClicked();
            if (null != express) {
                expressId = express.id;
            }

            final Coupon coupon = couponAdapter.getClicked();

            if (address == null) {
                ToastUtil.showMessage(getActivity(), "请选择一个收货地址");
            } else if (expressId == -1) {
                ToastUtil.showMessage(getActivity(), "请选择一个快递方式");
            } else {
                if (jishiGou.isChecked()) {
                    booking(App.getPassengerId(), address.shippingName, address.shippingPhone,
                            address.pro + address.city + address.area + address.street, expressId,
                            coupon == null ? null : coupon.id);
                } else {
                    //TODO timePicker
                    long time = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.CHINA);
                    calendar.setTime(new Date(time));
                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            yuyueBooking(App.getPassengerId(), address.shippingName, address.shippingPhone,
                                    address.pro + address.city + address.area + address.street, expressId,
                                    i + "-" + i1 + "-" + i2 + " " + "00:00",
                                    coupon == null ? null : coupon.id);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }
            }
        }
    }

    void initKuadiAdapter() {
        kuaidiAdapter = new KuaidiAdapter(getActivity());
        kuaidiRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2
                , LinearLayoutManager.HORIZONTAL, false));
        kuaidiRecycler.setAdapter(kuaidiAdapter);
        initCouponAdapter();
    }

    private CouponAdapter couponAdapter;

    void initCouponAdapter() {
        couponAdapter = new CouponAdapter(getActivity());
        couponRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        couponRecycler.setAdapter(couponAdapter);
    }

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyLayout;

    @BindView(R.id.content)
    LinearLayout content;

    @BindView(R.id.express)
    TextView expressTxt;

    @BindView(R.id.jishi_gou)
    RadioButton jishiGou;

    @BindView(R.id.yuyue_gou)
    RadioButton yuyueGou;

    @BindView(R.id.expand_layout)
    ExpandableLayout expandableLayout;

    @BindView(R.id.shouhuo_name)
    TextView shouhuoName;

    @BindView(R.id.shouhuo_phone)
    TextView shouhuoPhone;

    @BindView(R.id.shouhuo_addr)
    TextView shouhuoAddr;

    @BindView(R.id.kuaidi_recycler)
    RecyclerView kuaidiRecycler;

    @BindView(R.id.coupon_recycler)
    RecyclerView couponRecycler;

    @BindView(R.id.coupon_text)
    TextView couponText;

    @OnClick(R.id.close_expand_img)
    void closeExpandImg() {
        expandableLayout.collapse();
        apply.setText("提交订单");
    }

    @OnClick(R.id.close_expand_view)
    void closeExpandView() {
        expandableLayout.collapse();
        apply.setText("提交订单");
    }

    private ShoppingCartAdapter adapter;

    private KuaidiAdapter kuaidiAdapter;

    private ToSpecifiedFragmentListener toSpecifiedFragmentListener;

    public static final int REQUEST_ADDR = 0X00;

    private List<CartItem> items;

    public void setToSpecifiedFragmentListener(ToSpecifiedFragmentListener toSpecifiedFragmentListener) {
        this.toSpecifiedFragmentListener = toSpecifiedFragmentListener;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_shopping_cart;
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
        initRecyclerView();
        isPrepared = false;

        jishiGou.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    jishiGou.setTextColor(getResources().getColor(R.color.colorAccent));
                    yuyueGou.setTextColor(getResources().getColor(R.color.txt_normal));
                    showList(false);
                }
            }
        });
        yuyueGou.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    jishiGou.setTextColor(getResources().getColor(R.color.txt_normal));
                    yuyueGou.setTextColor(getResources().getColor(R.color.colorAccent));
                    showList(true);
                }
            }
        });
    }

    private Address address;

    @Override
    protected void initRecyclerView() {
        items = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new ShoppingCartAdapter(getActivity(), mRxManager);
        adapter.setOnMoneyChangedListener(this);
        shoppingRecycle.getRecyclerView().setLayoutManager(manager);

        shoppingRecycle.setAdapter(adapter);

        shoppingRecycle.setHasMore(false);

        shoppingRecycle.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                queryCart(App.getPassengerId());
            }

            @Override
            public void onLoadMore() {

            }
        });

        initKuadiAdapter();

        Member member = App.me().getMemberInfo();
        if (null != member.memberAddressList && member.memberAddressList.size() != 0) {
            for (Address addr : member.memberAddressList) {
                address = addr;
                if (address.defaultAddress) {
                    break;
                }
            }
        }
        setShippingMsg();
        shouhuoName.setOnClickListener(shippingOnClick);
        shouhuoAddr.setOnClickListener(shippingOnClick);
        shouhuoPhone.setOnClickListener(shippingOnClick);
    }

    View.OnClickListener shippingOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (address == null) {
                Intent intent = new Intent(getActivity(), EditAddressActivity.class);
                intent.putExtra("from", "shoppingCart");
                startActivityForResult(intent, REQUEST_ADDR);
            } else {
                Intent intent = new Intent(getActivity(), SecAddressActivity.class);
                intent.putExtra("address", address);
                startActivityForResult(intent, REQUEST_ADDR);
            }
        }
    };

    private void setShippingMsg() {
        if (address != null) {
            shouhuoAddr.setText(address.getStreet());
            shouhuoName.setText(address.getShippingName());
            shouhuoPhone.setText(address.getShippingPhone());
            shouhuoAddr.setTextColor(getResources().getColor(R.color.black));
            shouhuoName.setTextColor(getResources().getColor(R.color.black));
            shouhuoPhone.setTextColor(getResources().getColor(R.color.black));
        } else {
            shouhuoAddr.setText("请选择收货地址");
            shouhuoName.setText("请选择收货人姓名");
            shouhuoPhone.setText("请选择收货人电话");
            shouhuoAddr.setTextColor(getResources().getColor(R.color.txt_normal));
            shouhuoName.setTextColor(getResources().getColor(R.color.txt_normal));
            shouhuoPhone.setTextColor(getResources().getColor(R.color.txt_normal));
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        queryCart(App.getPassengerId());
        findByExpressDeliveryAll();
    }

    public void showEmptyView(int tag) {

        shoppingRecycle.setRefreshing(false);
        content.setVisibility(View.GONE);
        shoppingRecycle.setPullLoadMoreCompleted();
        emptyLayout.setVisibility(View.VISIBLE);
        if (tag == 0) {
            emptyLayout.setEmptyImage(R.drawable.ic_filed);
            emptyLayout.setEmptyText("购物车空空如也，点我去下单吧");
            emptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != toSpecifiedFragmentListener) {
                        toSpecifiedFragmentListener.toFragment(1);//跳转到购物界面
                    }
                }
            });
        } else {
            emptyLayout.setEmptyImage(R.drawable.ic_filed);
            emptyLayout.setEmptyText("貌似出了点问题，\n点我重试");
            emptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    queryCart(App.getPassengerId());
                }
            });
        }
    }

    private void hideEmpty() {
        content.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    /**
     * 查询购物车中所有商品
     *
     * @param memberId
     */
    private void queryCart(long memberId) {
        shoppingRecycle.setRefreshing(true);
        Observable<QueryCartResult> observable = ApiManager.getInstance().api
                .queryCart(memberId)
                .map(new HttpResultFunc<QueryCartResult>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), false, false, new HaveErrSubscriberListener<QueryCartResult>() {
            @Override
            public void onNext(QueryCartResult result) {
                items = result.items;

                showList(yuyueGou.isChecked());
                shoppingRecycle.setPullLoadMoreCompleted();

            }

            @Override
            public void onError(int code) {
                showEmptyView(code);
                shoppingRecycle.setPullLoadMoreCompleted();
            }
        })));
    }

    private void showList(boolean bespeak) {
        List<CartItem> lsItems = new ArrayList<>();
        double money = 0.0;
        int goodsNum = 0;
        for (CartItem item : items) {
            goodsNum += item.quantity;
            if (item.bespeak == bespeak) {//预约购
                lsItems.add(item);
                money += item.totalPrice;
            }
        }
        ((MainActivity) getActivity()).setBudge(goodsNum);
        totalText.setText(money + "元");
        couponAdapter.setOrderMoney(money);
        adapter.setData(lsItems);
        if (lsItems.size() == 0) {
            showEmptyView(0);
        } else {
            hideEmpty();
        }
    }

    private void booking(final long memberId, String receiverName,
                         String receiverPhone, String receiverAddress, long expressId, Long couponId) {
        Observable<Object> observable = ApiManager.getInstance().api
                .confirmOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId, couponId)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                expandableLayout.collapse();
                apply.setText("提交订单");
                queryCart(memberId);
            }

            @Override
            public void onError(int code) {
                //TODO 下单失败
            }
        })));
    }

    private void yuyueBooking(final long memberId, String receiverName,
                              String receiverPhone, String receiverAddress, long expressId, String date, Long couponId) {
        Observable<Object> observable = ApiManager.getInstance().api
                .bespeakOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId, date, couponId)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                expandableLayout.collapse();
                apply.setText("提交订单");
                queryCart(memberId);
            }

            @Override
            public void onError(int code) {
                //TODO 下单失败
            }
        })));
    }

    @Override
    public void onMoneyChange() {
        List<CartItem> items = adapter.getList();
        double totalPrice = 0.0;
        if (items.size() != 0) {
            for (CartItem item : items) {
                totalPrice += item.totalPrice;
            }
        } else {
            showEmptyView(0);
        }
        totalText.setText("" + totalPrice);
        couponAdapter.setOrderMoney(totalPrice);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDR) {
            if (resultCode == RESULT_OK) {
                address = (Address) data.getSerializableExtra("address");
                setShippingMsg();
            }
        }
    }

    /**
     * 查询所有快递
     */
    private void findByExpressDeliveryAll() {
        queryCoupon(App.getPassengerId());
        Observable<List<Express>> observable = ApiManager.getInstance().api
                .findByExpressDeliveryAll()
                .map(new HttpResultFunc<List<Express>>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new NoErrSubscriberListener<List<Express>>() {
            @Override
            public void onNext(List<Express> expresses) {
                kuaidiAdapter.setExpressList(expresses);
            }
        })));
    }

    private void queryCoupon(long memberId) {
        Observable<List<Coupon>> observable = ApiManager.getInstance().api
                .findCoupon(memberId)
                .map(new HttpResultFunc<List<Coupon>>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new NoErrSubscriberListener<List<Coupon>>() {
            @Override
            public void onNext(List<Coupon> coupons) {
                if (null != coupons && coupons.size() != 0) {
                    couponText.setVisibility(View.VISIBLE);
                    couponAdapter.setCoupons(coupons);
                } else {
                    couponText.setVisibility(View.GONE);
                }
            }
        })));
    }

    private long expressId = -1;
}
