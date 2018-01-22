package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tandong.bottomview.view.BottomView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.WaresNumber;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.wheelView.OnItemSelectedListener;
import com.yuangee.flower.customer.widget.wheelView.TextChooseAdapter;
import com.yuangee.flower.customer.widget.wheelView.WheelView;

import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.confirm_info_layout)
    RelativeLayout confirmLayout;

    @BindView(R.id.confirm_info_txt)
    TextView confirmInfoText;

    @BindView(R.id.user_name_txt)
    EditText userName;

    @BindView(R.id.user_phone_txt)
    EditText userPhone;
    private BottomView bottomView;
    private WheelView wheelView;
    private TextChooseAdapter childAdapter;
    private TextChooseAdapter parentAdapter;
    private TextChooseAdapter productAdapter;
    private int showFlag = -1;

    @OnClick(R.id.add_btn_icon)
    void addOneInfo() {
        if (!TextUtils.isEmpty(subNumber.getText().toString())) {
            confirmInfoText.setText(parentType.getText() + "-" + childType.getText() + "-" + productName.getText() + "-" + subNumber.getText());
            confirmLayout.setVisibility(View.VISIBLE);
            subNumber.setEnabled(false);
        }
    }

    @OnClick(R.id.sub_btn_icon)
    void subOneInfo() {
        subNumber.setEnabled(true);
        confirmInfoText.setText("");
        confirmLayout.setVisibility(View.GONE);
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

    @OnClick(R.id.add_order_btn)
    void addLocalOrder() {
        addCheck();
        for (Goods g : goodsList) {
            if (g.genreName.equalsIgnoreCase(parentType.getText().toString())
                    && g.genreSubName.equalsIgnoreCase(childType.getText().toString())
                    &&g.name.equalsIgnoreCase(productName.getText().toString())){
                orderIng(g.id,Integer.valueOf(subNumber.getText().toString()));
                break;
            }
        }
    }

    private void orderIng(long waresId, int num) {

        WaresNumber number = new WaresNumber();
        number.waresId = waresId;
        number.quantity = num;
        Gson gson = new Gson();

        List<WaresNumber> waresNumbers = new ArrayList<>();
        waresNumbers.add(number);

        Member member = DbHelper.getInstance().getMemberLongDBManager().load(App.getPassengerId());
        Observable<Object> observable = ApiManager.getInstance().api
                .cusOrder(member.name, member.phone, member.id, gson.toJson(waresNumbers), getIntent().getLongExtra("shopId", -1))
                .map(new HttpResultFunc<>(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(mContext, "创建成功");
            }
        })));
    }

    private List<Goods> goodsList;
    private Context mContext;
    private List<String> generNameList = new ArrayList<>();
    private List<String> generSubNameList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_local_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        goodsList = (List<Goods>) getIntent().getSerializableExtra("goods");
        for (Goods good : goodsList) {
            generNameList.add(good.genreName);
            updateSubData(generNameList.get(0));
        }
        parentType.setText(goodsList.get(0).genreName);
        childType.setText(goodsList.get(0).genreSubName);
        productName.setText(goodsList.get(0).name);
        mContext = this;
    }

    private void updateSubData(String parentType) {
        generSubNameList.clear();
        nameList.clear();
        for (Goods good : goodsList) {
            if (good.genreName.equalsIgnoreCase(parentType)) {
                generSubNameList.add(good.genreSubName);
                nameList.add(good.name);
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

    public void showDailog(TextChooseAdapter adapter) {
        bottomView = new BottomView(this,
                R.style.BottomViewTheme_Defalut, R.layout.bottom_view);
        View view = bottomView.getView();
        bottomView.setAnimation(R.style.BottomToTopAnim);//设置动画，可选
        view.findViewById(R.id.bottomview_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView.dismissBottomView();
            }
        });
        view.findViewById(R.id.bottomview_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                childType.setText(choosedString);
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
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                choosedString = (String) wheelView.getAdapter().getItem(index);
            }
        });
        bottomView.showBottomView(true);
    }

    private void addCheck() {
        if (TextUtils.isEmpty(confirmInfoText.getText().toString())) {
            ToastUtil.showMessage(mContext, "请确认商品信息");
            return;
        }
        if (TextUtils.isEmpty(userName.getText().toString())) {
            ToastUtil.showMessage(mContext, "客户名称必须输入");
            return;
        }
        if (TextUtils.isEmpty(userPhone.getText().toString())) {
            ToastUtil.showMessage(mContext, "客户电话必须输入");
            return;
        }
    }
}
