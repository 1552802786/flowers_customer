package com.yuangee.flower.customer.activity;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
public class BuyMainActivity extends RxBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private VpAdapter adapter;

    @BindView(R.id.activity_main)
    RelativeLayout rootView;
    @BindView(R.id.navigation_bar)
    RadioGroup group;
    @BindView(R.id.vp)
    NoScrollViewPager vp;

    private ShoppingFragment shoppingFragment;
    private ReserveFragment reserveFragment;
    private FenleiFragment fenleiFragment;
    private ShoppingDZFragment dazongFragment;
    private JinPaiFragment jpFragment;
    List<Fragment> fragments = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_buy_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initData();
        group.check(R.id.tab_d);
    }

    private void initData() {
        shoppingFragment = new ShoppingFragment();
        dazongFragment = new ShoppingDZFragment();
        fenleiFragment = new FenleiFragment();
        reserveFragment = new ReserveFragment();
        jpFragment = new JinPaiFragment();

        fragments.add(shoppingFragment);
        fragments.add(dazongFragment);
        fragments.add(fenleiFragment);
        fragments.add(reserveFragment);
        fragments.add(jpFragment);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(adapter);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.tab_a:
                        finish();
                        break;
                    case R.id.tab_b:
                        vp.setCurrentItem(0);
                        break;
                    case R.id.tab_c:
                        vp.setCurrentItem(1);
                        break;
                    case R.id.tab_d:
                        vp.setCurrentItem(2);
                        break;
                    case R.id.tab_e:
                        vp.setCurrentItem(3);
                        break;
                    case R.id.tab_f:
                        vp.setCurrentItem(4);
                        break;
                    case R.id.tab_g:
                        finish();
                        break;
                }

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vp.setCurrentItem(getIntent().getIntExtra("index", 0));
            }
        }, 500);
    }

}
