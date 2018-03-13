package com.yuangee.flower.customer.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.baidu.mapapi.search.core.PoiInfo;
import com.google.gson.JsonElement;
import com.squareup.otto.Subscribe;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.AppBus;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.CusOrderDetailActivity;
import com.yuangee.flower.customer.activity.CustomerOrderPayActivity;
import com.yuangee.flower.customer.activity.CustomerPlaceSearchActivity;
import com.yuangee.flower.customer.activity.EditAddressActivity;
import com.yuangee.flower.customer.activity.MainActivity;
import com.yuangee.flower.customer.activity.CustomerOrderActivity;
import com.yuangee.flower.customer.activity.PersonalCenterActivity;
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
import com.yuangee.flower.customer.entity.CustomerOrder;
import com.yuangee.flower.customer.entity.PayResult;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.fragment.shopping.FeeCreatOrderResult;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.picker.AddressInitTask;
import com.yuangee.flower.customer.result.QueryCartResult;
import com.yuangee.flower.customer.util.PersonUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private Member member;
    private List<CartItem> lsItems;


    @OnClick(R.id.apply)
    void apply() {
        if (!expandableLayout.isExpanded()) {
            expandableLayout.expand();
            apply.setText("确认订单");
            getAllFee();
        } else {
            final Coupon coupon = couponAdapter.getClicked();
            if (address == null) {
                ToastUtil.showMessage(getActivity(), "请选择一个收货地址");
            } else if (expressId == -1) {
                ToastUtil.showMessage(getActivity(), "请选择一个快递方式");
            } else {
                updateMemberAddress();
                updateMemberInfo();
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
                , LinearLayoutManager.VERTICAL, false));
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
    EditText shouhuoName;

    @BindView(R.id.shouhuo_phone)
    EditText shouhuoPhone;

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

    @BindView(R.id.shouhuo_addr_detail)
    TextView shouhuo_addr_detail;

    @BindView(R.id.yuyue_time)
    TextView yuyueTime;
    @BindView(R.id.shouhuo_user_message)
    EditText userMessage;

    @BindView(R.id.peihuo_fee)
    TextView peihuoFee;

    @BindView(R.id.baozhuang_fee)
    TextView baozhuangFee;

    @BindView(R.id.shouxu_fee)
    TextView shouxuFei;

    @BindView(R.id.yun_fee)
    TextView yunFei;
    @BindView(R.id.fee_layout)
    ExpandableLayout expanFeeLayout;
    @BindView(R.id.shouhuo_shop_address)
    EditText shouhuo_shop_address;

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
                startDate = System.currentTimeMillis() + destineTime.start * 24L * 60L * 60L * 1000L;
                endDate = System.currentTimeMillis() + destineTime.end * 24L * 60L * 60L * 1000L;
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
        picker.setResetWhileWheel(true);
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

    @OnClick(R.id.show_fee_info)
    void showFeeInfo() {
        if (!expanFeeLayout.isExpanded()) {
            expanFeeLayout.expand();
        }
    }

    @OnClick(R.id.hide_fee_info)
    void hideFeeInfo() {
        if (expanFeeLayout.isExpanded()) {
            expanFeeLayout.collapse();
        }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppBus.getInstance().register(this);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_shopping_cart;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        yuyueTimeCon.setVisibility(View.GONE);
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

        member = App.me().getMemberInfo();
        if (null != member.memberAddressList && member.memberAddressList.size() != 0) {
            address = member.memberAddressList.get(0);
//            for (Address addr : member.memberAddressList) {
//                address = addr;
//                if (address.defaultAddress) {
//                    break;
//                }
//            }
        }
        setShippingMsg();
//        shouhuoName.setOnClickListener(shippingOnClick);
//        shouhuoPhone.setOnClickListener(shippingOnClick);
        shouhuoAddr.setOnClickListener(shippingOnClick);
        shouhuo_addr_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), CustomerPlaceSearchActivity.class);
                it.putExtra("city", address.getCity());
                startActivityForResult(it, 10065);
            }
        });
    }

    View.OnClickListener shippingOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showPickerPlace(shouhuoAddr);
        }
    };

    private void setShippingMsg() {
        expressId = member.expressDeliveryId;
        if (address != null) {
            shouhuoAddr.setText(address.getPro() + address.getCity() + address.getArea());
            shouhuo_addr_detail.setText(address.getStreet());
            shouhuoName.setText(address.getShippingName());
            shouhuoPhone.setText(address.getShippingPhone());
        } else {
            shouhuoAddr.setHint("请选择收货地址");
            address = new Address();
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
                kuaidiAdapter.setExpressList(member.expressDelivery, member.expressDeliveryId);

//                List<Coupon> coupons = member.listCoupon;
//                if (null != coupons && coupons.size() != 0) {
//                    couponText.setVisibility(View.VISIBLE);
//                    couponAdapter.setCoupons(coupons);
//                } else {
//                    couponText.setVisibility(View.GONE);
//                }
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

    private double money = 0.0;

    private void showList(boolean bespeak) {
        lsItems = new ArrayList<>();
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
//        if (poiInfo == null) {
//            ToastUtil.showMessage(getActivity(), "具体位置不对，请从新选择");
//            return;
//        }
        if (longs.size() == 0) {
            ToastUtil.showMessage(getActivity(), "请选择至少一项商品");
            return;
        }

        Long[] longArray = new Long[longs.size()];

        Observable<CustomerOrder> observable = ApiManager.getInstance().api
                .confirmOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId, couponId, longs.toArray(longArray), "", address.latitude, address.longitude)
                .map(new HttpResultFunc<CustomerOrder>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<CustomerOrder>() {
            @Override
            public void onNext(final CustomerOrder o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                expandableLayout.collapse();
                apply.setText("提交订单");

                Intent it = new Intent(getActivity(), CustomerOrderPayActivity.class);
                it.putExtra("order", o);
                it.putExtra("shoppingCart", true);
                startActivity(it);
                onVisible();
            }

            @Override
            public void onError(int code) {

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

        Observable<CustomerOrder> observable = ApiManager.getInstance().api
                .bespeakOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId, date, couponId, longs.toArray(longArray))
                .map(new HttpResultFunc<CustomerOrder>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<CustomerOrder>() {
            @Override
            public void onNext(final CustomerOrder o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                expandableLayout.collapse();
                apply.setText("提交订单");
                onVisible();

                Intent it = new Intent(getActivity(), CustomerOrderPayActivity.class);
                it.putExtra("order", o);
                it.putExtra("shoppingCart", true);
                startActivity(it);
            }

            @Override
            public void onError(int code) {
                //TODO 下单失败
            }
        })));
    }

    private void getAllFee() {
        List<Long> longs = new ArrayList<>();
        for (CartItem cartItem : adapter.getList()) {
            if (cartItem.selected) {
                longs.add(cartItem.id);
            }
        }
        if (longs.size() == 0) {
            apply.setEnabled(false);
            apply.setBackgroundColor(Color.parseColor("#c2c1c1"));
            return;
        }
        String addressStr = shouhuoAddr.getText().toString() + shouhuo_addr_detail.getText().toString();
        if (TextUtils.isEmpty(address.shippingName) || TextUtils.isEmpty(address.shippingPhone) || TextUtils.isEmpty(shouhuoAddr.getText().toString().trim())
                || TextUtils.isEmpty(shouhuo_addr_detail.getText().toString().trim()) || (address.latitude == 0) || (address.longitude == 0)) {
            ToastUtil.showMessage(getActivity(), "请完善收货地址");
            return;
        }

        Long[] longArray = new Long[longs.size()];
        Observable<FeeCreatOrderResult> observable = ApiManager.getInstance().api
                .getAllfeeCreatOrder(longs.toArray(longArray), expressId, address.shippingName, address.shippingPhone, addressStr, address.latitude, address.longitude)
                .map(new HttpResultFunc<FeeCreatOrderResult>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<FeeCreatOrderResult>() {

            @Override
            public void onNext(FeeCreatOrderResult result) {
                apply.setEnabled(true);
                apply.setBackgroundColor(Color.RED);
                BigDecimal decimal = new BigDecimal(result.totalPrice);

                baozhuangFee.setText("¥ " + new BigDecimal(result.packagePrice).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                yunFei.setText("¥ " + new BigDecimal(result.expressDelivery).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                shouxuFei.setText("¥ " + decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                peihuoFee.setText("¥ " + new BigDecimal(result.preparePrice).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (couponAdapter.getClicked() != null) {
                    BigDecimal de = new BigDecimal((Double.parseDouble(result.summation) - couponAdapter.getClicked().getMoney()));
                    totalText.setText(de.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "元");
                } else {
                    totalText.setText(new BigDecimal(result.summation).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "元");
                }

            }

            @Override
            public void onError(int code) {
                int count = 0;
                for (CartItem cartItem : adapter.getList()) {
                    if (cartItem.selected) {
                        count++;
                    }
                }
                if (count > 0) {
                    apply.setEnabled(true);
                    apply.setBackgroundColor(Color.RED);
                } else {
                    apply.setEnabled(false);
                    apply.setBackgroundColor(Color.parseColor("#c2c1c1"));
                }
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

    PoiInfo poiInfo;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDR) {
            if (resultCode == RESULT_OK) {
                address = (Address) data.getSerializableExtra("address");
                setShippingMsg();
            }
        }
        if (requestCode == 10065 && resultCode == RESULT_OK) {
            poiInfo = data.getParcelableExtra("result");
            address.longitude = poiInfo.location.longitude;
            address.latitude = poiInfo.location.latitude;
            address.street = poiInfo.name;
            shouhuo_addr_detail.setText(poiInfo.name);
            getAllFee();
        }
    }

    private long expressId = -1;


    public void showPickerPlace(final TextView tv) {
        new AddressInitTask(getActivity(), new AddressInitTask.InitCallback() {
            @Override
            public void onDataInitFailure() {
                ToastUtil.showMessage(getActivity(), "数据初始化失败");
            }

            @Override
            public void onDataInitSuccess(ArrayList<Province> provinces) {
                AddressPicker picker = new AddressPicker(getActivity(), provinces);
                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        String provinceName = province.getName();
                        String cityName = "";
                        if (city != null) {
                            cityName = city.getName();
                            //忽略直辖市的二级名称
                            if (cityName.equals("市辖区") || cityName.equals("市") || cityName.equals("县")) {
                                cityName = "";
                            }
                        }
                        String countyName = "";
                        if (county != null) {
                            countyName = county.getName();
                        }
                        address.pro = provinceName;
                        address.city = cityName;
                        address.area = countyName;
                        tv.setText(provinceName + cityName + countyName);
                    }
                });
                picker.show();
            }
        }).execute();
    }

    private void updateMemberAddress() {
        address.shippingName = shouhuoName.getText().toString();
        address.shippingPhone = shouhuoPhone.getText().toString();
        address.street = shouhuo_addr_detail.getText().toString() + shouhuo_shop_address.getText().toString();
        if (StringUtils.isBlank(address.shippingName)
                || StringUtils.isBlank(address.shippingPhone) || StringUtils.isBlank(address.street)
                || StringUtils.isBlank(address.pro)) {
            ToastUtil.showMessage(getActivity(), "请将信息填写完整");
            return;
        }
        Observable<Object> observable = ApiManager.getInstance().api
                .updateMemberAddress(address.id, App.getPassengerId(), address.shippingName, address.shippingPhone,
                        address.pro, address.city, address.area, address.street, address.latitude, address.longitude, true)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object expresses) {


            }
        })));
    }

    //仅仅更新收货方式
    private void updateMemberInfo() {
        long id = App.getPassengerId();

        MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", String.valueOf(id));
        MultipartBody.Part deliverPart = MultipartBody.Part.createFormData("expressDeliveryId", String.valueOf(kuaidiAdapter.getClicked().id));
        MultipartBody.Part photoPart = null;

        Observable<Object> observable = ApiManager.getInstance().api
                .updateMember(idPart, null, null, null, null, photoPart, deliverPart)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, false, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {

            }
        })));

    }

    @Subscribe
    public void updatePriceTotal(String change) {
        int count = 0;
        money = 0;
        for (CartItem cartItem : adapter.getList()) {
            if (cartItem.selected) {
                count++;
                money += cartItem.totalPrice;
            }
        }
        totalText.setText(money + "元");
        if (count > 0) {
            apply.setEnabled(true);
            apply.setBackgroundColor(Color.RED);
        } else {
            apply.setEnabled(false);
            apply.setBackgroundColor(Color.parseColor("#c2c1c1"));
        }
    }

    @Subscribe
    public void couponChoosed(double couponPrice) {
        double price = Double.parseDouble(totalText.getText().toString().trim()) + couponPrice;
        totalText.setText(price + "元");
    }

    @Subscribe
    public void expressChoosed(Express express) {
        expressId = express.id;
        getAllFee();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppBus.getInstance().unregister(this);
    }
}