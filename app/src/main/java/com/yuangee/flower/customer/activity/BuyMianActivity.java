package com.yuangee.flower.customer.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.VpAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.fragment.FenleiFragment;
import com.yuangee.flower.customer.fragment.JinPaiFragment;
import com.yuangee.flower.customer.fragment.MineFragment;
import com.yuangee.flower.customer.fragment.ShoppingCartFragment;
import com.yuangee.flower.customer.fragment.UserServiceFragment;
import com.yuangee.flower.customer.fragment.home.HomeFragment;
import com.yuangee.flower.customer.fragment.reserve.ReserveFragment;
import com.yuangee.flower.customer.fragment.shopping.ShoppingFragment;
import com.yuangee.flower.customer.widget.HomeItemView;
import com.yuangee.flower.customer.widget.NoScrollViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/**
 * 作者：Rookie on 2018/10/8 11:12
 */
public class BuyMianActivity extends RxBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private VpAdapter adapter;

    // collections
    private SparseIntArray items;
    private List<Fragment> fragments;

    @BindView(R.id.vp)
    NoScrollViewPager vp;

    @BindView(R.id.bnve_buy)
    PageNavigationView bnve;

    @BindView(R.id.activity_main)
    RelativeLayout rootView;
    private HomeFragment homeFragment;
    private ShoppingFragment shoppingFragment;
    private ShoppingCartFragment shoppingCartFragment;
    private ReserveFragment reserveFragment;
    private FenleiFragment fenleiFragment;
    private NavigationController navigationController;

    @Override
    public int getLayoutId() {
        return R.layout.activity_buy_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initData();
    }

    private void initData() {
        fragments = new ArrayList<>(7);
        items = new SparseIntArray(7);

        homeFragment = new HomeFragment();

        shoppingFragment = new ShoppingFragment();

        shoppingCartFragment = new ShoppingCartFragment();

        reserveFragment = new ReserveFragment();
        fenleiFragment = new FenleiFragment();
        // add to fragments for adapter
        fragments.add(homeFragment);
        fragments.add(shoppingFragment);
        fragments.add(new UserServiceFragment());
        fragments.add(fenleiFragment);
        fragments.add(reserveFragment);
        fragments.add(new JinPaiFragment());
        fragments.add(shoppingCartFragment);


        // add to items for change ViewPager item
        items.put(R.id.i_home, 0);
        items.put(R.id.buy_shopping, 1);
        items.put(R.id.buy_big, 2);
        items.put(R.id.buy_fenlei, 3);
        items.put(R.id.buy_yuding, 4);
        items.put(R.id.buy_jingpai, 5);
        items.put(R.id.buy_shoppingcart, 6);


        // set adapter
        vp.setOffscreenPageLimit(7);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(adapter);
        // set listener to change the current item of view pager when click bottom nav item
        navigationController = bnve.custom()
                .addItem(newItem(R.drawable.ic_buy_index, R.drawable.ic_buy_index, "首页"))
                .addItem(newItem(R.drawable.ic_buy_gouwu, R.drawable.ic_buy_gouwu, "购买"))
                .addItem(newItem(R.drawable.ic_buy_big, R.drawable.ic_buy_big, "大宗"))
                .addItem(newItem(R.drawable.ic_buy_fenlei, R.drawable.ic_buy_fenlei, "分类"))
                .addItem(newItem(R.drawable.ic_buy_yuding, R.drawable.ic_buy_yuding, "预订"))
                .addItem(newItem(R.drawable.ic_buy_jingpai, R.drawable.ic_buy_jingpai, "竞拍"))
                .addItem(newItem(R.drawable.ic_buy_cart, R.drawable.ic_buy_cart, "购物车"))
                .build();
        navigationController.setupWithViewPager(vp);
        navigationController.setSelect(1,true);
        initEvent();
    }

    //创建一个Item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        HomeItemView normalItemView = new HomeItemView(this);
        normalItemView.initialize(drawable, checkedDrawable, text);
        return normalItemView;
    }

    public int oldIndex;

    /**
     * set listeners
     */
    private void initEvent() {

        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {

            @Override
            public void onSelected(int index, int old) {
                if (index == 6 || index == 0) {
                    finish();
                }
                oldIndex = old;
            }

            @Override
            public void onRepeat(int index) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
