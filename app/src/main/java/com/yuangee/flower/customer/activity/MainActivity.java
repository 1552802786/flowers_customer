package com.yuangee.flower.customer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.otto.Subscribe;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.VpAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.AreaResult;
import com.yuangee.flower.customer.entity.CouponEntity;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.HadOpenArea;
import com.yuangee.flower.customer.entity.SystomConfig;
import com.yuangee.flower.customer.fragment.AddAnimateListener;
import com.yuangee.flower.customer.fragment.MineFragment;
import com.yuangee.flower.customer.fragment.ShoppingCartFragment;
import com.yuangee.flower.customer.fragment.ToSpecifiedFragmentListener;
import com.yuangee.flower.customer.fragment.UserServiceFragment;
import com.yuangee.flower.customer.fragment.home.HomeFragment;
import com.yuangee.flower.customer.fragment.reserve.ReserveFragment;
import com.yuangee.flower.customer.fragment.shopping.ShoppingFragment;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.JsonUtil;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.AddCartAnimation;
import com.yuangee.flower.customer.widget.CouponDialog;
import com.yuangee.flower.customer.widget.NoScrollViewPager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.qqtheme.framework.entity.Area;
import okhttp3.MultipartBody;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends RxBaseActivity implements ToSpecifiedFragmentListener, AddAnimateListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private VpAdapter adapter;

    // collections
    private SparseIntArray items;
    private List<Fragment> fragments;

    @BindView(R.id.vp)
    NoScrollViewPager vp;

    @BindView(R.id.bnve)
    BottomNavigationViewEx bnve;

    @BindView(R.id.activity_main)
    RelativeLayout rootView;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private List<HadOpenArea> areas;
    String[] areaNames;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(false);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(10 * 60 * 1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
        queryOpenCity();
        queryCoupon();
    }

    private void queryCoupon() {
        String url = Config.BASE_URL + "rest/activity/findNewRegCoupon";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("memberId", App.getPassengerId() + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject object = JSONObject.parseObject(result);
                List<CouponEntity> entities = JsonUtil.jsonToArray(object.getString("data"), CouponEntity[].class);
                CouponDialog dialog = new CouponDialog(MainActivity.this, R.style.dialogActivity, entities);
                dialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    private void queryOpenCity() {
        String url = Config.BASE_URL + "rest/openArea/listAll";
        RequestParams params = new RequestParams(url);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                areas = JsonUtil.jsonToArray(result, HadOpenArea[].class);
                areaNames = new String[areas.size()];
                for (int i = 0; i < areas.size(); i++) {
                    areaNames[i] = areas.get(i).areaName;
                }
                if (App.me().getMemberInfo().areaId == 0) {
                    showAreaList();
                }
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    //仅仅更新区域id，name
    private void updateMemberInfo(HadOpenArea area) {
        long id = App.getPassengerId();

        MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", String.valueOf(id));
        MultipartBody.Part areaId = MultipartBody.Part.createFormData("areaId", String.valueOf(area.id));
        MultipartBody.Part areaName = MultipartBody.Part.createFormData("areaName", String.valueOf(area.areaName));

        Observable<Object> observable = ApiManager.getInstance().api
                .updateMember(idPart, null, null, null, null, null, areaId, areaName, null)
                .map(new HttpResultFunc<>(MainActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(MainActivity.this, true, false, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {

            }
        })));

    }

    public void showAreaList() {

        new AlertView(null, null, null, null, areaNames,
                MainActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                updateMemberInfo(areas.get(position));
            }
        }).setCancelable(true).show();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initView();
        initData();
        initEvent();
        getSystemConfig();
    }

    @Override
    public void initToolBar() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        vp.setCurrentItem(2);
    }

    /**
     * change BottomNavigationViewEx style
     */
    private void initView() {
        bnve.enableShiftingMode(false);
        bnve.enableAnimation(true);
        bnve.enableItemShiftingMode(false);
    }

    /**
     * create fragments
     */
    HomeFragment homeFragment;
    ShoppingFragment shoppingFragment;
    ShoppingCartFragment shoppingCartFragment;
    ReserveFragment reserveFragment;
    MineFragment mineFragment;
    UserServiceFragment serviceFragment;

    public void setBudge(int number) {
        this.selectedNum = number;
        addBadgeAt(1, this.selectedNum);
    }

    private void initData() {
        fragments = new ArrayList<>(5);
        items = new SparseIntArray(5);

        homeFragment = new HomeFragment();
        homeFragment.setToSpecifiedFragmentListener(this);

        shoppingFragment = new ShoppingFragment();
        shoppingFragment.setToSpecifiedFragmentListener(this);
        shoppingFragment.setAddAnimateListener(this);

        shoppingCartFragment = new ShoppingCartFragment();
        shoppingCartFragment.setToSpecifiedFragmentListener(this);

        reserveFragment = new ReserveFragment();
        reserveFragment.setToSpecifiedFragmentListener(this);
        reserveFragment.setAddAnimateListener(this);

        mineFragment = new MineFragment();
        serviceFragment = new UserServiceFragment();
        // add to fragments for adapter
        fragments.add(homeFragment);
        fragments.add(shoppingCartFragment);
        fragments.add(shoppingFragment);
        fragments.add(mineFragment);
        fragments.add(serviceFragment);


        // add to items for change ViewPager item
        items.put(R.id.i_home, 0);
        items.put(R.id.i_shopping, 1);
        items.put(R.id.i_buy, 2);
        items.put(R.id.i_mine, 3);
        items.put(R.id.i_reserve, 4);


        // set adapter
        vp.setOffscreenPageLimit(5);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(adapter);
    }

    /**
     * set listeners
     */

    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = 0;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = items.get(item.getItemId());
                if (position == 2) {
                    Intent it = new Intent(MainActivity.this, BuyMainActivity.class);
                    startActivity(it);
                    bnve.setCurrentItem(previousPosition);
                } else if (position == 4) {
                    bnve.setCurrentItem(previousPosition);
                    phone = DbHelper.getInstance().getMemberLongDBManager().load(App.getPassengerId()).customServicePhone;
                    String[] strs = new String[]{"客服电话", "在线服务", "售后服务"};
                    new AlertView(null, null, null, null, strs,
                            MainActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            switch (position) {
                                case 0:
                                    if (StringUtils.isNotBlank(phone)) {
                                        PhoneUtil.call(MainActivity.this, phone);
                                    } else {
                                        ToastUtil.showMessage(MainActivity.this, "无效电话号码");
                                    }
                                    break;
                                case 2:
                                    Intent it = new Intent(MainActivity.this, WebActivity.class);
                                    it.putExtra("url", "https://jinshuju.net/f/XuOQKk");
                                    it.putExtra("title", "订单投诉");
                                    startActivity(it);
                                    break;
                            }
                        }
                    }).setCancelable(true).show();

                } else if (previousPosition != position) {
                    // only set item when item changed
                    previousPosition = position;
                    vp.setCurrentItem(position);
                }
                return true;
            }
        });

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private int position;

    /**
     * 0-4代表viewPager中的fragment
     * -1代表显示searchFrame回到购物fragment
     * -2代表显示searchFrame回到预约fragment
     *
     * @param position
     */
    @Override
    public void toFragment(int position) {
        if (position == -1 || position == -2) {
        } else {
            vp.setCurrentItem(position);
        }
    }

    /**
     * 从首页点击类型或者点击推荐跳转到购物fragment
     *
     * @param genre
     * @param genreSub
     * @param params
     */
    @Override
    public void toShoppingByParams(String genre, String genreSub, String params) {
        vp.setCurrentItem(1);
        if (null != shoppingFragment) {
            shoppingFragment.showDrawer();
            shoppingFragment.initDrawer(genre);
        }
    }

    private int selectedNum = 0;

    @Override
    public void showAddAnimate(ImageView startView, int selectedNum) {
        this.selectedNum += selectedNum;
        AddCartAnimation.AddToCart(startView, bnve.getBottomNavigationItemView(2), this, rootView, 1);
        addBadgeAt(1, this.selectedNum);
    }


    @Override
    public void onBackPressed() {
        if (vp.getCurrentItem() == 1) {
            if (shoppingFragment.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    private Badge badge;

    private void addBadgeAt(int position, int number) {
        if (null == badge) {
            badge = new QBadgeView(this)
                    .setBadgeNumber(number)
                    .setGravityOffset(12, 2, true)
                    .bindTarget(bnve.getBottomNavigationItemView(position));
        } else {
            badge.setBadgeNumber(number);
        }
    }

    public static List<Genre> getGenre() {
        return DbHelper.getInstance().getGenreLongDBManager().loadAll();
    }

    private SystomConfig config;

    public SystomConfig getConfig() {
        return config;
    }

    private void getSystemConfig() {
        Observable<SystomConfig> observable = ApiManager.getInstance()
                .api.systemConfig()
                .map(new HttpResultFunc<SystomConfig>(MainActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, new NoErrSubscriberListener<SystomConfig>() {
            @Override
            public void onNext(SystomConfig o) {
                config = o;
            }
        })));
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Observable<AreaResult> observable = ApiManager.getInstance().api
                    .queryOpenCity(location.getCity())
                    .map(new HttpResultFunc<AreaResult>(MainActivity.this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            mRxManager.add(observable.subscribe(new MySubscriber<>(MainActivity.this, false, true, new HaveErrSubscriberListener<AreaResult>() {
                @Override
                public void onNext(AreaResult areaResult) {

                }

                @Override
                public void onError(int code) {

                    new AlertView("提示", "当前城市未开通云购服务\n您可选择其他地区进行查看",
                            null, new String[]{"确定"}, null, MainActivity.this,
                            AlertView.Style.Alert, null).show();
                }
            })));

        }
    }
}
