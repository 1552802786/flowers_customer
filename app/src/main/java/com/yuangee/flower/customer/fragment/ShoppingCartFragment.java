package com.yuangee.flower.customer.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yuangee.flower.customer.activity.MyOrderActivity;
import com.yuangee.flower.customer.activity.SecAddressActivity;
import com.yuangee.flower.customer.adapter.ShoppingCartAdapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.CartItem;
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

        if (address == null) {
            ToastUtil.showMessage(getActivity(), "请选择一个收货地址");
        } else if (expressId == -1) {
            ToastUtil.showMessage(getActivity(), "请选择一个快递方式");
        } else {
            if (jishiGou.isChecked()) {
                booking(App.getPassengerId(), address.shippingName, address.shippingPhone,
                        address.pro + address.city + address.area + address.street, expressId);
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
                                i + "-" + i1 + "-" + i2 + " " + "00:00");
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        }

    }

    private long checkId;

    @OnClick(R.id.express)
    void showExpressDialog() {
        radioGroup = new RadioGroup(getActivity());
        radioGroup.setPadding(DisplayUtil.dp2px(getActivity(), 20), DisplayUtil.dp2px(getActivity(), 10), 0, 0);
        checkId = expressId;
        if (null != expressList && expressList.size() != 0) {
            for (final Express express : expressList) {
                RadioButton radioButton = new RadioButton(getActivity());
                radioButton.setText(express.expressDeliveryName + "(" + express.expressDeliveryMoney + "元起)");
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            checkId = express.id;
                        }
                    }
                });
                if (expressId == express.id) {
                    radioButton.setChecked(true);
                }
                radioGroup.addView(radioButton);
            }
        }
        dialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Express express : expressList) {
                            if (checkId == express.id) {
                                expressId = express.id;
                                expressName = express.expressDeliveryName;
                            }
                        }
                        if (expressId != -1) {
                            expressTxt.setText("快递：" + expressName);
                            dialog.dismiss();
                        } else {
                            ToastUtil.showMessage(getActivity(), "请选择一个快递方式");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("快递")
                .setView(radioGroup)
                .create();
        dialog.show();
    }

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyLayout;

    @BindView(R.id.content)
    LinearLayout content;

    @BindView(R.id.receive_place)
    TextView receivePlace;

    @BindView(R.id.express)
    TextView expressTxt;

    @BindView(R.id.jishi_gou)
    RadioButton jishiGou;

    @BindView(R.id.yuyue_gou)
    RadioButton yuyueGou;

    private ShoppingCartAdapter adapter;

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

        Member member = App.me().getMemberInfo();
        if (null != member.memberAddressList && member.memberAddressList.size() != 0) {
            for (Address addr : member.memberAddressList) {
                address = addr;
                if (address.defaultAddress) {
                    break;
                }
            }
            receivePlace.setText("收货地址：" + address.getStreet());
        } else {
            receivePlace.setText("添加收货地址");
        }
        receivePlace.setOnClickListener(new View.OnClickListener() {
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
        });
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
        for (CartItem item : items) {
            if (item.bespeak == bespeak) {//预约购
                lsItems.add(item);
                money += item.totalPrice;
            }
        }
        totalText.setText(money + "元");
        adapter.setData(lsItems);
        if (lsItems.size() == 0) {
            showEmptyView(0);
        } else {
            hideEmpty();
        }
    }

    private void booking(long memberId, String receiverName,
                         String receiverPhone, String receiverAddress, long expressId) {
        Observable<Object> observable = ApiManager.getInstance().api
                .confirmOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
            }

            @Override
            public void onError(int code) {
                //TODO 下单失败
            }
        })));
    }

    private void yuyueBooking(long memberId, String receiverName,
                              String receiverPhone, String receiverAddress, long expressId, String date) {
        Observable<Object> observable = ApiManager.getInstance().api
                .bespeakOrderMulti(memberId, receiverName, receiverPhone, receiverAddress, expressId, date)
                .map(new HttpResultFunc<>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(getActivity(), "下单成功");
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDR) {
            if (resultCode == RESULT_OK) {
                address = (Address) data.getSerializableExtra("address");
                if (address != null) {
                    receivePlace.setText("收货地址：" + address.getStreet());
                } else {
                    receivePlace.setText("添加收货地址");
                }
            }
        }
    }

    private List<Express> expressList;

    /**
     * 查询所有快递
     */
    private void findByExpressDeliveryAll() {
        Observable<List<Express>> observable = ApiManager.getInstance().api
                .findByExpressDeliveryAll()
                .map(new HttpResultFunc<List<Express>>(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new NoErrSubscriberListener<List<Express>>() {
            @Override
            public void onNext(List<Express> expresses) {
                expressList = expresses;

            }
        })));
    }

    private RadioGroup radioGroup;
    private long expressId = -1;
    private String expressName = "";
    private AlertDialog dialog;
}
