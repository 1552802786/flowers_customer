package com.yuangee.flower.customer.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
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
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.CartItem;
import com.yuangee.flower.customer.entity.Coupon;
import com.yuangee.flower.customer.entity.DestineTime;
import com.yuangee.flower.customer.entity.Express;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.Order;
import com.yuangee.flower.customer.entity.PayResult;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.result.QueryCartResult;
import com.yuangee.flower.customer.util.PersonUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;
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

    private String yuyueTimeStr;

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
                    if (StringUtils.isBlank(yuyueTimeStr)) {
                        ToastUtil.showMessage(getActivity(), "请选择预约时间");
                        return;
                    }
                    yuyueBooking(App.getPassengerId(), address.shippingName, address.shippingPhone,
                            address.pro + address.city + address.area + address.street, expressId,
                            yuyueTimeStr,
                            coupon == null ? null : coupon.id);
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

    @BindView(R.id.select_all)
    CheckBox selectAll;

    @BindView(R.id.yuyue_time_con)
    RelativeLayout yuyueTimeCon;

    @BindView(R.id.yuyue_time)
    TextView yuyueTime;

    @OnClick(R.id.yuyue_time)
    void showTimePicker() {
        List<DestineTime> destineTimeLs = DbHelper.getInstance().getDestineTimeLongDBManager().loadAll();
        DestineTime destineTime = null;
        if (null != destineTimeLs && destineTimeLs.size() != 0) {
            destineTime = destineTimeLs.get(0);
        }
        long startDate = System.currentTimeMillis() + 24L * 60L * 60L * 1000L;//默认当前的后一天
        long endDate = System.currentTimeMillis() + (90L * 24L * 60L * 60L * 1000L);//默认三个月后
        if (null != destineTime) {
            if (destineTime.getValue()) {
                startDate = System.currentTimeMillis() + destineTime.start * 24 * 60 * 60 * 1000;
                endDate = System.currentTimeMillis() + destineTime.end * 24 * 60 * 60 * 1000;
            }
        }
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date(startDate));
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH) + 1;
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(new Date(endDate));
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH) + 1;
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePicker picker = new DatePicker(getActivity());
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(getActivity(), 10));
        picker.setRangeEnd(endYear, endMonth, endDay);
        picker.setRangeStart(startYear, startMonth, startDay);
        picker.setSelectedItem(startYear, startMonth, startDay);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                yuyueTimeStr = year + "-" + month + "-" + day;
                yuyueTime.setTextColor(getResources().getColor(R.color.txt_black));
                yuyueTime.setText(year + "年" + month + "月" + day + "日");
            }
        });
        picker.show();
    }

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
                    yuyueTimeCon.setVisibility(View.GONE);
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
                    yuyueTimeCon.setVisibility(View.VISIBLE);
                    showList(true);
                }
            }
        });
        //全选
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (CartItem cartItem : adapter.getList()) {
                        cartItem.selected = true;
                    }
                } else {
                    for (CartItem cartItem : adapter.getList()) {
                        cartItem.selected = false;
                    }
                }
                adapter.notifyDataSetChanged();
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

        selectAll.setChecked(false);

        queryCart(App.getPassengerId());

        PersonUtil.getMemberInfo(mRxManager, getActivity(), App.getPassengerId(), new PersonUtil.OnGetMember() {
            @Override
            public void onSuccess(Member member) {
                kuaidiAdapter.setExpressList(member.expressDelivery);

                List<Coupon> coupons = member.listCoupon;
                if (null != coupons && coupons.size() != 0) {
                    couponText.setVisibility(View.VISIBLE);
                    couponAdapter.setCoupons(coupons);
                } else {
                    couponText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed() {

            }
        });
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
                if (item.selected) {
                    money += item.totalPrice;
                }
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

        List<Long> longs = new ArrayList<>();
        for (CartItem cartItem : adapter.getList()) {
            if (cartItem.selected) {
                longs.add(cartItem.id);
            }
        }

        if (longs.size() == 0) {
            ToastUtil.showMessage(getActivity(), "请选择至少一项商品");
            return;
        }

        Long[] longArray = new Long[longs.size()];

        Observable<Order> observable = ApiManager.getInstance().api
                .confirmOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId, couponId, longs.toArray(longArray))
                .map(new HttpResultFunc<Order>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Order>() {
            @Override
            public void onNext(final Order o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                expandableLayout.collapse();
                apply.setText("提交订单");
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                onVisible();

//                AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                        .setTitle("温馨提示")
//                        .setMessage("您已下单成功，请支付")
//                        .setPositiveButton("支付宝支付", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                payJishiZfb(o.id, 0);
//                            }
//                        })
//                        .setCancelable(false)
//                        .create();
//                dialog.show();
            }

            @Override
            public void onError(int code) {
                //TODO 下单失败
            }
        })));
    }

    private void yuyueBooking(final long memberId, String receiverName,
                              String receiverPhone, String receiverAddress, long expressId, String date, Long couponId) {
        List<Long> longs = new ArrayList<>();
        for (CartItem cartItem : adapter.getList()) {
            if (cartItem.selected) {
                longs.add(cartItem.id);
            }
        }

        if (longs.size() == 0) {
            ToastUtil.showMessage(getActivity(), "请选择至少一项商品");
            return;
        }

        Long[] longArray = new Long[longs.size()];

        Observable<Order> observable = ApiManager.getInstance().api
                .bespeakOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId, date, couponId, longs.toArray(longArray))
                .map(new HttpResultFunc<Order>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Order>() {
            @Override
            public void onNext(final Order o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                expandableLayout.collapse();
                apply.setText("提交订单");
                onVisible();

                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("温馨提示")
                        .setMessage("您已下单成功，请支付预约金")
                        .setPositiveButton("支付宝支付", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                payYuyueZfb(o.id);
                            }
                        })
                        .setCancelable(false)
                        .create();
                dialog.show();
            }

            @Override
            public void onError(int code) {
                //TODO 下单失败
            }
        })));
    }

    @Override
    public void onMoneyChange(double money) {
        List<CartItem> items = adapter.getList();
        if (items.size() != 0) {

        } else {
            showEmptyView(0);
        }
        totalText.setText("" + money + "元");
        couponAdapter.setOrderMoney(money);
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

    private long expressId = -1;

    private void payYuyueZfb(Long orderId) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payYuyueSingleZfb(orderId)
                .map(new HttpResultFunc<ZfbResult>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(getActivity(), true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    /**
     * @param orderId
     * @param type    0 即时单支付 1预约单支付尾款
     */
    private void payJishiZfb(Long orderId, Integer type) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payJishiSingleZfb(orderId, type)
                .map(new HttpResultFunc<ZfbResult>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(getActivity(), true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    private void detailZfb(String s) {
        PayTask alipay = new PayTask(getActivity());
        String result = alipay
                .pay(s, true);

        Message msg = new Message();
        msg.what = 0;
        msg.obj = result;
        handler.sendMessage(msg);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Context context = getActivity();
                    PayResult result = new PayResult((String) message.obj);
                    if (result.resultStatus.equals("9000")) {
                        Toast.makeText(context, getString(R.string.pay_succeed),
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getActivity(), MyOrderActivity.class));

                    } else {
                        Toast.makeText(context, getString(R.string.pay_fail),
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getActivity(), MyOrderActivity.class));
                    }
                    break;
            }
            return true;
        }
    });
}