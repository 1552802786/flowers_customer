package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by developerLzh on 2017/10/27 0027.
 */

public class SecAddressActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.address_recycler)
    PullLoadMoreRecyclerView recyclerView;

    @BindView(R.id.add_place)
    LinearLayout addPlace;

    @BindView(R.id.empty_layout)
    CustomEmptyView customEmptyView;

    @OnClick(R.id.add_place)
    void addPlace(){

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sec_address;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("个人中心");
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.i_manage){
                    ToastUtil.showMessage(SecAddressActivity.this,"i_manage");
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_addr, menu);
        return true;
    }
}
