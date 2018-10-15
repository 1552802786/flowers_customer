package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.WaresNumber;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.BottomView;
import com.yuangee.flower.customer.widget.ExpandableHeightListView;
import com.yuangee.flower.customer.widget.wheelView.TextChooseAdapter;
import com.yuangee.flower.customer.widget.wheelView.WheelView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 添加本地订单  减少库存
 * Created by admin on 2018/1/20.
 */

public class CustomerAddLocalOrderActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.parent_type)
    TextView parentType;

    @BindView(R.id.child_type)
    TextView childType;

    @BindView(R.id.sub_number)
    EditText subNumber;

    @BindView(R.id.product_name)
    TextView productName;

    @BindView(R.id.user_name_txt)
    EditText userName;

    @BindView(R.id.user_phone_txt)
    EditText userPhone;

    @BindView(R.id.confirm_list)
    ExpandableHeightListView confirmList;
    private BottomView bottomView;
    private WheelView wheelView;
    private TextChooseAdapter childAdapter;
    private TextChooseAdapter parentAdapter;
    private TextChooseAdapter productAdapter;
    private int showFlag = -1;
    private ConfirmAdapter confirmAdapter;
    private String confirmString;
    private long shopId;
    @BindView(R.id.search_local)
    EditText search;
    private AlertDialog dialog;

    @OnClick(R.id.add_btn_icon)
    void addOneInfo() {
        if (!TextUtils.isEmpty(subNumber.getText().toString())) {
            confirmString = parentType.getText() + "-" + childType.getText() + "-" + productName.getText() + "-" + subNumber.getText();
            for (Goods g : goodsList) {
                if (g.genreName.equalsIgnoreCase(parentType.getText().toString())
                        && g.genreSubName.equalsIgnoreCase(childType.getText().toString())
                        && (g.name + "(" + g.color + "/¥" + g.unitPrice + "/" + g.salesVolume + ")").equalsIgnoreCase(productName.getText().toString())) {

                    WaresNumber number = new WaresNumber();
                    number.waresId = g.id;
                    number.quantity = Integer.parseInt(subNumber.getText().toString().trim());
                    if (g.salesVolume >= number.quantity) {
                        waresNumbers.add(number);
                        addString.add(confirmString);
                    } else {
                        ToastUtil.showMessage(this, "商品可售量不足" + number.quantity);
                    }
                    break;
                }
            }
        }
        confirmAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.parent_type)
    void chooseParentType() {
        showFlag = 0;
        if (parentAdapter == null) {
            parentAdapter = new TextChooseAdapter(generNameList);
        }
        showDailog(parentAdapter);
    }

    @OnClick(R.id.child_type)
    void chooseChildType() {
        showFlag = 1;
        if (childAdapter == null) {
            childAdapter = new TextChooseAdapter(generSubNameList);
        }
        showDailog(childAdapter);
    }

    @OnClick(R.id.product_name)
    void chooseProductName() {
        showFlag = 2;
        if (productAdapter == null) {
            productAdapter = new TextChooseAdapter(nameList);
        }
        showDailog(productAdapter);
    }

    @OnClick(R.id.search_btn)
    void searchLocalGood() {
        RadioButton button = new RadioButton(this);
        ScrollView scrollView = new ScrollView(this);
        final RadioGroup group = new RadioGroup(this);
        scrollView.addView(group);
        for (Goods g : goodsList) {
            if (g.name.contains(search.getText().toString().trim())) {
                button.setText(g.genreName + "-" + g.genreSubName + "-" + g.name + "(" + g.color + "/¥" + g.unitPrice + "/" + g.salesVolume + ")");
                group.addView(button);
            }
        }
        dialog = new AlertDialog.Builder(this)
                .setTitle("选择商品")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        for (int i = 0; i < group.getChildCount(); i++) {
                            RadioButton button = (RadioButton) group.getChildAt(i);
                            if (button.isChecked()) {
                                String[] str = button.getText().toString().split("-");
                                parentType.setText(str[0]);
                                childType.setText(str[1]);
                                productName.setText(str[2]);
                            }
                        }
                    }
                }).setView(scrollView).create();
        dialog.show();
    }

    List<WaresNumber> waresNumbers = new ArrayList<>();
    List<String> addString = new ArrayList<>();

    @OnClick(R.id.add_order_btn)
    void addLocalOrder() {
        if (waresNumbers.size() < 1) {
            ToastUtil.showMessage(mContext, "请至少添加一个商品");
        }
        orderIng(waresNumbers);
    }

    private void orderIng(List<WaresNumber> waresNumbers) {

        Gson gson = new Gson();
        Member member = DbHelper.getInstance().getMemberLongDBManager().load(App.getPassengerId());
        Observable<Object> observable = ApiManager.getInstance().api
                .cusOrder(userName.getText().toString(), userPhone.getText().toString(), member.id, gson.toJson(waresNumbers), shopId)
                .map(new HttpResultFunc<>(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(mContext, "创建成功");
                finish();
            }
        })));
    }

    private List<Goods> goodsList = new ArrayList<>();
    private Context mContext;
    private List<String> generNameList = new ArrayList<>();
    private List<String> generSubNameList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Map<String, String>> recorde = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_local_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        shopId = getIntent().getLongExtra("shopId", -1);
        getGoodsData();
        mContext = this;
        confirmAdapter = new ConfirmAdapter();
        confirmList.setAdapter(confirmAdapter);
    }

    private void updateSubData(String parentType) {
        generSubNameList.clear();
        for (Goods good : goodsList) {
            if (good.genreName.equalsIgnoreCase(parentType)) {
                if (!generSubNameList.contains(good.genreSubName)) {
                    generSubNameList.add(good.genreSubName);
                }
            }
        }
        updateNameData(generSubNameList.get(0));
    }

    private void updateNameData(String childtype) {
        nameList.clear();
        for (Map<String, String> map : recorde) {
            Iterator iter = map.entrySet().iterator();
            Map.Entry entry = (Map.Entry) iter.next();
            if (childtype.equalsIgnoreCase((String) entry.getKey())) {
                nameList.add((String) entry.getValue());
            }
        }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("添加订单记录");
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private String choosedString;

    public void showDailog(final TextChooseAdapter adapter) {
        bottomView = new BottomView(this,
                R.style.BottomViewTheme_Defalut, R.layout.bottom_view);
        View view = bottomView.getView();
//        bottomView.setAnimation(R.style.BottomToTopAnim);//设置动画，可选
        view.findViewById(R.id.bottomview_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView.dismissBottomView();
            }
        });
        view.findViewById(R.id.bottomview_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosedString = adapter.getItem(wheelView.getCurrentItem());
                switch (showFlag) {
                    case 0:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateSubData(choosedString);
                                parentType.setText(choosedString);
                                childType.setText(generSubNameList.get(0));
                                productName.setText(nameList.get(0));
                            }
                        });

                        break;
                    case 1:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateNameData(choosedString);
                                childType.setText(choosedString);
                                productName.setText(nameList.get(0));
                            }
                        });

                        break;
                    case 2:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                productName.setText(choosedString);
                            }
                        });
                        break;
                }
                bottomView.dismissBottomView();
            }
        });
        wheelView = view.findViewById(R.id.wheel_view);
        wheelView.setCyclic(false);
        wheelView.setAdapter(adapter);
        bottomView.showBottomView(true);
    }

    private class ConfirmAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return addString.size();
        }

        @Override
        public Object getItem(int i) {
            return addString.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Holder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.add_local_order_item, null);
                holder = new Holder(view);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.info.setText(addString.get(i));
            holder.subBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addString.remove(i);
                    waresNumbers.remove(i);
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }

    class Holder {
        TextView info;
        ImageView subBtn;

        public Holder(View view) {
            info = view.findViewById(R.id.confirm_info_txt);
            subBtn = view.findViewById(R.id.sub_btn_icon);
        }
    }

    private void getGoodsData() {
        Observable<PageResult<Goods>> observable = ApiManager.getInstance().api
                .findWares(shopId, 0, null, 999)
                .map(new HttpResultFunc<PageResult<Goods>>(CustomerAddLocalOrderActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, new HaveErrSubscriberListener<PageResult<Goods>>() {
            @Override
            public void onNext(PageResult<Goods> pageResult) {
                goodsList.addAll(pageResult.rows);
                for (Goods good : goodsList) {
                    Map<String, String> map = new HashMap<>();
                    map.put(good.genreSubName, good.name + "(" + good.color + "/¥" + good.unitPrice + "/" + good.salesVolume + ")");
                    recorde.add(map);
                    if (!generNameList.contains(good.genreName)) {
                        generNameList.add(good.genreName);
                    }
                }
                updateSubData(generNameList.get(0));
                parentType.setText(goodsList.get(0).genreName);
                childType.setText(goodsList.get(0).genreSubName);
                productName.setText(nameList.get(0));
            }

            @Override
            public void onError(int code) {
                goodsList.clear();
            }
        })));
    }
}
