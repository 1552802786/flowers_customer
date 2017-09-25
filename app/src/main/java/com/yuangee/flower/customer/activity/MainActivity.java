package com.yuangee.flower.customer.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MenuItem;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.databinding.ActivityMainBinding;
import com.yuangee.flower.customer.fragment.MineFragment;
import com.yuangee.flower.customer.fragment.ShoppingCartFragment;
import com.yuangee.flower.customer.fragment.ToSpecifiedFragmentListener;
import com.yuangee.flower.customer.fragment.reserve.ReserveFragment;
import com.yuangee.flower.customer.fragment.shopping.ShoppingFragment;
import com.yuangee.flower.customer.fragment.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToSpecifiedFragmentListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private VpAdapter adapter;
    private ActivityMainBinding bind;

    // collections
    private SparseIntArray items;// used for change ViewPager selected item
    private List<Fragment> fragments;// used for ViewPager adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }

    /**
     * change BottomNavigationViewEx style
     */
    private void initView() {
        bind.bnve.enableShiftingMode(false);
        bind.bnve.enableAnimation(true);
        bind.bnve.enableItemShiftingMode(false);

    }

    /**
     * create fragments
     */
    HomeFragment homeFragment;
    ShoppingFragment shoppingFragment;
    ShoppingCartFragment shoppingCartFragment;
    ReserveFragment reserveFragment;
    MineFragment mineFragment;

    private void initData() {
        fragments = new ArrayList<>(5);
        items = new SparseIntArray(5);

        homeFragment = new HomeFragment();
        shoppingFragment = new ShoppingFragment();

        shoppingCartFragment = new ShoppingCartFragment();
        shoppingCartFragment.setToSpecifiedFragmentListener(this);

        reserveFragment = new ReserveFragment();
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
        bind.vp.setOffscreenPageLimit(5);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        bind.vp.setAdapter(adapter);
    }

    /**
     * set listeners
     */
    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item
        bind.bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = items.get(item.getItemId());
                if (previousPosition != position) {
                    // only set item when item changed
                    previousPosition = position;
                    Log.i(TAG, "-----bnve-------- previous item:" + bind.bnve.getCurrentItem() + " current item:" + position + " ------------------");
                    bind.vp.setCurrentItem(position);
                }
                return true;
            }
        });

        bind.vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "-----ViewPager-------- previous item:" + bind.bnve.getCurrentItem() + " current item:" + position + " ------------------");
                bind.bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void toFragment(int position) {
        bind.vp.setCurrentItem(position);
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
        if (bind.vp.getCurrentItem() == 1) {
            if (shoppingFragment.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    //    private void setBarTitle(int position) {
//        if (position == 0) {
//            actionBar.setTitle(getString(R.string.title_home));
//        } else if (position == 1) {
//            actionBar.setTitle(getString(R.string.title_buy));
//        } else if (position == 2) {
//            actionBar.setTitle(getString(R.string.title_shopping_cart));
//        } else if (position == 3) {
//            actionBar.setTitle(getString(R.string.title_reserve));
//        } else if (position == 4) {
//            actionBar.setTitle(getString(R.string.title_mine));
//        }
//    }
}
