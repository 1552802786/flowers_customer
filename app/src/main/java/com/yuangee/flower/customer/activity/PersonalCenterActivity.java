package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;

import butterknife.BindView;

/**
 * Created by developerLzh on 2017/8/22 0022.
 */

public class PersonalCenterActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        toolbar.setTitle("个人中心");
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void initToolBar() {

    }
}
