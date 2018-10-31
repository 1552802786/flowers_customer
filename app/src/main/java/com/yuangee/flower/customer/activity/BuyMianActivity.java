package com.yuangee.flower.customer.activity;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.SparseIntArray;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.VpAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.fragment.FenleiFragment;
import com.yuangee.flower.customer.fragment.JinPaiFragment;
import com.yuangee.flower.customer.fragment.ShoppingCartFragment;
import com.yuangee.flower.customer.fragment.UserServiceFragment;
import com.yuangee.flower.customer.fragment.home.HomeFragment;
import com.yuangee.flower.customer.fragment.reserve.ReserveFragment;
import com.yuangee.flower.customer.fragment.shopping.ShoppingDZFragment;
import com.yuangee.flower.customer.fragment.shopping.ShoppingFragment;
import com.yuangee.flower.customer.widget.HomeItemView;
import com.yuangee.flower.customer.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;

/**
 * 作者：Rookie on 2018/10/8 11:12
 */
public class BuyMianActivity extends RxBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.activity_main)
    RelativeLayout rootView;
    @BindView(R.id.navigation_bar)
    RadioGroup group;

    private ShoppingFragment shoppingFragment;
    private ShoppingCartFragment shoppingCartFragment;
    private ReserveFragment reserveFragment;
    private FenleiFragment fenleiFragment;
    private ShoppingDZFragment dazongFragment;
    private JinPaiFragment jpFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_buy_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initData();
        group.check(R.id.tab_d);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (shoppingFragment != null) {
            transaction.hide(shoppingFragment);
        }
        if (reserveFragment != null) {
            transaction.hide(reserveFragment);
        }
        if (dazongFragment != null) {
            transaction.hide(dazongFragment);
        }
        if (fenleiFragment != null) {
            transaction.hide(fenleiFragment);
        }
        if (jpFragment != null) {
            transaction.hide(jpFragment);
        }
    }

    private void initData() {

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                hideFragments(transaction);
                switch (i) {
                    case R.id.tab_a:
                        break;
                    case R.id.tab_b:

                        if (shoppingFragment == null) {
                            shoppingFragment = new ShoppingFragment();
                            transaction.add(R.id.container, shoppingFragment);
                        } else {
                            // 如果SettingFragment不为空，则直接将它显示出来
                            transaction.show(shoppingFragment);
                        }
                        break;
                    case R.id.tab_c:
                        if (dazongFragment == null) {
                            dazongFragment = new ShoppingDZFragment();
                            transaction.add(R.id.container, dazongFragment);
                        } else {
                            // 如果SettingFragment不为空，则直接将它显示出来
                            transaction.show(dazongFragment);
                        }
                        break;
                    case R.id.tab_d:
                        if (fenleiFragment == null) {
                            fenleiFragment = new FenleiFragment();
                            transaction.add(R.id.container, fenleiFragment);
                        } else {
                            // 如果SettingFragment不为空，则直接将它显示出来
                            transaction.show(fenleiFragment);
                        }
                        break;
                    case R.id.tab_e:
                        if (reserveFragment == null) {
                            reserveFragment = new ReserveFragment();
                            transaction.add(R.id.container, reserveFragment);
                        } else {
                            // 如果SettingFragment不为空，则直接将它显示出来
                            transaction.show(reserveFragment);
                        }
                        break;
                    case R.id.tab_f:
                        if (jpFragment == null) {
                            jpFragment = new JinPaiFragment();
                            transaction.add(R.id.container, jpFragment);
                        } else {
                            // 如果SettingFragment不为空，则直接将它显示出来
                            transaction.show(jpFragment);
                        }
                        break;
                    case R.id.tab_g:

                        break;
                }
                transaction.commit();
            }
        });
        // set listener to change the current item of view pager when click bottom nav item
    }

    //创建一个Item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        HomeItemView normalItemView = new HomeItemView(this);
        normalItemView.initialize(drawable, checkedDrawable, text);
        return normalItemView;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
