package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bigkoo.alertview.AlertView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.AreaResult;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.HadOpenArea;
import com.yuangee.flower.customer.entity.SystomConfig;
import com.yuangee.flower.customer.fragment.AddAnimateListener;
import com.yuangee.flower.customer.fragment.MineFragment;
import com.yuangee.flower.customer.fragment.ShoppingCartFragment;
import com.yuangee.flower.customer.fragment.ToSpecifiedFragmentListener;
import com.yuangee.flower.customer.fragment.home.HomeFragment;
import com.yuangee.flower.customer.fragment.reserve.ReserveFragment;
import com.yuangee.flower.customer.fragment.shopping.ShoppingFragment;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.widget.AddCartAnimation;
import com.yuangee.flower.customer.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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
//        queryOpenCity();
    }
    private void queryOpenCity(){
        Observable<PageResult<HadOpenArea>> observable = ApiManager.getInstance().api
                .searchOpenArea()
                .map(new HttpResultFunc<PageResult<HadOpenArea>>(MainActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(MainActivity.this, false, true, new HaveErrSubscriberListener<PageResult<HadOpenArea>>() {
            @Override
            public void onNext(PageResult areaResult) {
                    Log.e("TAG",areaResult.rows.size()+"----");
            }

            @Override
            public void onError(int code) {
                new AlertView("提示", "当前城市未开通云购服务\n您可选择其他地区进行查看",
                        null, new String[]{"确定"}, null, MainActivity.this,
                        AlertView.Style.Alert, null).show();
            }
        })));


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

    public void setBudge(int number) {
        this.selectedNum = number;
        addBadgeAt(2, this.selectedNum);
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

        // add to fragments for adapter
        fragments.add(homeFragment);
        fragments.add(shoppingFragment);
        fragments.add(shoppingCartFragment);
        fragments.add(reserveFragment);
        fragments.add(mineFragment);

        // add to items for change ViewPager item
        items.put(R.id.i_home, 0);
        items.put(R.id.i_buy, 1);
        items.put(R.id.i_shopping, 2);
        items.put(R.id.i_reserve, 3);
        items.put(R.id.i_mine, 4);

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
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = items.get(item.getItemId());
                if (previousPosition != position) {
                    // only set item when item changed
                    previousPosition = position;
                    Log.i(TAG, "-----bnve-------- previous item:" + bnve.getCurrentItem() + " current item:" + position + " ------------------");
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
                Log.i(TAG, "-----ViewPager-------- previous item:" + bnve.getCurrentItem() + " current item:" + position + " ------------------");
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
        addBadgeAt(2, this.selectedNum);
    }

    /**
     * view pager adapter
     */
    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
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
//            Observable<AreaResult> observable = ApiManager.getInstance().api
//                    .queryOpenCity(location.getCity())
//                    .map(new HttpResultFunc<AreaResult>(MainActivity.this))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread());
//            mRxManager.add(observable.subscribe(new MySubscriber<>(MainActivity.this, false, true, new HaveErrSubscriberListener<AreaResult>() {
//                @Override
//                public void onNext(AreaResult areaResult) {
//
//                }
//
//                @Override
//                public void onError(int code) {
//                    new AlertView("提示", "当前城市未开通云购服务\n您可选择其他地区进行查看",
//                            null, new String[]{"确定"}, null, MainActivity.this,
//                            AlertView.Style.Alert, null).show();
//                }
//            })));

        }
    }
}
