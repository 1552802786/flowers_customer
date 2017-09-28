package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;

import butterknife.BindView;

/**
 * Created by developerLzh on 2017/9/28 0028.
 */

public class RegisterActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_apply_supplier;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("商品详情");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
