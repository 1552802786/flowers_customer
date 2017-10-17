package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.fragment.MineFragment;
import com.yuangee.flower.customer.fragment.ShoppingCartFragment;
import com.yuangee.flower.customer.fragment.ToSpecifiedFragmentListener;
import com.yuangee.flower.customer.fragment.home.HomeFragment;
import com.yuangee.flower.customer.fragment.reserve.ReserveFragment;
import com.yuangee.flower.customer.fragment.shopping.ShoppingFragment;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends RxBaseActivity implements ToSpecifiedFragmentListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private VpAdapter adapter;

    // collections
    private SparseIntArray items;
    private List<Fragment> fragments;

    @BindView(R.id.vp)
    ViewPager vp;

    @BindView(R.id.bnve)
    BottomNavigationViewEx bnve;

    @BindView(R.id.search_frame)
    LinearLayout searchFrame;

    @BindView(R.id.edit_suggest)
    EditText editSuggest;

    @BindView(R.id.clear_edit)
    ImageView clearEdit;

    @BindView(R.id.cancel_action)
    TextView cancelAction;

    @BindView(R.id.search_suggest)
    RecyclerView searchResultSuggest;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initView();
        initData();
        initEvent();
    }

    @Override
    public void initToolBar() {

    }

    /**
     * change BottomNavigationViewEx style
     */
    private void initView() {
        bnve.enableShiftingMode(false);
        bnve.enableAnimation(true);
        bnve.enableItemShiftingMode(false);

        editSuggest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    ToastUtil.showMessage(MainActivity.this, "点击了键盘上的查询");
                    return true;
                }
                return false;
            }
        });
        editSuggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable) {
                    String s = editable.toString();
                    if (StringUtils.isEmpty(s)) {
                        clearEdit.setVisibility(View.GONE);
                    } else {
                        clearEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    clearEdit.setVisibility(View.GONE);
                }
            }
        });
        cancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFrame.setVisibility(View.GONE);
                PhoneUtil.hideKeyboard(MainActivity.this);
            }
        });
        clearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSuggest.setText("");
            }
        });
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
        homeFragment.setToSpecifiedFragmentListener(this);

        shoppingFragment = new ShoppingFragment();
        shoppingFragment.setToSpecifiedFragmentListener(this);

        shoppingCartFragment = new ShoppingCartFragment();
        shoppingCartFragment.setToSpecifiedFragmentListener(this);

        reserveFragment = new ReserveFragment();
        reserveFragment.setToSpecifiedFragmentListener(this);

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
                    searchFrame.setVisibility(View.GONE);
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

    /**
     * 0-4代表viewPager中的fragment -1代表显示searchFrame
     *
     * @param position
     */
    @Override
    public void toFragment(int position) {
        if (position == -1) {
            searchFrame.setVisibility(View.VISIBLE);
            editSuggest.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) editSuggest.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editSuggest, 0);
        } else {
            vp.setCurrentItem(position);
        }

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
        if (searchFrame.getVisibility() == View.VISIBLE) {
            searchFrame.setVisibility(View.GONE);
        } else {
            if (vp.getCurrentItem() == 1) {
                if (shoppingFragment.onBackPressed()) {
                    return;
                }
            }
            super.onBackPressed();
        }
    }

    /**
     * 查询所有种类
     */
    private void findAllGenre(){
        Observable<List<Genre>> observable = ApiManager.getInstance().api
                .findAllGenre()
                .map(new HttpResultFunc<List<Genre>>(MainActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(new MySubscriber<>(MainActivity.this, true, true, new NoErrSubscriberListener<List<Genre>>() {
            @Override
            public void onNext(List<Genre> genres) {

            }
        }));
    }

}
